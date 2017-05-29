/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import com.emc.dds.xmlarchiving.client.configuration.ContentViewSetting;
import com.emc.documentum.xml.gwt.client.ui.DialogBox;
import com.emc.documentum.xml.xproc.gwt.client.rpc.SerializablePipelineInput;
import com.emc.documentum.xml.xproc.gwt.client.rpc.SerializableSource;
import com.emc.documentum.xml.xproc.gwt.client.ui.XProcFrame;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * The content view dialog box shows the HTML or PDF of an XML document. The settings of the
 * stylesheets and xproc pipelines used are configurable. WARNING: This class is experimental and
 * may change in future DDS releases.
 */
public class ContentViewDialogBox extends DialogBox implements CloseHandler<PopupPanel> {

  private final String title;
  private final ContentViewSetting previewSetting;
  private final String uri;
  private String xml;
  private final AbstractSearchResultPane ownerPane;

  public ContentViewDialogBox(AbstractSearchResultPane ownerPane,
      ContentViewSetting previewSetting, String title, String uri) {
    this.title = title;
    this.uri = uri;
    this.previewSetting = previewSetting;
    this.ownerPane = ownerPane;
    showContent();
    addCloseHandler(this);
  }

  public ContentViewDialogBox(AbstractSearchResultPane ownerPane,
      ContentViewSetting previewSetting, String title, String uri, String xml) {
    this.title = title;
    this.uri = uri;
    this.xml = xml;
    this.previewSetting = previewSetting;
    this.ownerPane = ownerPane;
    showContent();
    addCloseHandler(this);
  }

  private void showContent() {
    setText(title);
    String contentType = previewSetting.isPdf() ? "application/pdf" : "text/html";
    XProcFrame xprocFrame = new XProcFrame();
    SerializablePipelineInput input = new SerializablePipelineInput();
    if (xml == null) {
      input.addInput("source", new SerializableSource(uri));
    } else {
      input.addInput("source", new SerializableSource(xml, null, null));
    }
    input.addInput("stylesheet", new SerializableSource(previewSetting.getXslURI()));
    xprocFrame.setPipeline(previewSetting.getPipelineURI());
    xprocFrame.setPipelineInput(input);
    xprocFrame.setContentType(contentType);
    xprocFrame.setUseExternalOutput(previewSetting.isPdf());
    xprocFrame.refresh();
    add(xprocFrame);
  }

  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    ownerPane.unselectItems();
  }
}
