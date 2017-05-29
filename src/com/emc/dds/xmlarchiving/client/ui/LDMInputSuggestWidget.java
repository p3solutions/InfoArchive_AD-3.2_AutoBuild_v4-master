/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import com.emc.documentum.xml.xforms.gwt.client.control.core.XFormsCoreControl;
import com.emc.documentum.xml.xforms.gwt.client.ext.ui.core.impl.InputWidget;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

// TODO Should not extend non-public class. Only done here for quick implementation.
public class LDMInputSuggestWidget extends InputWidget implements SelectionHandler<Suggestion> {

  private SuggestBox suggestBox;

  private LDMSuggestOracle suggestOracle;

  public LDMInputSuggestWidget(XFormsCoreControl control, String indexName, String indexPath) {
    super(control);
    suggestOracle.setIndexName(indexName);
    suggestOracle.setIndexPath(indexPath);
  }

  @Override
  protected Widget createWidget() {
    suggestOracle = new LDMSuggestOracle();
    suggestBox = new SuggestBox(suggestOracle);
    TextBoxBase textBox = suggestBox.getTextBox();
    if (getXFormsControl().isIncremental()) {
      suggestBox.addKeyUpHandler(this);
    } else {
      suggestBox.addValueChangeHandler(this);
    }
    suggestBox.addSelectionHandler(this);
    textBox.addBlurHandler(this);
    textBox.addFocusHandler(this);
    return suggestBox;
  }

  @Override
  public void onSelection(SelectionEvent<Suggestion> event) {
    getXFormsControl().setValue(event.getSelectedItem().getReplacementString());
  }
}
