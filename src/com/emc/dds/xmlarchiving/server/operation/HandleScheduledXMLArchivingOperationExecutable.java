/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.ibm.icu.text.SimpleDateFormat;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.query.interfaces.XhiveXQueryQueryIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class HandleScheduledXMLArchivingOperationExecutable extends
PersistenceOperationExecutable<HandleScheduledXMLArchivingOperation, Boolean> {

  private static final String DATE_PATTERN = "yyyy-MM-dd";

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

  @Override
  public Boolean run(Map<String, Session> sessionMap) throws DDSException {
    Session session = sessionMap.get(getOperation().getStoreAlias());
    XhiveSessionIf xhiveSession = (XhiveSessionIf)session.getSession();
    String date = DATE_FORMAT.format(new Date());
    Document input =
        xhiveSession.getTemporaryDOMImplementation().createDocument(null, "data", null);
    Element dateElem = input.createElementNS(null, "date");
    input.getDocumentElement().appendChild(dateElem);
    Text text = input.createTextNode(date);
    dateElem.appendChild(text);
    LogCenter.log("Start operation \"" + getOperation().getOperationName() + "\" time: " + date);
    boolean result = handleOperation(xhiveSession, input);
    return result ? Boolean.TRUE : Boolean.FALSE;
  }

  private boolean handleOperation(XhiveSessionIf xhiveSession, Document input) {
    XhiveLibraryIf rootLib = xhiveSession.getDatabase().getRoot();
    String applicationName = getApplication().getName();
    boolean readOnly = getOperation().isReadOnly();
    String operationName = getOperation().getOperationName();
    for (String type : OperationsUtil.getAllTypes(rootLib, applicationName)) {
      String xqueryStr = OperationsUtil.getXQuery(rootLib, applicationName, type, operationName);
      if (xqueryStr != null) {
        XhiveXQueryQueryIf xquery = OperationsUtil.createXQuery(xhiveSession, xqueryStr);
        xquery.setVariable("input", input.getDocumentElement());
        Iterator<XhiveXQueryValueIf> result = xquery.execute();
        boolean hasResult = result.hasNext();
        if (readOnly) {
          return hasResult;
        }
        while (result.hasNext()) {
          result.next();
        }
        return hasResult;
      }
    }
    return false;
  }
}
