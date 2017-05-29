/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

import java.util.Map;

import com.emc.dds.xmlarchiving.client.authorization.Role;
import com.emc.documentum.xml.dds.gwt.client.util.ApplicationContext;

public class ApplicationSettings {

  private SearchSettings searchSettings;

  private SearchConfigurations searchConfigurations;

  private OperationConfigurations operationConfigurations;

  private Map<String, ContentViewSetting> contentViewSettings;

  private Map<String, NodeSetting> nodeSettings;

  private Hierarchy hierarchy;

  private NodeSetting rootNodeSetting;

  private final ApplicationContext applicationContext;

  private ApplicationState state;

  private boolean userServiceConfigured;

  private Role role;

  private String userName;
  private String emailId;

  public ApplicationSettings(ApplicationContext applicationContext) {
    super();
    this.applicationContext = applicationContext;
    state = new ApplicationState(this);
  }

  public void init() {
    //
  }

  public Hierarchy getHierarchy() {
    return hierarchy;
  }

  public void setHierarchy(Hierarchy hierarchy) {
    this.hierarchy = hierarchy;
    state.setCurrentSetting(hierarchy.getRootNodeSetting());
  }

  public void setNodeSettings(Map<String, NodeSetting> nodeSettings) {
    this.nodeSettings = nodeSettings;
  }

  public Map<String, NodeSetting> getNodeSettings() {
    return nodeSettings;
  }

  /**
   * localeAware is true if there is at least one dataset that has locales.
   */
  private boolean localeAware;

  public SearchSettings getSearchSettings() {
    return searchSettings;
  }

  public void setSearchSettings(SearchSettings searchSettings) {
    this.searchSettings = searchSettings;
  }

  public SearchConfigurations getSearchConfigurations() {
    return searchConfigurations;
  }

  public void setSearchConfigurations(SearchConfigurations searchConfigurations) {
    this.searchConfigurations = searchConfigurations;
  }

  public OperationConfigurations getOperationConfigurations() {
    return operationConfigurations;
  }

  public void setOperationConfigurations(OperationConfigurations operationConfigurations) {
    this.operationConfigurations = operationConfigurations;
  }

  public boolean isUserServiceConfigured() {
    return userServiceConfigured;
  }

  public void setUserServiceConfigured(boolean userServiceConfigured) {
    this.userServiceConfigured = userServiceConfigured;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Map<String, ContentViewSetting> getContentViewSettings() {
    return contentViewSettings;
  }

  public void setContentViewSettings(Map<String, ContentViewSetting> contentViewSettings) {
    this.contentViewSettings = contentViewSettings;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public ApplicationState getState() {
    return state;
  }

  public boolean isLocaleAware() {
    return localeAware;
  }
  public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
}
