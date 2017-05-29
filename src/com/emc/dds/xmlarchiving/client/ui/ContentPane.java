/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.emc.dds.xmlarchiving.client.configuration.ApplicationSettings;
import com.emc.dds.xmlarchiving.client.configuration.ApplicationState;
import com.emc.dds.xmlarchiving.client.configuration.NodeSetting;
import com.emc.dds.xmlarchiving.client.event.ApplicationEvent;
import com.emc.dds.xmlarchiving.client.event.ApplicationEventListener;
import com.emc.dds.xmlarchiving.client.event.ApplicationEventSource;

/**
 * Abstract class of panes that load content and communicate with other panes. WARNING: This class
 * is experimental and may change in future DDS releases.
 */
public abstract class ContentPane extends Pane implements ApplicationEventSource,
ApplicationEventListener {

  private final List<ApplicationEventListener> listeners =
      new ArrayList<ApplicationEventListener>();

  private final ApplicationSettings applicationSettings;

  private boolean loaded;

  /**
   * Constructs a pane.
   * @param applicationSettings the settings shared by the panes
   */
  public ContentPane(ApplicationSettings applicationSettings) {
    super();
    this.applicationSettings = applicationSettings;
  }

  /**
   * Load the pane data from the server.
   */
  public void loadData() {
    //
  }

  /**
   * Indicate whether the data is loaded.
   * @param loaded the new value
   */
  public void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }

  /**
   * Returns <code>true</code> if the data is loaded.
   * @return <code>true</code> if the data is loaded
   */
  public boolean isLoaded() {
    return loaded;
  }

  /**
   * Returns the application settings object.
   * @return the application settings object
   */
  public ApplicationSettings getApplicationSettings() {
    return applicationSettings;
  }

  public NodeSetting getCurrentSetting() {
    return applicationSettings.getState().getCurrentSetting();
  }

  /**
   * Returns the current data set setting.
   * @return the current data set setting.
   */
// protected DataSetSetting getDataSetSetting() {
// return this.applicationSettings.getState().getCurrentDataSetSetting();
// }

  protected ApplicationState getState() {
    return applicationSettings.getState();
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
   * Remove all listeners of this pane.
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

  /**
   * Handle event.
   * @param event the event to handle.
   */
  @Override
  public void handle(ApplicationEvent event) {
  }

}
