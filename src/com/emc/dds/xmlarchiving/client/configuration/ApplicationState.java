/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.emc.dds.xmlarchiving.client.data.NodeSettingDataSource;
import com.emc.dds.xmlarchiving.client.event.ApplicationEvent;
import com.emc.dds.xmlarchiving.client.event.ApplicationEventListener;
import com.emc.dds.xmlarchiving.client.event.ApplicationEventSource;
import com.emc.dds.xmlarchiving.client.event.LanguageChangedEvent;
import com.emc.dds.xmlarchiving.client.event.NodeSelectedEvent;
import com.emc.dds.xmlarchiving.client.event.SearchSelectedEvent;
import com.emc.documentum.xml.gwt.client.DataChangeListener;
import com.emc.documentum.xml.gwt.client.SourcesDataChangeEvents;

public class ApplicationState implements DataChangeListener, ApplicationEventSource {

  private ApplicationSettings applicationSettings;
  private NodeSetting currentSetting;
  private String currentLocale;
  private SearchSetting currentSearchSetting;

  private final List<ApplicationEventListener> listeners =
      new ArrayList<ApplicationEventListener>();

  public ApplicationState(ApplicationSettings applicationSettings) {
    this.applicationSettings = applicationSettings;
  }

  public ApplicationSettings getApplicationSettings() {
    return applicationSettings;
  }

  public void setApplicationSettings(ApplicationSettings applicationSettings) {

    setAllToNull();
    this.applicationSettings = applicationSettings;
  }

  public NodeSetting getCurrentSetting() {
    return currentSetting;
  }

  private boolean isAuthorized(NodeSetting currSetting) {
    return true;
  }

  public void setCurrentSetting(NodeSetting currentSetting) {
    // TODO: handle locale
    if (this.currentSetting != currentSetting && isAuthorized(currentSetting)) {
      NodeSetting oldSetting = this.currentSetting;
      setAllToNull();
      this.currentSetting = currentSetting;

      fireEvent(new NodeSelectedEvent(oldSetting, this.currentSetting));
    }
  }

  public String getCurrentLocale() {
    return currentLocale;
  }

  public void setCurrentLocale(String currentLocale) {
    String oldLocale = this.currentLocale;
    this.currentLocale = currentLocale;
    if (oldLocale == null) {
      if (currentLocale != null) {
        fireEvent(new LanguageChangedEvent(currentLocale, oldLocale));
      }
    } else {
      if (!oldLocale.equals(currentLocale)) {
        fireEvent(new LanguageChangedEvent(currentLocale, oldLocale));
      }
    }
  }

  public SearchSetting getCurrentSearchSetting() {
    if (currentSearchSetting == null) {
      Iterator<SearchSetting> iterator =
          getCurrentSetting().getSearchSettings().values().iterator();
      if (iterator.hasNext()) {
        currentSearchSetting = iterator.next();
      }
    }
    return currentSearchSetting;
  }

  public void setCurrentSearchSetting(SearchSetting currentSearchSetting) {
    this.currentSearchSetting = currentSearchSetting;
  }

  public void setCurrentSearchSetting(String searchName) {
    currentSearchSetting = getCurrentSetting().getSearchSettings().get(searchName);
    fireEvent(new SearchSelectedEvent(currentSearchSetting));
  }

  private void setAllToNull() {
    currentLocale = null;
    currentSearchSetting = null;
  }

  @Override
  public void onDataChange(SourcesDataChangeEvents sender) {
    // TODO: remove this
    setCurrentSetting((NodeSetting)((NodeSettingDataSource)sender).getContext());
  }

  /**
   * Add a listener to this pane.
   * @param a new listener.
   */
  @Override
  public void addListener(ApplicationEventListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes all listeners of this pane.
   */
  @Override
  public void clearListeners() {
    listeners.clear();
  }

  /**
   * Remove listener of this pane.
   * @param listener listener to remove.
   */
  @Override
  public void removeListener(ApplicationEventListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire event.
   * @param event the event to fire.
   */
  protected void fireEvent(ApplicationEvent event) {
    for (ApplicationEventListener listener : listeners) {
      listener.handle(event);
    }
  }

}
