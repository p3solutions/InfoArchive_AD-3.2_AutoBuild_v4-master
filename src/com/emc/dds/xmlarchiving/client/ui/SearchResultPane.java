/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.emc.dds.xmlarchiving.client.authorization.Role;
import com.emc.dds.xmlarchiving.client.configuration.ApplicationSettings;
import com.emc.dds.xmlarchiving.client.configuration.ContentViewSetting;
import com.emc.dds.xmlarchiving.client.configuration.OperationConfiguration;
import com.emc.dds.xmlarchiving.client.configuration.OperationField;
import com.emc.dds.xmlarchiving.client.data.StoredQueryDataSource;
import com.emc.dds.xmlarchiving.client.event.ApplicationEvent;
import com.emc.dds.xmlarchiving.client.i18n.Locale;
import com.emc.dds.xmlarchiving.client.p3.util.UISize;
import com.emc.dds.xmlarchiving.client.rpc.LDMService;
import com.emc.dds.xmlarchiving.client.rpc.LDMServiceAsync;
import com.emc.dds.xmlarchiving.client.ui.image.MainImageBundle;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXQueryValue;
import com.emc.documentum.xml.dds.gwt.client.ui.DDSXFormsPanel;
import com.emc.documentum.xml.dds.gwt.client.util.DDSURI;
import com.emc.documentum.xml.gwt.client.DataChangeListener;
import com.emc.documentum.xml.gwt.client.Dialog;
import com.emc.documentum.xml.gwt.client.FailureHandler;
import com.emc.documentum.xml.gwt.client.FailureHandler.FailureListener;
import com.emc.documentum.xml.gwt.client.SourcesDataChangeEvents;
import com.emc.documentum.xml.gwt.client.ui.Button;
import com.emc.documentum.xml.gwt.client.ui.DialogBox;
import com.emc.documentum.xml.gwt.client.ui.table.TableSelection;
import com.emc.documentum.xml.gwt.client.xml.DomUtils;
import com.emc.documentum.xml.gwt.client.xml.XMLParser;
import com.emc.documentum.xml.xforms.gwt.client.XFormsSubmissionHandler;
import com.emc.documentum.xml.xforms.gwt.client.model.XFormsSubmission;
import com.emc.documentum.xml.xproc.gwt.client.rpc.SerializablePipelineInput;
import com.emc.documentum.xml.xproc.gwt.client.rpc.SerializableQName;
import com.emc.documentum.xml.xproc.gwt.client.rpc.SerializableSource;
import com.emc.documentum.xml.xproc.gwt.client.ui.XProcFrame.DefaultXProcRequestSerializer;
import com.emc.documentum.xml.xproc.gwt.client.ui.XProcFrame.XProcRequestSerializer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pane to represent the results of a search in a list view. In addition to resize events, this pane
 * listens to search pane submit events. WARNING: This class is experimental and may change in
 * future DDS releases.
 */
public class SearchResultPane extends AbstractSearchResultPane {

  private final LDMServiceAsync ldmService = (LDMServiceAsync)GWT.create(LDMService.class);

  private class SearchResultButton extends Button implements ClickHandler, ChangeListener {

    private TableSelection selection;
    private OperationConfiguration operationConfiguration;

    SearchResultButton(TableSelection selection, OperationConfiguration operationConfiguration,
        String name) {
      this.selection = selection;
      this.operationConfiguration = operationConfiguration;
      selection.addChangeListener(this);
      addClickHandler(this);
      setEnabled(false);
      setText(operationConfiguration.getButtonLabel());
    }

    protected DialogBox createDialogBox() {
      if (operationConfiguration.isMessage()) {
        return new SearchResultMessageBox(selection, operationConfiguration);
      }
      if (!operationConfiguration.getXformURI().equals("xforms/")) {
        return new SearchResultDialogBox(selection, operationConfiguration);
      }
      return null;
    }

    @Override
    public void onClick(ClickEvent event) {
      final DialogBox dialogbox = createDialogBox();
      final String configurationId = operationConfiguration.getConfigurationId();
      if (dialogbox != null) {
        dialogbox.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            int left = (Window.getClientWidth() - offsetWidth) / 3;
            int top = (Window.getClientHeight() - offsetHeight) / 3;
            dialogbox.setPopupPosition(left, top);
          }
        });
      } else if (configurationId != null) {
        displaySelectionResult(operationConfiguration, getSelection(selection));
      }
    }

    @Override
    public void onChange(Widget sender) {
      setEnabled(selection.size() != 0);
    }
  }

  private class SearchResultDialogBox extends DialogBox implements
  AsyncCallback<List<SerializableXQueryValue>>, XFormsSubmissionHandler, FailureListener {

    private OperationConfiguration operationConfiguration;
    private String selectionStr;

    SearchResultDialogBox(TableSelection selection, OperationConfiguration operationConfiguration) {
      this.operationConfiguration = operationConfiguration;
      selectionStr = getSelection(selection);
      int size = selection.size();
      String title = replace(operationConfiguration.getDialogTitle(), "$count", "" + size);
      setText(title);
      DDSURI ddsURI = new DDSURI(operationConfiguration.getXformURI());
      ddsURI.setAttribute(DDSURI.ATTRIBUTE_DOMAIN, DDSURI.DOMAIN_RESOURCE);
      DDSXFormsPanel xformsPanel = new DDSXFormsPanel(ddsURI.toString());
      Map<String, OperationField> opFields = operationConfiguration.getFields();
      xformsPanel.replace(null, "//record", getSelection(selection), false);
      xformsPanel.setSubmissionHandler(this);
      add(xformsPanel);
      FailureHandler.addFailureListener(this);
    }

    @Override
    public void onFailure(Throwable caught) {
      FailureHandler.handle(this, caught);
    }

    @Override
    public void onFailure(AsyncCallback sender, Throwable caught) {
      if (caught != null) {
        if (caught.getMessage() != null && caught.getMessage().equals("Session timed out")) {
          hide();
        } else {
          FailureHandler.handle(sender, caught);
        }
      }
    }

    @Override
    public void onSuccess(List<SerializableXQueryValue> result) {
      //
    }

    @Override
    public boolean onSubmit(XFormsSubmission submission, String submissionBody) {
      hide();
      String topElem = "<data>";
      int index = submissionBody.indexOf(topElem) + topElem.length();
      StringBuilder input = new StringBuilder();
      input.append(submissionBody.substring(0, index));
      input.append(submissionBody.substring(index));
      String user = getApplicationSettings().getUserName();
      executeOperation(input.toString(), operationConfiguration, user,
          operationConfiguration.getConfigurationId());
      submission.onSubmitDone("", 200, null, null);
      return false;
    }
  }

  private class SearchResultMessageBox extends DialogBox implements
  AsyncCallback<List<SerializableXQueryValue>> {

    private String selected;
    private OperationConfiguration operationConfiguration;

    SearchResultMessageBox(TableSelection selection, OperationConfiguration operationConfiguration) {
      this.operationConfiguration = operationConfiguration;
      selected = getSelection(selection);
      VerticalPanel verticalPanel = new VerticalPanel();
      verticalPanel.add(new Label(replace(operationConfiguration.getMessage(), "$count", ""
          + selection.size())));
      Button okButton = new Button(Locale.getLabels().ok());
      okButton.addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          submit();
        }
      });
      okButton.addKeyDownHandler(new KeyDownHandler() {

        @Override
        public void onKeyDown(KeyDownEvent event) {
          int nativeKeyCode = event.getNativeKeyCode();
          if (nativeKeyCode == KeyCodes.KEY_ENTER || nativeKeyCode == ' ') {
            submit();
          }
        }
      });
      addStyleName("core-Dialog-alert");
      okButton.addStyleName("core-Dialog-alert-ok");
      verticalPanel.add(okButton);
      add(verticalPanel);
    }

    @Override
    public void onFailure(Throwable caught) {
      FailureHandler.handle(this, caught);
    }

    @Override
    public void onSuccess(List<SerializableXQueryValue> result) {
      //
    }

    private String getResult() {
      StringBuffer sb = new StringBuffer();
      sb.append("<data>");
      sb.append(selected);
      sb.append("</data>");
      return sb.toString();
    }

    public boolean submit() {
      hide();
      String user = getApplicationSettings().getUserName();
      executeOperation(getResult(), operationConfiguration, user,
          operationConfiguration.getConfigurationId());
      return false;
    }
  }

  private class SelectAllButton extends Button implements ClickHandler, DataChangeListener {

    private TableSelection selection;
    private StoredQueryDataSource dataSource;

    SelectAllButton(TableSelection selection, StoredQueryDataSource dataSource) {
      this.selection = selection;
      this.dataSource = (StoredQueryDataSource) dataSource.cloneDataSource();
      this.dataSource.addDataChangeListener(this);
      setText("Select all");
      addClickHandler(this);
    }

    @Override
    public void onClick(ClickEvent event) {
      dataSource.setRange(-1, -1);
    }

    @Override
    public void onDataChange(SourcesDataChangeEvents sender) {
      List<SerializableXQueryValue> list = dataSource.getList();
      if (list.size() != 0) {
        SerializableXQueryValue rootNodeValue = list.get(0);
        Document doc = XMLParser.parse(rootNodeValue.asString());
        Element childElement = DomUtils.getFirstChildElement(doc.getDocumentElement());
        Collection<Object> newSelection = new ArrayList<Object>();
        while (childElement != null) {
          newSelection.add(childElement.toString());
          childElement = DomUtils.getNextSiblingElement(childElement);
        }
        selection.add(newSelection.toArray());
      }
    }
  }

  private class SelectNoneButton extends Button implements ClickHandler {

    private TableSelection selection;

    SelectNoneButton(TableSelection selection) {
      this.selection = selection;
      setText("Select none");
      addClickHandler(this);
    }

    @Override
    public void onClick(ClickEvent event) {
      selection.remove(1, selection.getOwner().getRowCount());
      selection.clear();
    }
  }

  // EXCEL the export button
  private class ExportResultSetButton extends Button implements ClickHandler {

    /**
     * Used to temporarily encode '&' in content of the encoded URL
     */
    private static final String AMP_FLAG = "-_AMP_FLAG_-";

    public ExportResultSetButton() {
      setText("Export");
      addClickHandler(this);
    }

    @Override
    public void onClick(ClickEvent event) {
      StoredQueryDataSource src = (StoredQueryDataSource) getDataSource().cloneDataSource();
      String xQuery = escapeXQuery(embedExternalVariablesXQuery(src));
      runProxyExport(xQuery);
    }

    private String embedExternalVariablesXQuery(StoredQueryDataSource src) {
      String DECLARE_FIRST = "declare variable $first as xs:string external;";
      String DECLARE_LAST = "declare variable $last as xs:string external;";
      String DECLARE_USER = "declare variable $currentuser as xs:string external;";
      String DECLARE_INPUT = "declare variable $input as xs:string external;";
      String DECLARE_RESTRICTIONS = "declare variable $restrictions as xs:string external;";

      String xQuery = src.getXQuery(null);

      xQuery = xQuery.replace(DECLARE_FIRST, "let $first := '1'");
      xQuery = xQuery.replace(DECLARE_LAST, "let $last := '-1'");
      xQuery = xQuery.replace(DECLARE_USER, "let $currentuser := '" + src.getCurrentUserName() + '\'');
      //xQuery = xQuery.replace(DECLARE_INPUT, "let $input := '" + src.getFormInput() + '\'');
      xQuery = xQuery.replace(DECLARE_RESTRICTIONS, "let $restrictions := '" + src.getRestrictions() + '\'');
      return xQuery;
    }

    private String escapeXQuery(String s) {
      return s.replaceAll("&", AMP_FLAG);
    }

    private void runProxyExport(String fullXQuery) {
      String proxyXQueryURI = "dds://DOMAIN=resource/xqueries/utility/wrapperEval.xml";
      String styleSheetURI = "dds://DOMAIN=resource/xslt/exportExcelXML.xsl";
      String pipelineURI = "dds://DOMAIN=resource/xproc/displayxqueryresult.xpl";
      String contentType = "application/vnd.ms-excel";
      boolean useExternalOutput = false;

      Map<String, String> headers = new HashMap<String, String>(2);
      headers.put("Content-Disposition", "attachment; filename=ExcelExport.xls");

      SerializablePipelineInput input = new SerializablePipelineInput();
      input.addInput("source", new SerializableSource("dds://DOMAIN=resource/xqueries/"));
      input.addInput("query", new SerializableSource(proxyXQueryURI));
      input.addInput("stylesheet", new SerializableSource(styleSheetURI));
      input.addParameter("parameters", new SerializableQName("xquery"), fullXQuery);

      XProcRequestSerializer requestSerializer = new DefaultXProcRequestSerializer();
      String url =
          requestSerializer.getRequestString(pipelineURI, input, contentType, useExternalOutput,
              headers);

      submitPostForm(url);
    }

    /**
     * The trick to this is that the content to POST is generated from a GET URL.
     *
     * The content can have the '&' character which is
     * escaped as a AMP_FLAG and unescaped when the URL is parsed into the form.  This
     * is due to URLs using & as the parameter delimiter.
     *
     * @param theGETURL
     */
    private void submitPostForm(String theGETURL) {
      final int qIndex = theGETURL.indexOf('?');
      String baseURL = theGETURL.substring(0, qIndex);
      String[] args = theGETURL.substring(qIndex + 1).split("&");

      FormPanel form = new FormPanel();
      form.setAction(baseURL);
      form.setMethod(FormPanel.METHOD_POST);

      // Create a panel to hold all of the form widgets.
      VerticalPanel panel = new VerticalPanel();
      form.setWidget(panel);
      for (String arg : args) {
        arg = arg.replaceAll(AMP_FLAG, "&");
        final int eIndex = arg.indexOf('=');
        final String name = arg.substring(0, eIndex);
        final String value = arg.substring(eIndex + 1);
        panel.add(new Hidden(name, value));
      }

      RootPanel.get().add(form);

      form.submit();
    }
  }

  private Panel searchResultPanel;
  
  private long starttime;

  private HorizontalPanel hPanel = new HorizontalPanel();
  
  public static final String CORE_HEIGHT = UISize.getInteriorResultsHeightStr();

  private LDMBorderDecorator decorator;

  public SearchResultPane(ApplicationSettings applicationSettings, ContentViewPane contentViewPane,
      int maxListCount, int truncateLabels) {
    super(applicationSettings, contentViewPane, maxListCount, truncateLabels);
  }

  @Override
  public void init() {
    super.init();
    searchResultPanel = getTopPanel();
    searchResultPanel.addStyleName(getPaneStyle());
    decorator = new LDMBorderDecorator("Search Results", searchResultPanel);
    initWidget(decorator);
    setLoaded(true);
    searchResultPanel.setHeight(CORE_HEIGHT);
  }

  @Override
  public int getPaneType() {
    return SEARCH_RESULT_PANE;
  }

  @Override
  protected int loadTable(List<SerializableXQueryValue> results) {
    long totaltime = System.currentTimeMillis() - starttime;
    int numItems = super.loadTable(results);
    if (decorator != null) {
      decorator.setTopText(HEADER_TEXT + " (" + numItems + ") (time: " + totaltime + " ms)", false);
    }
    return numItems;
  }

  protected void clearResults() {
	super.clearResults();
	hPanel.clear();
	this.decorator.setTopText(HEADER_TEXT, false);
  }
  
  @Override
  public void handle(ApplicationEvent event) {
    super.handle(event);
    if (event.getType() != ApplicationEvent.SEARCH_SUBMIT_EVENT){
		
		clearResults();
		showEmptyContentView();
		hPanel.clear();
	}
    switch (event.getType()) {
      case ApplicationEvent.SEARCH_SUBMIT_EVENT:
      case ApplicationEvent.NESTED_SEARCH_SUBMIT_EVENT:
        starttime = System.currentTimeMillis();
        decorator.setTopText(HEADER_TEXT, false);
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hPanel.setSize("100%", "100%");
        Role role = getApplicationSettings().getRole();

        List<String> operations = new ArrayList<String>();

        for (String operation : getApplicationSettings().getState().getCurrentSearchSetting()
            .getSearchConfiguration().getOperations()) {
          if (role.hasOperationAuthorization(operation)) {
            operations.add(operation);
          }
        }

        if (role.hasOperationAuthorization("export")) {
          hPanel.add(new ExportResultSetButton());
        }
        hPanel.add(getPagingBar());

        if (!operations.isEmpty()) {
          FlowPanel flowPanel = new FlowPanel();

          for (String operation : operations) {
            flowPanel.add(createSearchResultButton(operation));
          }

          hPanel.add(flowPanel);
          hPanel.setCellHorizontalAlignment(flowPanel, HasHorizontalAlignment.ALIGN_RIGHT);
        }

        decorator.setFooterWidget(hPanel);
        break;
    }
  }

  private SearchResultButton createSearchResultButton(String operation) {
    OperationConfiguration operationConfiguration =
        getApplicationSettings().getOperationConfigurations().get(operation);
    if (operationConfiguration != null) {
      return new SearchResultButton(getTableSelection(), operationConfiguration, operation);
    } else {
      Dialog.alert(Locale.getMessages().unknownOperation(operation), MainImageBundle.INSTANCE
          .error48().createImage());
      return null;
    }
  }

  private void executeOperation(String input, OperationConfiguration operationConfiguration,
      String user, String configurationId) {

    List<String> remainingQueries = operationConfiguration.getXqueries();
    ldmService.handleXMLArchivingOperation(input, remainingQueries.get(0), user,
        new HandleQueryCallback(input, user, remainingQueries.subList(1, remainingQueries.size()),
            operationConfiguration));
  }

  private void displaySelectionResult(OperationConfiguration operationConfiguration,
      String selection) {
    final String configurationId = operationConfiguration.getConfigurationId();
    ContentViewSetting contentViewSetting =
        getState().getApplicationSettings().getContentViewSettings().get(configurationId);
    String type = getSelectionType(selection);
    if (type == null) {
      type = getCurrentSetting().getId();
    }
    Document doc = XMLParser.parse(selection);
    getContentViewPane().displayBusinessObject("", type, doc.getDocumentElement(),
        contentViewSetting);
  }

  private String getSelectionType(String selection) {
    Document doc = XMLParser.parse(selection);
    NodeList result = doc.getElementsByTagName("result");
    for (int k = 0; k < result.getLength(); k++) {
      Element elem = (Element)result.item(k);
      String type = elem.getAttribute("type");
      if (!"".equals(type)) {
        return elem.getAttribute("type");
      }
    }
    return null;
  }

  private String getSelection(TableSelection selection) {
    StringBuilder stringBuilder = new StringBuilder("<selection>");
    for (Object object : selection) {
      stringBuilder.append(object.toString());
    }
    stringBuilder.append("</selection>");
    return stringBuilder.toString();
  }

  @Override
  protected int getHeaderHeight() {
    return 0;
  }

  private String replace(String str, String original, String replacement) {
    if (str != null) {
      String result = str.replace(original, replacement);
      return result;
    }
    return null;
  }

  private class HandleQueryCallback implements AsyncCallback<Boolean> {

    private String input;
    private String user;
    private OperationConfiguration operationConfiguration;

    private List<String> remainingQueries;

    public HandleQueryCallback(String input, String user, List<String> remainingQueries,
        OperationConfiguration operationConfiguration) {

      this.input = input;
      this.user = user;
      this.remainingQueries = remainingQueries;
      this.operationConfiguration = operationConfiguration;
    }

    @Override
    public void onFailure(Throwable caught) {
      FailureHandler.handle(this, caught);
    }

    @Override
    public void onSuccess(Boolean success) {

      if (remainingQueries.size() > 0) {
        ldmService.handleXMLArchivingOperation(
            input,
            remainingQueries.get(0),
            user,
            new HandleQueryCallback(input, user, remainingQueries.subList(1,
                remainingQueries.size()), operationConfiguration));
      } else {
        if (operationConfiguration.getConfigurationId() != null) {
          displaySelectionResult(operationConfiguration, input);
        } else {
          resubmit();
        }
      }
    }
  }
}
