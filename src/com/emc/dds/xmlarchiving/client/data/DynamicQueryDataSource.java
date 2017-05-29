/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.data;

import java.util.List;
import java.util.Map;

import com.emc.documentum.xml.dds.gwt.client.rpc.DDSServices;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableNode;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXQueryValue;
import com.emc.documentum.xml.gwt.client.Dialog;
import com.emc.documentum.xml.gwt.client.FailureHandler;
import com.emc.documentum.xml.xforms.gwt.client.model.XFormsSubmission;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Retrieves distinct values from the database corresponding to an XPath statement.
 */
public class DynamicQueryDataSource {

  XFormsSubmission submission;

  public DynamicQueryDataSource(XFormsSubmission submission) {
    super();
    this.submission = submission;
  }

  public void processRequest(String xquery, Map<String, String> parameterMap) {

    Boolean readOnly = true;

    DDSServices.getXQueryService().execute(null, xquery, parameterMap, readOnly,
        new AsyncCallback<List<SerializableXQueryValue>>() {

      @Override
      public void onFailure(Throwable caught) {
        FailureHandler.handle(this, caught);
      }

      @Override
      public void onSuccess(List<SerializableXQueryValue> result) {
        int size = result.size();
        Boolean leafNodes = false;
        StringBuffer sb = new StringBuffer();

        if (size > 0) {
          // Size tells us how many root items there are - more than 1 is non-hierarchical
          // so we have to wrap them
          if (size > 1) {

            leafNodes = true;
          }
          if (leafNodes) {
            sb.append("<data><choice show='All'/>");
          }

          for (SerializableXQueryValue value : result) {
            if (value != null) {
              String content = value.asString();

              if (leafNodes) {
                sb.append("<choice show='" + content + "'>" + content + "</choice>");
              } else {
                sb.append(content);
              }
            }
          }
          if (leafNodes) {
            sb.append("</data>");
          }
          // SearchPane.java uses -1, probably should use 200 instead below
          // See com.google.gwt.http.client.Response.class
          submission.onSubmitDone(sb.toString(), -1, null, null);
        } else {
          Dialog.alert("No data items found matching dynamic XForms request!");
          // SearchPane.java uses -1, probably should use 200 instead below
          // See com.google.gwt.http.client.Response.class
          submission.onSubmitError(XFormsSubmission.ErrorType.NO_DATA, -1, null,
              "No data items found matching dynamic XForms request!", "<data/>");
        }
      }
    });
  }

  public static String taggedNode(SerializableNode node) {
    String tag = node.getNodeName();
    return '<' + tag + '>' + node.toString() + "</" + tag + '>';
  }
}
