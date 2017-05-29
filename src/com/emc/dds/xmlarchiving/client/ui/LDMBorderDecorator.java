/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.Widget;

/**
 * Border decorator widget to add flexibility to the user interface styling.
 */
public class LDMBorderDecorator extends Composite {

  private Grid grid;

  /**
   * Constructs a border decorator. Creates a full border (top, middle, bottom, left, center,
   * right). Sets the primary style name to "core-LDMBorderDecorator".
   * @param widget the widget around which to add a border
   */
  public LDMBorderDecorator(Widget widget) {
    this(widget, null);
  }

  /**
   * Constructs a border decorator. Creates a full border (top, middle, bottom, left, center,
   * right).
   * @param widget the widget around which to add a border
   * @param styleName the primary style name to use
   */
  public LDMBorderDecorator(Widget widget, String styleName) {
    this(styleName);
    widget.setSize("100%", "100%");
    grid.setWidget(1, 1, widget);
  }

  /**
   * Constructs a border decorator. Creates a full border (top, middle, bottom, left, center,
   * right).
   * @param title title
   * @param widget the widget around which to add a border
   */
  public LDMBorderDecorator(String title, Widget widget) {
    this(widget);
    setTopText(title, false);
  }

  /**
   * Constructs a border decorator. Creates a full border (top, middle, bottom, left, center,
   * right).
   * @param widget the widget around which to add a border
   * @param styleName the primary style name to use
   */
  public LDMBorderDecorator(String title, Widget widget, String styleName) {
    this(widget, styleName);
    setTopText(title, false);
  }

  /**
   * Constructs a border decorator.
   * @param text the text around which to add a border
   * @param styleName the primary style name to use
   */
  public LDMBorderDecorator(String title, String styleName) {
    this(styleName);
    setTopText(title, false);
  }

  /**
   * Constructs a border decorator.
   * @param text the text around which to add a border
   * @param styleName the primary style name to use
   * @param asHTML specifies whether the label should be treated as HTML content
   */
  public LDMBorderDecorator(String title, String styleName, boolean asHTML) {
    this(styleName);
    setTopText(title, asHTML);
  }

  private LDMBorderDecorator(String styleName) {
    String primaryStyleName = styleName == null ? "core-LDMBorderDecorator" : styleName;
    grid = new Grid(4, 3);
    setStylePrimaryName(0, primaryStyleName + "-top");
    setStylePrimaryName(1, primaryStyleName + "-middle");
    setStylePrimaryName(2, primaryStyleName + "-footer");
    setStylePrimaryName(3, primaryStyleName + "-bottom");
    CellFormatter cellFormatter = grid.getCellFormatter();
    initRow(cellFormatter, 0);
    initInner(cellFormatter, 1, 0);
    initInner(cellFormatter, 1, 2);
    initRow(cellFormatter, 2);
    initRow(cellFormatter, 3);
    addStyleToColumn(0, "left");
    addStyleToColumn(1, "center");
    addStyleToColumn(2, "right");
    grid.setCellPadding(0);
    grid.setCellSpacing(0);
    initWidget(grid);
    addStyleName(primaryStyleName);
    getElement().getStyle().setTableLayout(TableLayout.FIXED);
  }

  public void setTopText(String title, boolean asHTML) {
    if (asHTML) {
      grid.setHTML(0, 1, title);
    } else {
      grid.setText(0, 1, title);
    }
  }

  /**
   * Sets the widget contained within the border.
   * @param widget the widget to put inside the border
   */
  @Override
  public void setWidget(Widget widget) {
    grid.setWidget(1, 1, widget);
  }

  /**
   * Returns the widget contained within the border.
   * @return the widget contained within the border or <code>null</code> if it contains no widget
   */
  @Override
  public Widget getWidget() {
    return grid.getWidget(1, 1);
  }

  /**
   * Returns the text contained within the border.
   * @return the text contained within the border or <code>null</code> if it contains no text
   */
  public String getText() {
    return grid.getText(1, 1);
  }

  /**
   * Returns the HTML contained within the border.
   * @return the HTML contained within the border or <code>null</code> if it contains no HTML
   */
  public String getHTML() {
    return grid.getHTML(1, 1);
  }

  private void setStylePrimaryName(int row, String style) {
    grid.getRowFormatter().setStylePrimaryName(row, style);
    CellFormatter cellFormatter = grid.getCellFormatter();
    grid.getRowFormatter().addStyleName(row, style);
    for (int i = grid.getColumnCount() - 1; i >= 0; --i) {
      cellFormatter.setStylePrimaryName(row, i, style);
    }
  }

  private void addStyleToColumn(int column, String style) {
    CellFormatter cellFormatter = grid.getCellFormatter();
    for (int i = grid.getRowCount() - 1; i >= 0; --i) {
      String primaryName = cellFormatter.getStylePrimaryName(i, column);
      cellFormatter.addStyleName(i, column, primaryName + '-' + style);
    }
  }

  public void setFooterWidget(Widget footerWidget) {
    grid.setWidget(2, 1, footerWidget);
  }

  private void initRow(CellFormatter cellFormatter, int row) {
    initInner(cellFormatter, row, 0);
    initInner(cellFormatter, row, 1);
    initInner(cellFormatter, row, 2);
  }

  private void initInner(CellFormatter cellFormatter, int row, int column) {
    Element element = cellFormatter.getElement(row, column);
    DOM.setInnerHTML(element, "<div style='width:1px;'></div>");
  }
}
