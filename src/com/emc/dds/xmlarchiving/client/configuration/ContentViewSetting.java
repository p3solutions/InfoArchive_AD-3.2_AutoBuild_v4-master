/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

/**
 * Object to store a content view setting. A content view setting is a combination of stylesheet and
 * xproc pipeline. Based on its schema id, the content view setting is chosen for an XML document.
 * WARNING: This class is experimental and may change in future DDS releases.
 */
public class ContentViewSetting {

  private String[] schemaIds;
  private String pipelineURI;
  private String xslURI;
  private String exportPDFXslURI;
  private String exportPDFXprocURI;
  private String xquery;
  private String contentType;
  private String fileName;
  private String exportPDFFromDatabase;
  private String exportSRCFromDatabase;
  private boolean pdf;

  public ContentViewSetting(String pipelineURI, String xslURI, String xquery, boolean pdf,
      String schemaIds, String contentType, String fileName, String exportPDFXslURI,
      String exportPDFxprocURI, String exportPDFFromDatabase, String exportSRCFromDatabase) {
    super();
    this.pipelineURI = pipelineURI;
    this.xslURI = xslURI;
    this.exportPDFXslURI = exportPDFXslURI;
    this.xquery = xquery;
    this.pdf = pdf;
    this.schemaIds = schemaIds == null || "".equals(schemaIds) ? null : schemaIds.split(",");
    this.contentType = contentType;
    this.fileName = fileName;
    exportPDFXprocURI = exportPDFxprocURI;
    this.exportPDFFromDatabase = exportPDFFromDatabase;
    this.exportSRCFromDatabase = exportSRCFromDatabase;
  }

  public boolean appliesTo(String schemaId) {
    if (schemaIds != null && schemaIds.length > 0) {
      for (String schemaId2 : schemaIds) {
        String truncatedSchemaId = schemaId2.trim();
        if (truncatedSchemaId.equals(schemaId)) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  public String getPipelineURI() {
    return pipelineURI;
  }

  public String getXslURI() {
    return xslURI;
  }

  public String getExportPDFXslURI() {
    return exportPDFXslURI;
  }

  public String getExportPFFXprocURI() {
    return exportPDFXprocURI;
  }

  public String getExportPDFFromDatabase() {
    return exportPDFFromDatabase;
  }

  public String getExportSRCFromDatabase() {
    return exportSRCFromDatabase;
  }

  public String getXquery() {
    return xquery;
  }

  public boolean isPdf() {
    return pdf;
  }

  public String[] getSchemaIds() {
    return schemaIds;
  }

  public String getContentType() {
    if (contentType.equals("")) {
      return isPdf() ? "application/pdf" : "text/html";
    }
    return contentType;
  }

  public boolean useExternalOutput() {
    return isPdf() || !getFileName().equals("");
  }

  public String getFileName() {
    return fileName;
  }
}
