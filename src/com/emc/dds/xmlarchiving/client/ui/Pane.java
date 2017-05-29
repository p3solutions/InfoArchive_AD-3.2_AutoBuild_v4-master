/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Abstract class to be used as base class for panes. A pane represents an independent section of
 * the UI. Panes must be configurable and reusable. WARNING: This class is experimental and may
 * change in future DDS releases.
 */
public abstract class Pane extends Composite implements ProvidesResize, RequiresResize {

  public static final int MAIN_PANE = 0;
  public static final int SEARCH_RESULT_DISCLOSURE_PANE = 1;
  public static final int SEARCH_RESULT_PANE = 2;
  public static final int CONTENT_TREE_PANE = 3;
  public static final int CONTENT_VIEW_PANE = 4;
  public static final int MENU_PANE = 5;
  public static final int SEARCH_PANE = 6;
  public static final int DATA_SET_LIST_PANE = 7;
  public static final int WELCOME_PANE = 8;
  public static final int FOOTER_PANE = 9;
  public static final int HEADER_PANE = 10;
  public static final int LOGOUT_PANE = 11;

  public static final String MENU_PANE_NAME = "MenuPane";
  public static final String MAIN_PANE_NAME = "MainPane";
  public static final String SEARCH_PANE_NAME = "SearchPane";
  public static final String CONTENT_VIEW_PANE_NAME = "ContentViewPane";
  public static final String SEARCH_RESULT_DISCLOSURE_PANE_NAME = "SearchResultDisclosurePane";
  public static final String SEARCH_RESULT_PANE_NAME = "SearchResultPane";
  public static final String CONTENT_TREE_PANE_NAME = "ContentTreePane";
  public static final String DATA_SET_LIST_PANE_NAME = "DataSetListPane";
  public static final String FOOTER_PANE_NAME = "FooterPane";
  public static final String HEADER_PANE_NAME = "HeaderPane";
  public static final String WELCOME_PANE_NAME = "WelcomePane";
  public static final String LOGOUT_PANE_NAME = "LogoutPane";

  public static final String STYLE_MAIN = "template-Main";
  public static final String STYLE_CONTENT_VIEW_DIALOG_BOX = "template-ContentViewDialogBox";

  public static final String STYLE_HEADER_LABEL = "template-HeaderLabel";
  public static final String STYLE_TABLE_HEADER_LABEL = "template-TableHeaderLabel";
  public static final String STYLE_SCROLL_BORDER = "scroll-border";

  /**
   * Constructs a pane.
   * @param applicationSettings the settings shared by the panes
   */
  public Pane() {
    super();
  }

  /**
   * Returns the type of the pane.
   * @return the type of the pane
   */
  public abstract int getPaneType();

  /**
   * Returns the name of the pane.
   * @return the name of the pane
   */
  public abstract String getPaneName();

  public String getPaneStyle() {
    StringBuffer sb = new StringBuffer();
    sb.append("template-");
    sb.append(getPaneName());
    return sb.toString();
  }

  public static int getType(String name) {
    if (name.equals(HEADER_PANE_NAME)) {
      return HEADER_PANE;
    }
    if (name.equals(FOOTER_PANE_NAME)) {
      return FOOTER_PANE;
    }
    if (name.equals(SEARCH_PANE_NAME)) {
      return SEARCH_PANE;
    }
    if (name.equals(SEARCH_RESULT_PANE_NAME)) {
      return SEARCH_RESULT_PANE;
    }
    if (name.equals(SEARCH_RESULT_DISCLOSURE_PANE_NAME)) {
      return SEARCH_RESULT_DISCLOSURE_PANE;
    }
    if (name.equals(DATA_SET_LIST_PANE_NAME)) {
      return DATA_SET_LIST_PANE;
    }
    if (name.equals(CONTENT_TREE_PANE_NAME)) {
      return CONTENT_TREE_PANE;
    }
    if (name.equals(CONTENT_VIEW_PANE_NAME)) {
      return CONTENT_VIEW_PANE;
    }
    if (name.equals(MENU_PANE_NAME)) {
      return MENU_PANE;
    }
    if (name.equals(WELCOME_PANE_NAME)) {
      return WELCOME_PANE;
    }
    return -1;
  }

  @Override
  public void onResize() {
    if (getWidget() instanceof RequiresResize) {
      ((RequiresResize)getWidget()).onResize();
    }
  }
}
