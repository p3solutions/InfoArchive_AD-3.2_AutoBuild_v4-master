/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import java.util.List;

import com.emc.dds.xmlarchiving.client.ui.image.MainImageBundle;
import com.emc.documentum.xml.dds.gwt.client.rpc.DDSServices;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableLibraryChild;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableNode;
import com.emc.documentum.xml.dds.gwt.client.util.DDSURI;
import com.emc.documentum.xml.gwt.client.Dialog;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public class LDMSuggestOracle extends MultiWordSuggestOracle implements
    AsyncCallback<List<SerializableNode>> {

  private String indexName;

  private String indexPath;

  private boolean initialized;

  private Callback callback;

  private Request request;

  public LDMSuggestOracle() {
    super();
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public void setIndexPath(String indexPath) {
    this.indexPath = indexPath;
  }

  @Override
  public void requestSuggestions(final Request req, final Callback callbck) {
    if (initialized) {
      super.requestSuggestions(req, callbck);
    } else {
      if (indexName.contains("Hold")) {
        initialized = true;
      }

      request = req;
      callback = callbck;

      final String uri = DDSURI.SCHEME_DDS + ":" + indexPath;
      DDSServices.getIndexService().getKeys(uri, indexName, new AsyncCallback<List<String>>() {

        @Override
        public void onSuccess(List<String> result) {
          if (!result.isEmpty()) {
            addAll(result);
            LDMSuggestOracle.super.requestSuggestions(req, callbck);
          } else {
            getChildren();
          }
        }

        @Override
        public void onFailure(Throwable caught) {
          Dialog.alert(caught.getMessage(), MainImageBundle.INSTANCE.error48().createImage());
        }
      });
    }
  }

  private String getDataSetName() {
    final String dataStr = "/DATA/";
    int indexData = indexPath.indexOf(dataStr);
    if (indexData == 0) {
      int indexCollection = indexPath.indexOf("/Collection/");
      if (indexCollection != -1) {
        return indexPath.substring(dataStr.length(), indexCollection);
      }
    }
    return null;

  }

  private void getChildren() {
    String dataSetName = getDataSetName();
    if (dataSetName != null) {
      DDSURI ddsUri = new DDSURI("/");
      ddsUri.setAttribute(DDSURI.ATTRIBUTE_DATASET, dataSetName);
      DDSServices.getXMLPersistenceService().getChildren(ddsUri.toString(),
          SerializableNode.LIBRARY_NODE, this);
    }
  }

  @Override
  public void onFailure(Throwable caught) {
    Dialog.alert(caught.getMessage(), MainImageBundle.INSTANCE.error48().createImage());
  }

  @Override
  public void onSuccess(final List<SerializableNode> result) {
    for (SerializableNode serializableNode : result) {
      SerializableLibraryChild libChild = (SerializableLibraryChild)serializableNode;
      String uri = indexPath + libChild.getName() + "/";
      DDSServices.getIndexService().getKeys(uri, indexName, new AsyncCallback<List<String>>() {

        @Override
        public void onSuccess(List<String> result) {
          addAll(result);
          LDMSuggestOracle.super.requestSuggestions(request, callback);
        }

        @Override
        public void onFailure(Throwable caught) {
          Dialog.alert(caught.getMessage(), MainImageBundle.INSTANCE.error48().createImage());
        }
      });
    }
  }

}
