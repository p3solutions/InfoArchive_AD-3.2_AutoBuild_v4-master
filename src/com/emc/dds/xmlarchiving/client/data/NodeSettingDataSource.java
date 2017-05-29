/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.data;

import java.util.ArrayList;

import com.emc.dds.xmlarchiving.client.authorization.Role;
import com.emc.dds.xmlarchiving.client.configuration.NodeSetting;
import com.emc.documentum.xml.gwt.client.AbstractDataSource;

public class NodeSettingDataSource extends AbstractDataSource<NodeSetting> {

  private Role role;

  public NodeSettingDataSource(NodeSetting rootNodeSetting, Role role) {
    super();
    this.role = role;
    setContext(rootNodeSetting);
  }

  public NodeSettingDataSource(AbstractDataSource<NodeSetting> dataSource, Role role) {
    super(dataSource);
    this.role = role;
  }

  @Override
  public AbstractDataSource<NodeSetting> cloneDataSource() {
    return new NodeSettingDataSource(this, role);
  }

  @Override
  public NodeSetting getParent(NodeSetting userObject) {
    return userObject.getParent();
  }

  @Override
  public void refresh() {
    NodeSetting context = (NodeSetting)getContext();
    ArrayList<NodeSetting> data = new ArrayList<NodeSetting>();
    data.addAll(context.getChildren(role));
    setData(data, -1);
  }
}
