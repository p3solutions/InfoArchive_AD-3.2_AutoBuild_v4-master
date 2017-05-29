/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

import java.util.ArrayList;
import java.util.List;

import com.emc.dds.xmlarchiving.client.authorization.Role;

public class NodeSetting {

  private final String id;

  private final String label;

  private final String description;

  private final String icon;

  private boolean chainOfCustody;

  private final SearchSettings searchSettings;

  private final List<String> contentViewSettings;

  private final NodeSetting parent;

  private List<NodeSetting> children;

  public NodeSetting(String id, String label, String description, String icon,
      boolean chainOfCustody, SearchSettings searchSettings, List<String> contentViewSettings,
      NodeSetting parent) {
    super();
    this.id = id;
    this.label = label;
    this.description = description;
    this.icon = icon;
    this.chainOfCustody = chainOfCustody;
    this.searchSettings = searchSettings;
    this.contentViewSettings = contentViewSettings;
    this.parent = parent;
  }

  public String getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  public String getIcon() {
    return icon;
  }

  public boolean isChainOfCustody() {
    return chainOfCustody;
  }

  public SearchSettings getSearchSettings() {
    return searchSettings;
  }

  public List<String> getContentViewSettings() {
    return contentViewSettings;
  }

  public void setChildren(List<NodeSetting> children) {
    this.children = children;
  }

  public List<NodeSetting> getChildren(Role role) {
    List<NodeSetting> result = new ArrayList<NodeSetting>();
    for (NodeSetting setting : children) {
      if (role.hasNodeAuthorization(setting.id)) {
        result.add(setting);
      }
    }
    return result;
  }

  public List<NodeSetting> getChildren() {
    return children;
  }

  public NodeSetting getParent() {
    return parent;
  }

  public ContentViewSetting getContentViewSetting(ApplicationSettings applicationSettings,
      String schemaId) {
    for (String previewSettingID : contentViewSettings) {
      ContentViewSetting previewSetting =
          applicationSettings.getContentViewSettings().get(previewSettingID);
      if (previewSetting.appliesTo(schemaId)) {
        return previewSetting;
      }
    }
    return null;
  }

}
