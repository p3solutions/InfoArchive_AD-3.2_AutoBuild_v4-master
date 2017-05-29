/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.emc.dds.xmlarchiving.client.authorization.Role;
import com.emc.dds.xmlarchiving.client.configuration.ApplicationSettings;
import com.emc.dds.xmlarchiving.client.configuration.SearchSetting;
import com.emc.dds.xmlarchiving.client.configuration.SearchSettings;
import com.emc.dds.xmlarchiving.client.data.DynamicQueryDataSource;
import com.emc.dds.xmlarchiving.client.event.ApplicationEvent;
import com.emc.dds.xmlarchiving.client.event.PaneLoadedEvent;
import com.emc.dds.xmlarchiving.client.event.SearchSubmitEvent;
import com.emc.documentum.xml.dds.gwt.client.ui.DDSXFormsPanel;
import com.emc.documentum.xml.dds.gwt.client.util.DDSURI;
import com.emc.documentum.xml.gwt.client.ui.SimpleDecoratorPanel;
import com.emc.documentum.xml.xforms.gwt.client.XFormsSubmissionHandler;
import com.emc.documentum.xml.xforms.gwt.client.event.XFormsEvent;
import com.emc.documentum.xml.xforms.gwt.client.event.XFormsEventListener;
import com.emc.documentum.xml.xforms.gwt.client.model.XFormsSubmission;
import com.emc.documentum.xml.xforms.gwt.client.model.impl.XFormsSubmissionImpl;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.DecoratedTabBar;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Pane containing an XForm. When the form is submitted, an event is fired containing the user
 * input. Other panes can listen to this event and process the result. WARNING: This class is
 * experimental and may change in future DDS releases.
 */
public class SearchPane extends ContentPane implements XFormsEventListener,
XFormsSubmissionHandler, SelectionHandler<Integer> {

  private Map<String, DDSXFormsPanel> xformsPanels = new HashMap<String, DDSXFormsPanel>();
  private final FlowPanel queryPanel;
  private DDSXFormsPanel xformsPanel;
  private final TabBar searchButtons;
  private final ScrollPanel scrollPanel;
// private final DecoratedTabBar searchButtons;
  private final ArrayList<String> searchNames;

  public SearchPane(ApplicationSettings applicationSettings) {

    super(applicationSettings);
    applicationSettings.getState().addListener(this);

    queryPanel = new FlowPanel();
    queryPanel.addStyleName(getPaneStyle());

    searchNames = new ArrayList<String>();
    searchButtons = new DecoratedTabBar();
// this.searchButtons = new TabBar();
    searchButtons.addSelectionHandler(this);
    SimpleDecoratorPanel borderDecorator =
        new SimpleDecoratorPanel("core-TabBarBorder", searchButtons);
    borderDecorator.setWidth("100%");
    queryPanel.add(borderDecorator);

    scrollPanel = new ScrollPanel();
    scrollPanel.setAlwaysShowScrollBars(false);
    scrollPanel.setHeight("100%"); // Important
    scrollPanel.addStyleName(STYLE_SCROLL_BORDER);
    queryPanel.add(scrollPanel);

    LDMBorderDecorator decorator = new LDMBorderDecorator("Search", queryPanel);
    // Having the query panel have 200% height causes the scrollpanel to maximize to content
    queryPanel.setHeight(""); // Important
    initWidget(decorator);
    updateSearchButtons();
  }

  @Override
  public int getPaneType() {
    return SEARCH_PANE;
  }

  @Override
  public String getPaneName() {
    return SEARCH_PANE_NAME;
  }

  @Override
  public void loadData() {
    SearchSetting searchSetting = getState().getCurrentSearchSetting();
    Role role = getApplicationSettings().getRole();
    if (searchSetting != null
        && role.hasOperationAuthorization(searchSetting.getSearchConfigurationId())) {
      DDSURI xformCollectionURI = new DDSURI(searchSetting.getSearchConfiguration().getXformURI());
      xformCollectionURI.setAttribute(DDSURI.ATTRIBUTE_DOMAIN, DDSURI.DOMAIN_RESOURCE);
      String uri = xformCollectionURI.toString();
      this.xformsPanel = this.xformsPanels.get(uri);
       if(this.xformsPanel != null) {
          this.xformsPanel.removeXFormsEventListener(this);
          this.xformsPanel.setSubmissionHandler(null);
      }
      this.xformsPanel = new DDSXFormsPanel(uri);
      this.xformsPanel.setUIHandler(new LDMUIHandler());
      this.xformsPanel.addXFormsEventListener(this);
      this.xformsPanel.setSubmissionHandler(this);
      this.xformsPanel.setStylePrimaryName("XFormsPanel");
      this.scrollPanel.setWidget(this.xformsPanel);
    }
  }

  @Override
  public void handle(ApplicationEvent event) {
    super.handle(event);
    switch (event.getType()) {
      case ApplicationEvent.NODE_SELECTED_EVENT:
        updateSearchButtons();
        if (xformsPanel != null) {
          scrollPanel.remove(xformsPanel);
        }
        setLoaded(false);
        loadData();
        break;
      case ApplicationEvent.SEARCH_SELECTED_EVENT:
        if (xformsPanel != null) {
          scrollPanel.remove(xformsPanel);
        }

        setLoaded(false);
        loadData();
        // if the right tab is not selected, then select
        String searchName = getState().getCurrentSearchSetting().getName();
        int index = 0;
        for (String key : getState().getCurrentSetting().getSearchSettings().keySet()) {
          if (key.equals(searchName)) {
            if (searchButtons.getSelectedTab() != index) {
              searchButtons.selectTab(index);
            }
            break;
          }
          index++;
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void onXFormsEvent(XFormsEvent event) {
    if (event.getNameAsString().equals("xforms-ready")) {
      if (!isLoaded()) {
        setLoaded(true);
        fireEvent(new PaneLoadedEvent(SEARCH_PANE));
      }
    }
  }

  @Override
  public boolean onSubmit(XFormsSubmission submission, String submissionBody) {
	    Element xformsInstance = XMLParser.parse(submission.getInstanceData(null)).getDocumentElement();
	    Map<String, String> externalVariables = new LinkedHashMap<String, String>();
	    for (String field : getState().getCurrentSearchSetting().getSearchConfiguration()
	        .getSearchFields().keySet()) {
	      NodeList fieldNodes = xformsInstance.getElementsByTagName(field);
	      if(fieldNodes.getLength() > 0) {
	        Node firstChild = fieldNodes.item(0).getFirstChild();
	        if (firstChild != null) {
	          String query = firstChild.getNodeValue();
	          externalVariables.put(field, query);
	        }
	      }
	    }
	    fireEvent(new SearchSubmitEvent(externalVariables));

	    submission.onSubmitDone("", -1, null, null);
	    return true;
  }

  private void updateSearchButtons() {
    Role role = getApplicationSettings().getRole();
    searchNames.clear();
    SearchSettings searchSettings =
        getApplicationSettings().getState().getCurrentSetting().getSearchSettings();
    List<SearchSetting> authorizedSettings = new ArrayList<SearchSetting>();
    for (SearchSetting searchSetting : searchSettings.values()) {
      if (role.hasOperationAuthorization(searchSetting.getSearchConfigurationId())) {
        authorizedSettings.add(searchSetting);
      }
    }
    if (authorizedSettings.size() == 0) {
      queryPanel.setVisible(false);
    } else {
      while (searchButtons.getTabCount() > 0) {
        searchButtons.removeTab(0);
      }
      for (SearchSetting searchSetting : authorizedSettings) {
        if (role.hasOperationAuthorization(searchSetting.getSearchConfigurationId())) {
          String searchName = searchSetting.getName();
          searchButtons.addTab(searchName);
          searchNames.add(searchName);
        }
      }
      searchButtons.selectTab(0);
      queryPanel.setVisible(true);
    }
  }

  @Override
  public void onSelection(SelectionEvent<Integer> event) {
    String searchName = searchNames.get(event.getSelectedItem());
    getApplicationSettings().getState().setCurrentSearchSetting(searchName);
  }
}
