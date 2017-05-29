package com.emc.dds.xmlarchiving.client.p3.reporting;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.emc.dds.xmlarchiving.client.configuration.SearchConfiguration;
import com.emc.dds.xmlarchiving.client.configuration.SearchSetting;
import com.emc.dds.xmlarchiving.client.data.StoredQueryDataSource;
import com.emc.dds.xmlarchiving.client.p3.util.CharacterUtil;
import com.emc.dds.xmlarchiving.client.p3.util.UUID;
import com.emc.dds.xmlarchiving.client.ui.LDMUIHandler;
import com.emc.documentum.xml.dds.gwt.client.rpc.DDSServices;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXQueryValue;
import com.emc.documentum.xml.gwt.client.Dialog;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Malik
 *
 */
public class PersistentReportDataSource extends StoredQueryDataSource {
    
    //private static final String QUEUE = "/DATA/changeme/Reports/Queue";
	
    private static final String TICKET_PREFIX = "RT";
	private static final String APP = "changeme";
	private static final String QUEUE = "/DATA/" + APP + "/Reports/Queue";
    
    /**
     * During construction of the parent StoredQueryDataSource, the raw XQuery 
     * is decorated by various code injection mechanisms.  The decorated XQuery 
     * is then saved to xDB as a report ticket, and the superclass StoredQueryDataSource 
     * XQuery is replaced with a simple query to inform the user of the ticket.
     * 
     * @param rawXQuery
     * @param fields
     * @param searchSetting
     * @param userName
     * @param restrictions
     * @param defaultEmail
     */
    public PersistentReportDataSource(String rawXQuery, Map<String, String> fields, 
            SearchSetting searchSetting, String userName, String restrictions, SearchConfiguration searchCfg,
            AsyncCallback<?> failureHandler, String siteminderEmail) {
        super(rawXQuery, fields, searchSetting, userName, restrictions, failureHandler);
        
        final String defaultEmail = "default@email.com";
        final String reportOutputType = searchCfg.getReportOutputType();
        
        for (String x : fields.keySet()){
        	System.out.println(x + " " + fields.get(x));
        }
        
        boolean spaceCheckFlag = true;
        Iterator<Entry<String, String>> it = fields.entrySet().iterator();
        StringBuilder searchFields = new StringBuilder("");
        while (it.hasNext()){
            Entry<String, String> x = it.next();
            if(searchFields.toString().equals(""))
                  searchFields.append("\n").append(x);
            else
                  searchFields.append(";\n").append(x);
            
            if(spaceCheckFlag){
                  String y = x.getValue();
                  if(y.length() != 0){
                        if(y.trim().length() == 0)
                              spaceCheckFlag = false;
                  }
            }
            fields.put(x.getKey(),x.getValue().trim());
        }
        
        if(!spaceCheckFlag){
            Dialog.alert("One/more fields has only spaces in the input text. Please recheck");
            setXQuery(createUserXQuery("One/more fields has only spaces in the input text. Please recheck"));
            return;
        }
        
        
        String decoratedXQuery = getXQuery(null);
        
        String emailFromForms = fields.get("email");
        String email = (emailFromForms == null || emailFromForms.length() == 0) ? ((siteminderEmail == null || siteminderEmail.length() == 0)?defaultEmail:siteminderEmail): emailFromForms;
        
        
        Date date = new Date();
        long milli = date.getTime();
        String dateTime = date.toString();
        String ticketName = createTicketName(userName, milli);
        String reportName = searchSetting.getName();
        String id =  TICKET_PREFIX + UUID.uuid(10, 10);
		String ticketContent = createContent(reportName, reportOutputType, userName, date, email, decoratedXQuery, searchFields, id);
        String fullTicketXQuery = createTicketGenerationXQuery(ticketName, ticketContent);
        
        registerTicket(ticketName, fullTicketXQuery);
        
        String userMessage = "Report ticket \""+id+"\" created " + dateTime;
        setXQuery(createUserXQuery(userMessage));
    }
    private String createUserXQuery(String message) {
		message = CharacterUtil.escapeXMLAttribute(message);
		String query = "<results total=\"1\" searchResultItems=\"message\" nestedSearches=\"\"> "
				+ "<result message=\""
				+ message
				+ "\" type=\"emptyScreen\"/>"
				+ "</results>";
		return query;
	}
   
    
    /**
     * Has side-effect of starting the registration of the report ticket with xDB.
     * 
     * @param UserName
     * @return The ticket message for the user, which is the ticket ID.
     */
    private void registerTicket(String ticketName, String fullTicketXQuery) {
        DDSServices.getXQueryService().execute(null, fullTicketXQuery, false, 
                new AsyncCallback<List<SerializableXQueryValue>>() {
            @Override
            public void onSuccess(List<SerializableXQueryValue> result) {
                // Good!  Queue does not fetch information so no need to use result
            }
            @Override
            public void onFailure(Throwable t) {
                LDMUIHandler.displayFriendlyException(t, "Unable to create report ticket");
            }
        });
    }
    
    private static String createTicketGenerationXQuery(String ticketName, String ticketContent) {
        return 
            "let $doc as document-node() := document {\n" + 
            ticketContent + 
            "}\n" + 
            "return xhive:insert-document('" + QUEUE + "/" + ticketName + "', $doc)";
    }
    
//    private String createTicketName(String UserName, String dateTime) {
//        StringBuilder id = new StringBuilder();
//        id.append("Ticket_");
//        id.append(UserName);
//        id.append('_');
//        id.append(dateTime);
//        return id.toString().replaceAll("\\s+", "_");
//    }
    
    private String createTicketName(String UserName, long milliseconds) {
        StringBuilder id = new StringBuilder();
        id.append("Ticket_");
        id.append(UserName);
        id.append('_');
        id.append(milliseconds);
        id.append(".xml");
        return id.toString().replaceAll("\\s+", "_");
    }
    
    private String createContent(String reportName, String reportOutputType, String userName, Date date, 
			String email, String decoratedXQuery, StringBuilder searchFields, String id)  {
				reportName = (reportName == null) ? "" : reportName;
				userName = (userName == null) ? "" : userName;
				// String dateTime = date.toString();
				long milliseconds = date.getTime();
				email = (email == null) ? "" : email;
				decoratedXQuery = (decoratedXQuery == null) ? "" : decoratedXQuery;
				
				int month = (date.getMonth() + 1);
				int year = (date.getYear() + 1900);
				int day = date.getDate();
				
				int hour = date.getHours();
		        int min = date.getMinutes();
		        int sec = date.getSeconds()>59?59:date.getSeconds();
			
		        String dateTime = day + " " + getMonth(month) + ", " + year + " " + getHour(hour) + ":" + (min<10?"0":"") + min  + ":" + (sec<10?"0":"") + sec + " " + ((hour < 12)?"AM":"PM");
		        
				return
					"<report>\r\n" + 
					"  <id>" + id + "</id>\n" + 
					"  <name>" + reportName + "</name>\n" + 
					"  <username>" + userName + "</username>\n" + 
					"  <datetime>" + dateTime + "</datetime>\n" + 
					"  <CCYYMMDD>" +  year + (month<10?"0":"") + month + (day<10?"0":"") + day + "</CCYYMMDD>\n" + 
					"  <epoch1970>" + milliseconds + "</epoch1970>\n" + 
					"  <email>" + email + "</email>\n" + 
					"  <type>" + reportOutputType + "</type>\n" + 
					"  <searchField>" + searchFields + "</searchField>\n" +
					"  <xquery>\n" + 
					"<![CDATA[" + 
					decoratedXQuery + 
					"]]>\n" + 
					"</xquery>\n" + 
					"</report>\n";
			}
    
    private String getHour(int hour) {
        if(hour == 0 || hour ==12)
              return "12";
        else{
              int m_hour = hour%12;
              if(m_hour < 10)
                    return "0"+m_hour;
              else
                    return Integer.toString(m_hour);
        }
  }

  private String getMonth(int month) {
        switch(month){
        case 1:
              return "Jan";    
        case 2:
              return "Feb";    
        case 3:
              return "Mar";    
        case 4:
              return "Apr";    
        case 5:
              return "May";    
        case 6:
              return "Jun";    
        case 7:
              return "Jul";    
        case 8:
              return "Aug";    
        case 9:
              return "Sep";    
        case 10:
              return "Oct";    
        case 11:
              return "Nov";    
        case 12:
              return "Dec";
        default:
              return "";
        }
  }


}

