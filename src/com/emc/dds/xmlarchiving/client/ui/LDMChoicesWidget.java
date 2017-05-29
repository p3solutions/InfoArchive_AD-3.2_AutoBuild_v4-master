/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import com.emc.documentum.xml.xforms.gwt.client.control.core.XFormsCoreControl;
import com.emc.documentum.xml.xforms.gwt.client.ext.ui.core.impl.Select1Widget;
import com.google.gwt.user.client.ui.Widget;

// NOTE: This customized widget correctly displays the choices retrieved by LDMChoicesListBox
// in a Select1 widget, but the selected value is not propagated correctly by a bind from that
// Select1 widget. It's unclear what the problem is. Adding a SelectionHandler (commented out
// below) did not fix the problem.
// NOTE: This is dead code

//public class LDMChoicesWidget extends Select1Widget implements SelectionHandler<Object> {
public class LDMChoicesWidget extends Select1Widget {

  private LDMChoicesListBox choicesListBox;

  public LDMChoicesWidget(XFormsCoreControl control, String xquery) {
    super(control);
    choicesListBox.setXquery(xquery);
    choicesListBox.requestChoices();
  }

  @Override
  protected Widget createWidget() {
    choicesListBox = new LDMChoicesListBox();

    choicesListBox.addFocusHandler(this);
    choicesListBox.addBlurHandler(this);
    if (getXFormsControl().isIncremental()) {
      choicesListBox.addChangeHandler(this);
    }
    return choicesListBox;
  }
/*
  @Override
  public void onSelection(SelectionEvent<Object> event) {
	System.out.println("event.getSelectedItem().toString() is " + event.getSelectedItem().toString());
	System.out.println("getXFormsControl() is " + getXFormsControl());
    getXFormsControl().setValue(event.getSelectedItem().toString());
    System.out.println("Done");
  }
 */
}
