/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import com.emc.dds.xmlarchiving.client.configuration.ApplicationSettings;
import com.emc.dds.xmlarchiving.client.configuration.Hierarchy;
import com.emc.dds.xmlarchiving.client.configuration.NodeSetting;
import com.emc.dds.xmlarchiving.client.data.NodeSettingDataSource;
import com.emc.dds.xmlarchiving.client.event.ApplicationEvent;
import com.emc.dds.xmlarchiving.client.event.NestedSearchSubmitEvent;
import com.google.gwt.user.client.ui.ScrollPanel;

public class DataSetsTreePane extends ContentPane {

  private NodeSettingDataSource dataSource;
  private NodeSettingTree tree;

  private ScrollPanel datasetTreeScrollpanel;

  public DataSetsTreePane(ApplicationSettings applicationSettings) {

    super(applicationSettings);
  }

  public void init() {
    ApplicationSettings applicationSettings = getApplicationSettings();
    Hierarchy hierarchy = applicationSettings.getHierarchy();
    dataSource =
        new NodeSettingDataSource(hierarchy.getRootNodeSetting(), applicationSettings.getRole());
    dataSource.addDataChangeListener(getApplicationSettings().getState());
    tree = new NodeSettingTree(dataSource, super.getApplicationSettings().getRole());

    datasetTreeScrollpanel = new ScrollPanel(tree);
    datasetTreeScrollpanel.addStyleName(getPaneStyle());

    dataSource.refresh();

    LDMBorderDecorator decorator =
        new LDMBorderDecorator(hierarchy.getTitle(), datasetTreeScrollpanel);
    initWidget(decorator);

    setLoaded(true);
  }

  @Override
  public String getPaneName() {
    return DATA_SET_LIST_PANE_NAME;
  }

  @Override
  public int getPaneType() {
    return DATA_SET_LIST_PANE;
  }

  @Override
  public void handle(ApplicationEvent event) {
    super.handle(event);
    switch (event.getType()) {
      case ApplicationEvent.RESIZE_EVENT:
        break;
      case ApplicationEvent.NESTED_SEARCH_SUBMIT_EVENT:
        String nodeId = ((NestedSearchSubmitEvent)event).getNodeId();
        NodeSetting nodeSetting = getApplicationSettings().getNodeSettings().get(nodeId);
        dataSource.setContext(nodeSetting);
        break;
    }
  }
}
