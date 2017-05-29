/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.emc.dds.xmlarchiving.client.event.ApplicationEvent;
import com.emc.dds.xmlarchiving.client.event.ApplicationEventListener;
import com.emc.dds.xmlarchiving.client.event.ApplicationEventSource;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * The Main pane serves as a container for all panes included in the application. It maintains the
 * fixed height and width of all panels and sets all event listeners. Furthermore it handles the
 * loading of data. This is necessary because IE cannot handle concurrent requests. WARNING: This
 * class is experimental and may change in future DDS releases.
 */
public class MainPane extends DockLayoutPanel implements ApplicationEventListener,
ApplicationEventSource {

  private Map<String, Pane> panes = new LinkedHashMap<String, Pane>();

  private final List<ApplicationEventListener> listeners =
      new ArrayList<ApplicationEventListener>();

  /**
   * Constructs a content pane.
   * @param applicationSettings
   */
  public MainPane() {
    super(Unit.PX);
  }

  public void setPanes(Map<String, Pane> panes) {
    this.panes = panes;
  }

  public void init(Panel panel) {
    setListeners();
    // add(panel);
  }

  public Map<String, Pane> getPanes() {
    return panes;
  }

  private void setListeners() {
    for (String source : panes.keySet()) {
      Pane sourcePane = panes.get(source);
      if (sourcePane instanceof ContentPane) {
        for (String target : panes.keySet()) {
          if (!target.equals(source)) {
            Pane targetPane = panes.get(target);
            if (targetPane instanceof ContentPane) {
              ((ContentPane)sourcePane).addListener((ContentPane)targetPane);
            }
          }
        }
      }
    }
  }

  /**
   * Sets the total fixed height of some non-resizable panes.
   * @param fixedHeight the fixed height
   */
  public void init() {
    // now load the rest of the data
    loadData();
  }

  public void loadData() {
    for (String key : panes.keySet()) {
      Pane pane = panes.get(key);
      if (pane instanceof ContentPane && !((ContentPane)pane).isLoaded()) {
        ((ContentPane)pane).loadData();
        break;
      }
    }
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

  @Override
  public void handle(ApplicationEvent event) {
    switch (event.getType()) {
      case ApplicationEvent.PANE_LOADED:
        for (String key : panes.keySet()) {
          Pane pane = panes.get(key);
          if (pane instanceof ContentPane && !((ContentPane)pane).isLoaded()) {
            ((ContentPane)pane).loadData();
            break;
          }
        }
        break;
    }
  }

}
