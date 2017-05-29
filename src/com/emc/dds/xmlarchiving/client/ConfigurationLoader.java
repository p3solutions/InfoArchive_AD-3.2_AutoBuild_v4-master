/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.emc.dds.xmlarchiving.client.configuration.ApplicationSettings;
import com.emc.dds.xmlarchiving.client.configuration.ContentViewSetting;
import com.emc.dds.xmlarchiving.client.configuration.Hierarchy;
import com.emc.dds.xmlarchiving.client.configuration.NestedSearch;
import com.emc.dds.xmlarchiving.client.configuration.NodeSetting;
import com.emc.dds.xmlarchiving.client.configuration.OperationConfiguration;
import com.emc.dds.xmlarchiving.client.configuration.OperationConfigurations;
import com.emc.dds.xmlarchiving.client.configuration.OperationField;
import com.emc.dds.xmlarchiving.client.configuration.Report;
import com.emc.dds.xmlarchiving.client.configuration.SearchConfiguration;
import com.emc.dds.xmlarchiving.client.configuration.SearchConfigurations;
import com.emc.dds.xmlarchiving.client.configuration.SearchField;
import com.emc.dds.xmlarchiving.client.configuration.SearchResultItem;
import com.emc.dds.xmlarchiving.client.configuration.SearchSetting;
import com.emc.dds.xmlarchiving.client.configuration.SearchSettings;
import com.emc.dds.xmlarchiving.client.i18n.Locale;
import com.emc.dds.xmlarchiving.client.p3.util.UISize;
import com.emc.dds.xmlarchiving.client.ui.AbstractSearchResultPane;
import com.emc.dds.xmlarchiving.client.ui.ContentPane;
import com.emc.dds.xmlarchiving.client.ui.ContentViewPane;
import com.emc.dds.xmlarchiving.client.ui.DataSetsTreePane;
import com.emc.dds.xmlarchiving.client.ui.FooterPane;
import com.emc.dds.xmlarchiving.client.ui.HeaderPane;
import com.emc.dds.xmlarchiving.client.ui.LogoutPane;
import com.emc.dds.xmlarchiving.client.ui.MainPane;
import com.emc.dds.xmlarchiving.client.ui.MenuPane;
import com.emc.dds.xmlarchiving.client.ui.Pane;
import com.emc.dds.xmlarchiving.client.ui.SearchPane;
import com.emc.dds.xmlarchiving.client.ui.SearchResultPane;
import com.emc.documentum.xml.gwt.client.xml.XMLParser;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationLoader {

  private static final String VERTICAL_PANEL = "verticalPanel";
  private static final String HORIZONTAL_PANEL = "horizontalPanel";

  private final Map<String, Pane> panes = new LinkedHashMap<String, Pane>();

  private ApplicationSettings applicationSettings;

  private final String contentConfiguration;

  private final String paneConfiguration;

  private MainPane mainPane;

  private Main main;

  private LayoutPanel outerPane;
  
  private boolean uageDataset = false;
  
  public ConfigurationLoader(List<String> configuration) {
    contentConfiguration = configuration.get(0);
    paneConfiguration = configuration.get(1);
  }

  public Panel load(Main mainToLoad) {
    main = mainToLoad;
    applicationSettings = mainToLoad.getApplicationSettings2();
    loadContentConfiguration();
    loadPaneConfiguration();
    ScrollPanel scrollPanel = new ScrollPanel(outerPane);
    outerPane.setHeight(uageDataset?UISize.getEntireHeightWithDSStr():UISize.getEntireHeightStr()); // 1100
    RootLayoutPanel.get().add(scrollPanel);
    outerPane.add(mainPane);
    outerPane.setWidgetTopBottom(mainPane, 44, Unit.PX, 30, Unit.PX);
    mainPane.init();
    return scrollPanel;
  }

  private void loadContentConfiguration() {
    if (contentConfiguration == null || contentConfiguration.length() == 0) {
      throw new RuntimeException("Content configuration not found");
    }
    Element docElem = XMLParser.parse(contentConfiguration).getDocumentElement();
    loadSearchConfigurations(docElem);
    loadTransformationConfigurations(docElem);
    loadOperationConfigurations(docElem);
    loadHierarchy(docElem);
    applicationSettings.init();
  }

  private String getClassName(String name) {
    char firstChar = name.charAt(0);
    StringBuffer sb = new StringBuffer();
    sb.append(Character.toUpperCase(firstChar));
    sb.append(name.substring(1));
    return sb.toString();
  }

  private int getPaneType(String name) {
    int paneType = Pane.getType(getClassName(name));
    if (paneType == -1) {
      throw new RuntimeException("Unknown class type: " + name);
    }
    return paneType;
  }

  private Panel getPanel(Node node) {
    Panel panel = null;
    if (VERTICAL_PANEL.equals(node.getNodeName())) {
      panel = new VerticalPanel();

    } else if (HORIZONTAL_PANEL.equals(node.getNodeName())) {
      panel = new HorizontalPanel();
    }
    return panel;
  }

  private void loadPaneConfiguration() {
    if (paneConfiguration == null || paneConfiguration.length() == 0) {
      throw new RuntimeException("Pane configuration not found");
    }
    Element docElem = XMLParser.parse(paneConfiguration).getDocumentElement();

    outerPane = new LayoutPanel();
    mainPane = new MainPane();
    Node node = docElem.getFirstChild();
    Panel panel = null;

    while (node != null) {
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        panel = getPanel(node);
        if (panel != null) {
          Element element = (Element)node;
          loadChildPanes(panel, element);
          break;
        }
      }
      node = node.getNextSibling();
    }
    if (panel == null) {
      throw new RuntimeException("Missing top panel");
    }
    mainPane.setPanes(panes);
    mainPane.init(panel);
    // this.mainPane.setWidth("100%");
  }

  private void loadChildPanes(Panel panel, Element panelElement) {
    Node child = panelElement.getFirstChild();
    while (child != null) {
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        Panel childPanel = getPanel(child);
        if (childPanel != null) {
          // panel.add(childPanel);
          loadChildPanes(childPanel, (Element)child);
        } else {
          int paneType = getPaneType(child.getNodeName());
          if (paneType != -1) {
            createPane(paneType, (Element)child);
          } else {
            throw new RuntimeException("Unknown pane type: " + child.getNodeName());
          }
        }
      }
      child = child.getNextSibling();
    }
  }

  private Pane createPane(int paneType, Element element) {
    Pane pane = null;
    switch (paneType) {
      case Pane.FOOTER_PANE:
        pane = new FooterPane();
        outerPane.add(pane);
        outerPane.setWidgetBottomHeight(pane, 0, Unit.PX, 30, Unit.PX);
        break;
      case Pane.HEADER_PANE:
        pane = new HeaderPane();
        Widget widget = pane;
        if (applicationSettings.isUserServiceConfigured()) {
          DockLayoutPanel topPanel = new DockLayoutPanel(Unit.PCT);
          widget = topPanel;
          topPanel.addWest(pane, 80);
          LogoutPane logoutPane = new LogoutPane(main);
          topPanel.add(logoutPane);
        }
        outerPane.add(widget);
        outerPane.setWidgetTopHeight(widget, 0, Unit.PX, 44, Unit.PX);
        break;
      case Pane.SEARCH_PANE:
        pane = new SearchPane(applicationSettings);
        mainPane.addNorth(pane, UISize.getSearchHeightInt());
        break;
      case Pane.SEARCH_RESULT_PANE:
        SplitLayoutPanel searchResultPanel = new SplitLayoutPanel();
        int maxListCount = getValueAsInt(element.getAttribute("maxListCount"));
        int truncateLabels = getValueAsInt(element.getAttribute("truncateLabels"));
        // String widthValue = element.getAttribute("width");
        // int percentageWidth = "".equals(widthValue) ? 50 :
// getPercentageValue(element.getAttribute("width"));
        ContentViewPane viewPane =
            new ContentViewPane(applicationSettings, "Record Details",
                ContentViewPane.RECORD_DETAILS_TYPE);
        pane = new SearchResultPane(applicationSettings, viewPane, maxListCount, truncateLabels);
        ((AbstractSearchResultPane)pane).init();
        searchResultPanel.addEast(viewPane, UISize.getInteriorResultsHeightInt());
        searchResultPanel.add(pane);
        mainPane.add(searchResultPanel);
        break;
      case Pane.CONTENT_TREE_PANE:
        break;
      case Pane.CONTENT_VIEW_PANE:
        pane =
        new ContentViewPane(applicationSettings, Locale.getLabels().entityInfo(),
            ContentViewPane.TICKET_INFO_TYPE);
        break;
      case Pane.DATA_SET_LIST_PANE:
    	uageDataset= true;
        SplitLayoutPanel dataSetPanel = new SplitLayoutPanel();
        ContentViewPane ticketInfoPane =
            new ContentViewPane(applicationSettings, Locale.getLabels().entityInfo(),
                ContentViewPane.TICKET_INFO_TYPE);
        pane = new DataSetsTreePane(applicationSettings);
        ((DataSetsTreePane)pane).init();
        String showTicketInfo = element.getAttribute("showTicketInfo");
        if (!"false".equalsIgnoreCase(showTicketInfo)) {
          dataSetPanel.addEast(ticketInfoPane, 300);
        }
        dataSetPanel.add(pane);
        mainPane.addNorth(dataSetPanel, UISize.getDetaSetHeightInt());
        break;
      case Pane.MENU_PANE:
    	  pane = new MenuPane(this.applicationSettings, false, true);
    	  /* LayoutPanel menuPanel = new LayoutPanel();
       		 menuPanel.add(pane);
       		 this.outerPane.add(menuPanel);
       		 this.outerPane.setWidgetTopHeight(pane, 0, Unit.PX, 20, Unit.PX);*/
    	  this.mainPane.addNorth(pane, UISize.getMenuHeightInt());
    	  break;
    }
    if (pane != null) {
        panes.put(pane.getPaneName(), pane);
        pane.addStyleName(pane.getPaneStyle());
        if (pane instanceof ContentPane) {
            ((ContentPane)pane).addListener(mainPane);
            mainPane.addListener((ContentPane)pane);
        }
    }
    return pane;
  }

  private void loadSearchConfigurations(Element elem) {

    SearchConfigurations searchCfgs = new SearchConfigurations();
    for (Element searchCfgEl : getDescElems(elem, "searchConfiguration")) {
      searchCfgs.put(searchCfgEl.getAttribute("id"), loadSearchConfiguration(searchCfgEl));
    }
    applicationSettings.setSearchConfigurations(searchCfgs);
  }

  private void loadOperationConfigurations(Element elem) {

    OperationConfigurations operationCfgs = new OperationConfigurations();
    for (Element operationCfgEl : getDescElems(elem, "operationConfiguration")) {
      operationCfgs.put(operationCfgEl.getAttribute("id"),
          loadOperationConfiguration(operationCfgEl));
    }
    applicationSettings.setOperationConfigurations(operationCfgs);
  }

  private void loadTransformationConfigurations(Element elem) {

    Map<String, ContentViewSetting> contentViewSettings = new HashMap<String, ContentViewSetting>();
    for (Element cViewEl : getDescElems(elem, "transformationConfiguration")) {
      contentViewSettings.put(cViewEl.getAttribute("id"), loadContentViewSetting(cViewEl));
    }
    applicationSettings.setContentViewSettings(contentViewSettings);
  }

  private ContentViewSetting loadContentViewSetting(Element elem) {
    String schemaIds = getSingleElementValue(elem, "schemaIDs");
    String styleURI = getSingleElementValue(elem, "styleURI");
    String xquery = getSingleElementValue(elem, "xquery");
    String pipelineURI = getSingleElementValue(elem, "pipelineURI");
    boolean pdf = Boolean.parseBoolean(getSingleElementValue(elem, "pdf"));
    String fileName = getSingleElementValue(elem, "fileName");
    String contentType = getSingleElementValue(elem, "contentType");
    String exportPDFStyleURI = getSingleElementValue(elem, "pdfExportStyleURI");
    String exportPDFxprocURI = getSingleElementValue(elem, "pdfExportPipelineURI");
    String exportPDFFromDatabase = getSingleElementValue(elem, "exportPDFFromDatabase");
    String exportSRCFromDatabase = getSingleElementValue(elem, "exportSRCFromDatabase");
    return new ContentViewSetting(pipelineURI, styleURI, xquery, pdf, schemaIds, contentType,
        fileName, exportPDFStyleURI, exportPDFxprocURI, exportPDFFromDatabase,
        exportSRCFromDatabase);
  }

  private SearchField loadSearchField(Element elem) {
    boolean fullText = Boolean.parseBoolean(getSingleElementValue(elem, "fullText"));
    boolean metadata = Boolean.parseBoolean(getSingleElementValue(elem, "metadata"));
    boolean addAsVariable = Boolean.parseBoolean(getSingleElementValue(elem, "addAsVariable"));
    String whereClauseParameter = getSingleElementValue(elem, "whereclauseparameter");
    String flexibleCode = getSingleElementValue(elem, "flexiblecode");
    String label = getSingleElementValue(elem, "label");
    String operator = getSingleElementValue(elem, "operator");
    String type = getSingleElementValue(elem, "type");
    return new SearchField(fullText, label, whereClauseParameter, flexibleCode, metadata, operator,
        type, addAsVariable);
  }

  private SearchResultItem loadSearchResultItem(Element elem) {
    String label = getSingleElementValue(elem, "label");
    boolean dateInMilliseconds =
        Boolean.parseBoolean(getSingleElementValue(elem, "dateInMilliseconds"));
    return new SearchResultItem(label, dateInMilliseconds);
  }

  private OperationField loadOperationField(Element elem) {
    String label = getSingleElementValue(elem, "label");
    String name = getSingleElementValue(elem, "name");
    return new OperationField(name, label);
  }

  private SearchConfiguration loadSearchConfiguration(Element elem) {
    Map<String, SearchField> searchFields = new LinkedHashMap<String, SearchField>();
    Map<String, SearchResultItem> searchResultItems = new LinkedHashMap<String, SearchResultItem>();
    Map<String, NestedSearch> nestedSearches = new LinkedHashMap<String, NestedSearch>();
    Map<String, Report> reports = new LinkedHashMap<String, Report>();
    // Width is not percent but px
    int contentResultPaneWidth = 450;
    String contentResultPaneWidthConfig = "";
    contentResultPaneWidthConfig = getSingleElementValue(elem, "crpWidth");
    if (!contentResultPaneWidthConfig.equals("")) {
      contentResultPaneWidth = Integer.parseInt(contentResultPaneWidthConfig);
    }

    String xformName = getSingleElementValue(elem, "xformName");
    NodeList searchFieldElements = elem.getElementsByTagName("searchField");
    for (int k = 0; k < searchFieldElements.getLength(); k++) {
      Element searchFieldElement = (Element)searchFieldElements.item(k);
      String name = getSingleElementValue(searchFieldElement, "name");
      SearchField searchField = loadSearchField(searchFieldElement);
      searchFields.put(name, searchField);
    }

    // MOD introduced <predicate> attribute, occurring zero or one time
 	String predicate = getChildElementValue(elem, "predicate");

 		// MOD introduced new boolean attribute in template and schema,
 		// detailsPane
 	String detailsPane = getChildElementValue(elem, "detailsPane");
 	boolean usesDetailsPane = (detailsPane == null || detailsPane.length() == 0) ? true
 				: Boolean.valueOf(detailsPane).booleanValue();

 	Integer searchResultsWidthPercent = null;
	try {
		searchResultsWidthPercent = Integer.valueOf(getChildElementValue(
				elem, "searchResultsWidthPercent"));
	} catch (NumberFormatException e) {
		// detailsPaneWidth is null signifying no specified witdth
	}

	// MOD introduced new boolean attribute in tempalte and schema,
	// persistentReport
	String persistentReport = getChildElementValue(elem, "persistentReport");
	String reportOutputType = getChildElementValue(elem, "reportOutputType");
	String persistentReportEmail = getChildElementValue(elem,
			"persistentReportEmail");
	boolean isPersistentReport = (persistentReport == null || persistentReport
			.length() == 0) ? false : Boolean.valueOf(persistentReport)
			.booleanValue();

	String xquery = getChildElementValue(elem, "xquery");

		
 	NodeList searchResultItemElements = elem.getElementsByTagName("searchResultItem");

    for (int k = 0; k < searchResultItemElements.getLength(); k++) {
      Element searchResultItemElement = (Element)searchResultItemElements.item(k);
      String name = getSingleElementValue(searchResultItemElement, "name");
      SearchResultItem searchResultItem = loadSearchResultItem(searchResultItemElement);
      searchResultItems.put(name, searchResultItem);

    }

    NodeList nestedSearchElements = elem.getElementsByTagName("nestedSearch");

    for (int k = 0; k < nestedSearchElements.getLength(); k++) {
      Element nestedSearchElement = (Element)nestedSearchElements.item(k);
      String name = getSingleElementValue(nestedSearchElement, "name");
      NestedSearch nestedSearch = loadNestedSearch(nestedSearchElement);
      nestedSearches.put(name, nestedSearch);
    }

    NodeList reportElements = elem.getElementsByTagName("report");
    for (int k = 0; k < reportElements.getLength(); k++) {
      Element reportElement = (Element)reportElements.item(k);
      String id = reportElement.getAttribute("id");
      Report report = loadReport(reportElement);
      reports.put(id, report);
    }
    Set<String> operations = new HashSet<String>();
    NodeList operationElements = elem.getElementsByTagName("operation");
    for (int k = 0; k < operationElements.getLength(); k++) {
      Element operationElement = (Element)operationElements.item(k);
      operations.add(operationElement.getAttribute("refid"));
    }
    return new SearchConfiguration(usesDetailsPane,
			searchResultsWidthPercent, "xforms/" + xformName, xquery,
			predicate, searchFields, searchResultItems, nestedSearches,
			reports, operations, isPersistentReport, reportOutputType,
			persistentReportEmail,contentResultPaneWidth);
  }

  private OperationConfiguration loadOperationConfiguration(Element elem) {
    Map<String, OperationField> operationFields = new LinkedHashMap<String, OperationField>();
    String xformName = getSingleElementValue(elem, "xformName");
    String buttonLabel = getSingleElementValue(elem, "buttonLabel");
    String dialogTitle = getSingleElementValue(elem, "dialogTitle");
    String message = getSingleElementValue(elem, "message");
    Element contentViewSetting = getSingleElement(elem, "contentViewSetting");
    String contentViewSettingId = null;
    String sourceResultItemName = null;
    if (contentViewSetting != null) {
      contentViewSettingId = contentViewSetting.getAttribute("refid");
      sourceResultItemName = contentViewSetting.getAttribute("sourceResultItemName");
    }

    NodeList operationFieldElements = elem.getElementsByTagName("operationField");
    for (int k = 0; k < operationFieldElements.getLength(); k++) {
      Element operationFieldElement = (Element)operationFieldElements.item(k);
      String fieldName = getSingleElementValue(operationFieldElement, "name");
      OperationField operationField = loadOperationField(operationFieldElement);
      operationFields.put(fieldName, operationField);
    }
    NodeList xqueryElements = elem.getElementsByTagName("xquery");
    List<String> xqueries = new ArrayList<String>();
    for (int k = 0; k < xqueryElements.getLength(); k++) {
      Element xqueryElem = (Element)xqueryElements.item(k);
      xqueries.add(getTextContent(xqueryElem));
    }
    return new OperationConfiguration("xforms/" + xformName, buttonLabel, dialogTitle, message,
        operationFields, xqueries, contentViewSettingId, sourceResultItemName);
  }

  private SearchSettings loadSearchSettings(Element elem) {
    SearchSettings result = new SearchSettings();
    if (elem != null) {
      SearchConfigurations srchCfgs = applicationSettings.getSearchConfigurations();
      for (Element searchSetting : getChildElements(elem)) {
        String name = searchSetting.getAttribute("name");
        String refId = searchSetting.getAttribute("refid");
        result.put(name, new SearchSetting(srchCfgs, name, refId));
      }
    }
    return result;
  }

  private NodeSetting loadHierarchy(Element elem) {
    NodeSetting rootNodeSetting = null;
    Element hierarchy = getSingleElement(elem, "hierarchy");
    String title = getChildElementValue(hierarchy, "title");
    Map<String, NodeSetting> nodeSettings = new HashMap<String, NodeSetting>();
    if (hierarchy != null) {
      Element node = (Element)hierarchy.getFirstChild();
      while (node != null && !(node instanceof Element && "node".equals(node.getNodeName()))) {
        node = (Element)node.getNextSibling();
      }
      if (node != null) {
        rootNodeSetting = loadNodeSetting(null, node, nodeSettings);
      }
    }
    applicationSettings.setNodeSettings(nodeSettings);
    applicationSettings.setHierarchy(new Hierarchy(title, rootNodeSetting));
    return rootNodeSetting;
  }

  private NodeSetting loadNodeSetting(NodeSetting parentNodeSetting, Element node,
      Map<String, NodeSetting> nodeSettings) {
    // load currentNode
    String id = getSingleElementValue(node, "id");
    String label = getSingleElementValue(node, "label");
    String description = getSingleElementValue(node, "description");
    String icon = getSingleElementValue(node, "icon");
    boolean chainOfCustody = false;
    String value = getChildElementValue(node, "chainOfCustody");
    if (value != null && !"".equals(value)) {
      chainOfCustody = Boolean.parseBoolean(value);
    }
    Element searchSettingsElem = getChildElement(node, "searchSettings");
    SearchSettings searchSettings = loadSearchSettings(searchSettingsElem);
    List<String> contentViewSettings = new ArrayList<String>();
    Element contentViewSettingsElem = getChildElement(node, "contentViewSettings");
    if (contentViewSettingsElem != null) {
      for (Element contentViewSettingElement : getDescElems(contentViewSettingsElem,
          "contentViewSetting")) {
        contentViewSettings.add(contentViewSettingElement.getAttribute("refid"));
      }
    }
    NodeSetting nodeSetting =
        new NodeSetting(id, label, description, icon, chainOfCustody, searchSettings,
            contentViewSettings, parentNodeSetting);
    nodeSettings.put(id, nodeSetting);
    List<NodeSetting> childNodeSettings = new ArrayList<NodeSetting>();
    Node childNode = node.getFirstChild();
    while (childNode != null) {
      if ("node".equals(childNode.getNodeName())) {
        NodeSetting childNodeSetting =
            loadNodeSetting(nodeSetting, (Element)childNode, nodeSettings);
        childNodeSettings.add(childNodeSetting);
      }
      childNode = childNode.getNextSibling();
    }
    nodeSetting.setChildren(childNodeSettings);
    return nodeSetting;
  }

  private NestedSearch loadNestedSearch(Element elem) {
    String name = getSingleElementValue(elem, "name");
    String label = getSingleElementValue(elem, "label");
    String description = getSingleElementValue(elem, "description");
    String configuration = getSingleElementValue(elem, "configurationId");
    String nodeId = getSingleElementValue(elem, "nodeId");
    String transformationId = getSingleElementValue(elem, "transformationConfiguration");
    Element fieldsElement = null;

    // nested searches may not have any fields if the search is a detailed search drill down that
// doesn't need field mapping to a new search form
    Map<String, String> fields = null;
    if (getChildElements(elem, "fields").size() > 0) {
      fieldsElement = getChildElements(elem, "fields").get(0);
      fields =
          loadKeyValuePairs(fieldsElement, "field", "targetSearchFieldName", "sourceResultItemName");
    }
    return new NestedSearch(name, label, description, nodeId, configuration, fields,
        transformationId);

  }

  private Report loadReport(Element elem) {
    String id = elem.getAttribute("id");
    String label = getSingleElementValue(elem, "label");
    String description = getSingleElementValue(elem, "description");
    String configuration = getSingleElementValue(elem, "configurationId");
    String xquery = getSingleElementValue(elem, "xquery");
    List<Element> fieldsElements = getChildElements(elem, "fields");
    Map<String, String> fields = null;
    if (fieldsElements.size() > 0) {
      Element fieldsElement = fieldsElements.get(0);
      fields =
          loadKeyValuePairs(fieldsElement, "field", "targetSearchFieldName", "sourceResultItemName");
    }
    return new Report(id, label, description, configuration, xquery, fields);
  }

  private Map<String, String> loadKeyValuePairs(Element elem, String fieldName, String keyName,
      String valueName) {
    Map<String, String> result = new HashMap<String, String>();
    for (Element field : getChildElements(elem, fieldName)) {
      String key = getSingleElementValue(field, keyName);
      String value = getSingleElementValue(field, valueName);
      result.put(key, value);
    }
    return result;
  }

  private List<Element> getChildElements(Element elem) {

    List<Element> result = new ArrayList<Element>();
    Node child = elem.getFirstChild();
    while (child != null) {
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        result.add((Element)child);
      }
      child = child.getNextSibling();
    }
    return result;
  }

  private List<Element> getChildElements(Element elem, String tagName) {
    List<Element> result = new ArrayList<Element>();
    Node child = elem.getFirstChild();
    while (child != null) {
      if (child.getNodeType() == Node.ELEMENT_NODE && ((Element)child).getTagName().equals(tagName)) {
        result.add((Element)child);
      }
      child = child.getNextSibling();
    }
    return result;
  }

  private List<Element> getDescElems(Element elem, String tagName) {
    List<Element> result = new ArrayList<Element>();
    NodeList descendants = elem.getElementsByTagName(tagName);
    for (int counter = 0; counter < descendants.getLength(); counter++) {
      result.add((Element)descendants.item(counter));
    }
    return result;
  }

  private Element getSingleElement(Element elem, String name) {
    NodeList result = elem.getElementsByTagName(name);
    if (result.getLength() > 0) {
      return (Element)result.item(0);
    }
    return null;
  }

  private String getSingleElementValue(Element elem, String name) {
    NodeList result = elem.getElementsByTagName(name);
    if (result.getLength() > 0) {
      return getTextContent((Element)result.item(0));
    }
    return "";
  }

  private Element getChildElement(Element parent, String name) {
    for (Element elem : getChildElements(parent, name)) {
      if (name.equals(elem.getNodeName())) {
        return elem;
      }
    }
    return null;
  }

  private String getChildElementValue(Element parent, String name) {
    List<Element> elems = getChildElements(parent, name);
    if (elems.size() > 0) {
      return getTextContent(elems.get(0));
    }
    return "";
  }

  private String getTextContent(Element element) {
    StringBuffer sb = new StringBuffer("");
    Node child = element.getFirstChild();
    while (child != null) {
      if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
        sb.append(child.getNodeValue());
      }
      child = child.getNextSibling();
    }
    return sb.toString();
  }

  private int getValueAsInt(String value) {
    if (value != null && value.length() > 0) {
      return Integer.parseInt(value);
    }
    return -1;
  }

  private int getPercentageValue(String value) {
    if (value != null && value.length() > 0 && value.indexOf("%") == value.length() - 1) {
      return Integer.parseInt(value.substring(0, value.length() - 1));
    }
    return 100;
  }

}
