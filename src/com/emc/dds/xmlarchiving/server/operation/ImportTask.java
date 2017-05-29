/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.internal.DDSConstants;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.content.BinaryContentDescriptor;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.uri.resolver.DDSURIResolver;
import com.emc.documentum.xml.dds.user.User;

public class ImportTask implements Runnable {

  private Set<String> xmlExtensions;
  private TaskLogger logger = new TaskLogger();
  private Application application;
  private Document ticketDoc;
  private String ticketName;
  private DDSURIResolver resolver;
  private String incomingDir;
  private String outgoingDir;

  protected ImportTask(Application application, String ticketName, Document ticketDoc,
      Set<String> xmlExtensions, String incomingDir, String outgoingDir) {
    this.application = application;
    this.ticketDoc = ticketDoc;
    this.ticketName = ticketName;
    this.xmlExtensions = xmlExtensions;
    resolver = new DDSURIResolver(application);
    this.incomingDir = incomingDir;
    this.outgoingDir = outgoingDir;
  }

  private String getTicketNameDone(String ticketFileName) {
    String extension = OperationsUtil.getExtension(ticketFileName);
    StringBuffer sb = new StringBuffer();
    sb.append(ticketFileName.substring(0, ticketFileName.lastIndexOf(extension) - 1));
    // add a time stamp
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    String outgoingTicketNameTimeStamp =
        dateFormat.format(date).replace("/", "_").replace(' ', '_').replace(':', '_');
    sb.append("_").append(outgoingTicketNameTimeStamp).append("_done.");
    sb.append(extension);
    return sb.toString();
  }

  @Override
  public void run() {
    logger = new TaskLogger();
    FileOutputStream fos = null;
    try {
      logger.setTicketDoc(ticketDoc, ticketName);
      handleTask();
      logger.logEndTime();
      logger.logStateFinished();
      logger.logExecutionResult();
      File ticketFile = new File(outgoingDir, logger.getTicketName());
      fos = new FileOutputStream(ticketFile);
      OperationsUtil.serializeDocument(logger.getDocument(), fos);
    } catch (Exception e) {
      if (logger != null) {
        logger.error("Import failed ", e);
      }
      LogCenter.error(this, e.getMessage());
      e.printStackTrace();
    } finally {
      try {
        if (fos != null) {
          fos.close();
        }
      } catch (IOException ioe) {
        // ignore
      }
    }
    try {
      String ticketNameDone = getTicketNameDone(logger.getTicketName());
      File ticketFileDone = new File(outgoingDir, ticketNameDone);
      File ticketFile = new File(outgoingDir, logger.getTicketName());
      if (!ticketFile.renameTo(ticketFileDone)) {
        LogCenter.warning(this, "Unable to rename file " + logger.getTicketName() + " to "
            + ticketNameDone);
      }
    } catch (Exception e) {
      LogCenter.error(this, e.getMessage());
      e.printStackTrace();
    }
  }

  private void handleTask() throws Exception {
    // check the task type
    Element taskElem = OperationsUtil.getElement(ticketDoc.getDocumentElement(), "task");
    if (taskElem != null) {
      Element importElem = OperationsUtil.getElement(taskElem, "import");
      if (importElem != null) {
        handleImport(importElem);
      } else {
        Element queriesElem = OperationsUtil.getElement(taskElem, "execute-xquery");
        if (queriesElem != null) {
          handleXQuery(queriesElem);
        } else {
          logger.error("unknown task type", null);
        }
      }
    }
  }

  private void handleImport(Element importElem) throws Exception {
    boolean unzip = true;
    String systemId = OperationsUtil.getElementValue(importElem, "systemid");
    if (systemId != null) {
      File importFile = new File(incomingDir, systemId);
      if (importFile.exists()) {
        String unzipStr = OperationsUtil.getElementValue(importElem, "unzip");
        if (unzipStr != null) {
          unzip = Boolean.parseBoolean(unzipStr);
        }
        String location = OperationsUtil.getElementValue(importElem, "location");
        if (location != null) {
          // first handle detachable libraries
          handleDetachableLibraries(importElem);
          if (unzip) {
            try (ZipFile zipFile = new ZipFile(importFile)) {
              handleZipFile(importFile, zipFile, location);
            }
          } else {
            handleFile(importFile, location);
          }
        } else {
          logger.error("Location must have a value", null);
        }
        if (!OperationsUtil.deleteFile(importFile)) {
          logger.warning("Unable to delete zip file in incoming dir");
        }
      } else {
        logger.error("missing zip file " + systemId, null);
      }
    }
  }

  private void handleDetachableLibraries(Element importElem) throws Exception {
    Element detachableLibraries = OperationsUtil.getElement(importElem, "detachablelibraries");
    if (detachableLibraries != null) {
      Node child = detachableLibraries.getFirstChild();
      while (child != null) {
        if (child instanceof Element && child.getLocalName().equals("detachablelibrary")) {
          Element detachableLibrary = (Element)child;
          String locationURI = OperationsUtil.getElementValue(detachableLibrary, "location");
          DDSURI uri = DDSURI.parseURI(locationURI);
          String path = uri.getDomainSpecificPart();
          String store = uri.getAttribute(DDSURIResolver.ATTRIBUTE_STORE);
          if (path.lastIndexOf('/') != path.length() - 1) {
            logger.error("Detachable library uri does not represent a Location: " + locationURI,
                null);
          } else if (!uri.getAttribute(DDSURIResolver.ATTRIBUTE_DOMAIN).equals(
              DDSURIResolver.DOMAIN_DATA)) {
            logger
                .error("Detachable library only supported for DOMAIN=data : " + locationURI, null);
          } else if (store != null && !store.equals(application.getMainStore().getAlias())) {
            logger.error("Detachable libraries only supported in store "
                + application.getMainStore().getAlias(), null);
          } else {
            String libPath = path.substring(0, path.length() - 1);
            int index = libPath.lastIndexOf('/');
            String parentPath = index == 0 ? "/" : path.substring(0, index);
            String name =
                index == 0 ? libPath.substring(1) : libPath.substring(index + 1, libPath.length());
            String fullParentPath = createFullXDBPath(uri, parentPath);
            String createIndexes =
                OperationsUtil.getElementValue(detachableLibrary, "createindexes");
            application.execute(application.getApplicationUser(),
                new CreateDetachableLibraryOperation(application.getMainStore(), fullParentPath,
                    name, createIndexes == null ? true : createIndexes.equals("true")));
          }
        }
        child = child.getNextSibling();
      }
    }
  }

  private String createFullXDBPath(DDSURI uri, String parentPath) {
    StringBuffer sb = new StringBuffer();
    sb.append(OperationsUtil.getLibraryPath(uri.getAttribute(DDSURIResolver.ATTRIBUTE_DATASET)));
    String locale = uri.getAttribute(DDSURIResolver.ATTRIBUTE_LOCALE);
    if (locale != null && !locale.equals("")) {
      sb.append('/');
      sb.append(locale);
    }
    sb.append(parentPath);
    return sb.toString();

  }

  private void handleFile(File file, String locationStr) throws IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
      String info = "Import file \"" + file.getName() + "\" into location \"" + locationStr + "\"";
      importResource(locationStr + file.getName(), fis, info);
    }
  }

  private void handleZipFile(File file, ZipFile zipFile, String locationStr) throws IOException {
    InputStream zipInputStream = null;

    try (FileInputStream fis = new FileInputStream(file);
        ZipInputStream zis = new ZipInputStream(fis)) {
      String info = null;

      for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
        if (!zipEntry.isDirectory()) {
          final String path = zipEntry.getName();
          zipInputStream = zipFile.getInputStream(zipEntry);
          info = "Import zip entry \"" + path + "\" into location \"" + locationStr + "\"";
          String uri = locationStr + getStorePath(path);
          importResource(uri, zipInputStream, info);
        }
      }
    } finally {
      if (zipInputStream != null) {
        zipInputStream.close();
      }
    }
  }

  private void checkAndCreateLocation(Store store, String uri) throws Exception {
    User user = application.getApplicationUser();
    application.execute(user, new CreateMissingLocationOperation(store, uri));
  }

  private void importResource(String uri, InputStream is, String info) {
    boolean isXML = xmlExtensions.contains(OperationsUtil.getExtension(uri));
    User user = application.getApplicationUser();
    try {
      DDSURI ddsURI = DDSURI.parseURI(uri);
      String storeStr = ddsURI.getAttribute(DDSURIResolver.ATTRIBUTE_STORE);
      Store store =
          storeStr == null || storeStr.equals("") ? application.getMainStore() : application
              .getStore(storeStr);
      checkAndCreateLocation(store, uri);
      URITarget targetContainer = resolver.resolveURI(ddsURI, application.getApplicationUser());
      Container container = (Container)targetContainer.getStoreChild();
      ContentDescriptor descriptor =
          isXML ? new XMLContentDescriptor() : new BinaryContentDescriptor();
      application.execute(user, new PersistOperation(container, descriptor,
          new InputStreamData(is), true));
      logger.log(info + " completed succesfully");
    } catch (Exception e) {
      logger.error(info + " failed", e);
    }
  }

  private String getStorePath(String systemId) {
    return systemId.replace('\\', '/');
  }

  private void handleXQuery(Element xqueryElem) {

  }

  private class TaskLogger {

    private Document ticketDoc;
    private String ticketName;
    private Element result;
    private Element log;
    private Element errors;
    private Element warnings;
    private Text status;

    void setTicketDoc(Document ticketDocument, String name) {
      ticketDoc = ticketDocument;
      Element docElem = ticketDocument.getDocumentElement();
      result = insertElement(docElem, "result");
      log = insertElement(result, "log");
      errors = insertElement(result, "errors");
      warnings = insertElement(result, "warnings");
      status = (Text)insertElement(result, "status", "started").getFirstChild();
      ticketName = name;
      logStartTime();
    }

    void error(String error, Exception exception) {
      Element errorElt = insertElement(errors, "error");
      insertElement(errorElt, "message", error);
      if (exception != null) {
        StringBuffer sb = new StringBuffer();
        Throwable t = exception;
        while (t != null) {
          sb.append(t.toString());
          t = t.getCause();
          if (t != null) {
            sb.append("\n Caused by: ");
          }
        }
        exception.printStackTrace();
        insertElement(errorElt, "exception", sb.toString());
      }
    }

    void log(String message) {
      insertElement(log, "message", message);
    }

    void warning(String message) {
      insertElement(warnings, "warning", message);
    }

    void logStartTime() {
      insertElement(result, "startime", new Date().toString());
    }

    void logEndTime() {
      insertElement(result, "endtime", new Date().toString());
    }

    void logStateFinished() {
      status.setData("finished");
    }

    void logExecutionResult() {
      insertElement(result, "executionresult", errors.getFirstChild() == null ? "success" : "error");
    }

    Document getDocument() {
      return ticketDoc;
    }

    String getTicketName() {
      return ticketName;
    }

    private Element insertElement(Element parent, String name) {
      Element child = ticketDoc.createElementNS(DDSConstants.DDS_NAMESPACE_URI, name);
      parent.appendChild(child);
      return child;
    }

    private Element insertElement(Element parent, String name, String value) {
      Element child = insertElement(parent, name);
      Text text = ticketDoc.createTextNode(value);
      child.appendChild(text);
      return child;
    }
  }

}
