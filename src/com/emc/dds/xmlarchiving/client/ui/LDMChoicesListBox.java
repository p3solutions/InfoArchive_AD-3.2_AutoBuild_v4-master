/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import java.util.List;

import com.emc.documentum.xml.dds.gwt.client.rpc.DDSServices;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXQueryValue;
import com.emc.documentum.xml.gwt.client.Dialog;
import com.emc.documentum.xml.gwt.client.FailureHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;

// NOTE: This is dead code

public class LDMChoicesListBox extends ListBox {

  private String xquery;

  public LDMChoicesListBox() {
    super();
  }

  public void setXquery(String xquery) {
    this.xquery = xquery;
  }

  public void requestChoices() {
    String xquery =
        "let $items := " + this.xquery + "\n" + "for $d in distinct-values($items)\n" + "return $d";

    DDSServices.getXQueryService().execute(null, xquery,
        new AsyncCallback<List<SerializableXQueryValue>>() {

          @Override
          public void onFailure(Throwable caught) {
            FailureHandler.handle(this, caught);
          }

          @Override
          public void onSuccess(List<SerializableXQueryValue> result) {
            int size = result.size();
            if (size > 0) {
              addItem("All", " ");
              for (int i = 0; i < size; i++) {
                addItem(result.get(i).asString());
              }
            } else {
              Dialog.alert("No matching data items found!"); // TODO: fix this
            }
          }
        });
  }
}
