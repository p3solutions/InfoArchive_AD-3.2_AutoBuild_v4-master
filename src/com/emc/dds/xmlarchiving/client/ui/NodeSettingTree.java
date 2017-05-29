/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import com.emc.dds.xmlarchiving.client.authorization.Role;
import com.emc.dds.xmlarchiving.client.configuration.NodeSetting;
import com.emc.dds.xmlarchiving.client.data.NodeSettingDataSource;
import com.emc.dds.xmlarchiving.client.ui.image.MainImageBundle;
import com.emc.documentum.xml.gwt.client.AbstractDataSource;
import com.emc.documentum.xml.gwt.client.ui.AbstractDataTree;
import com.emc.documentum.xml.gwt.client.ui.ImageTextPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;

public class NodeSettingTree extends AbstractDataTree {

  private Role role;

  public NodeSettingTree(NodeSettingDataSource dataSource, Role role) {
    super(dataSource);
    this.role = role;
  }

  @Override
  protected TreeItem createItem(Object userObject, AbstractDataSource<?> dataSource) {

    NodeSetting setting = (NodeSetting)userObject;
    String label = setting.getLabel();
    // TODO handle this
    // Unfortunately we cannot use java reflection
    Image icon = null;
    MainImageBundle imageBundle = MainImageBundle.INSTANCE;
    String iconName = setting.getIcon();
    if (iconName.equals("application16")) {
      icon = imageBundle.application16().createImage();
    } else if (iconName.equals("allapplications16")) {
      icon = imageBundle.allapplications16().createImage();
    } else if (iconName.equals("businessobject16")) {
      icon = imageBundle.businessobject16().createImage();
    }
    return new TreeItem(new ImageTextPanel(icon, label));
  }

  @Override
  protected boolean isLeaf(Object userObject) {
    return ((NodeSetting)userObject).getChildren(role).size() == 0;
  }

}
