/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.internal.DDSConstants;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;

public class HandleIncomingOperationExecutable extends
PersistenceOperationExecutable<HandleIncomingOperation, Object> {

  protected static final String OPERATION_NAME = "handle incoming";

  private String incomingDir;
  private String outgoingDir;
  private Set<String> xmlExtensions;
  private List<String> ticketNames = new ArrayList<String>();
  private List<Document> ticketDocs = new ArrayList<Document>();
  private int maxThreads;

  @Override
  public Object run(Map<String, Session> sessionMap) throws DDSException {
    String sharedLocation = getOperation().getSharedLocation();
    File sharedLocationDir = new File(sharedLocation);
    if (!sharedLocationDir.exists()) {
      if (!sharedLocationDir.mkdir()) {
        throw new DDSException("Unable to create location " + sharedLocation);
      }
    }
    incomingDir = sharedLocation + "/incoming";
    outgoingDir = sharedLocation + "/outgoing";
    Date date = new Date();
    maxThreads = getOperation().getMaxThreads();
    xmlExtensions = OperationsUtil.stringAsSet(getOperation().getXmlextensions());
    LogCenter.log("Start handling incoming jobs \"" + getOperation().getId() + "\" time: " + date);
    handleIncomingJobs(date);
    return Boolean.TRUE;
  }

  private void handleIncomingJobs(Date date) {
    if (hasRunningTasks()) {
      LogCenter.log("Skip handling incoming jobs " + getOperation().getId() + " time: " + date
          + " because other tasks are still active");
    } else {
      getIncomingTickets();
      if (ticketNames.size() > 0) {
        handleIncomingTasks();
      }
    }
  }

  private boolean hasRunningTasks() {
    File outgoingDirFile = new File(outgoingDir);
    if (!outgoingDirFile.exists()) {
      outgoingDirFile.mkdirs();
    }

    FileFilter xmlFileFilter = new FileFilter() {

      @Override
      public boolean accept(File pathname) {
        return hasXMLExtension(pathname.getName());
      }
    };

    File[] xmlFiles = outgoingDirFile.listFiles(xmlFileFilter);

    // check if a ticket is uploaded to the incoming directory
    for (File xmlFile : xmlFiles) {
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(xmlFile);
        Document ticketDoc = OperationsUtil.parseDocument(fis);
        if (isTicketDocument(ticketDoc)) {
          String status = OperationsUtil.getElementValue(ticketDoc.getDocumentElement(), "status");
          if (status != null) {
            return !status.equals("finished");
          }
        }
      } catch (Exception e) {
        LogCenter.error(this, "Exception thrown while checking ticket documents");
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (IOException ioe) {
            // ignore
          }
        }
      }
    }

    return false;
  }

  private void getIncomingTickets() {
    // check if shared dir exists
    File incomingDirFile = new File(incomingDir);
    if (!incomingDirFile.exists()) {
      incomingDirFile.mkdirs();
    }

    FileFilter xmlFileFilter = new FileFilter() {

      @Override
      public boolean accept(File pathname) {
        return hasXMLExtension(pathname.getName());
      }
    };

    File[] xmlFiles = incomingDirFile.listFiles(xmlFileFilter);

    // check if a ticket is uploaded to the incoming directory
    for (File xmlFile : xmlFiles) {
      FileInputStream fis = null;
      FileOutputStream fos = null;
      try {
        fis = new FileInputStream(xmlFile);
        TicketErrorHandler errorHandler = new TicketErrorHandler();
        Document ticketDoc = OperationsUtil.parseDocument(fis, true, "ticket.xsd", errorHandler);
        if (!errorHandler.getErrors().isEmpty()) {
          LogCenter.error("Validation errors " + xmlFile.getAbsolutePath() + ": "
              + errorHandler.getErrors());
        } else {
          String ticketName = xmlFile.getName();
          ticketDocs.add(ticketDoc);
          ticketNames.add(ticketName);
          // create job tickets with a time stamp
          DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
          Date date = new Date();
          String outgoingTicketName = dateFormat.format(date);
          outgoingTicketName =
              ticketName.substring(0, ticketName.indexOf(".xml")) + "_"
                  + outgoingTicketName.replace("/", "_").replace(' ', '_').replace(':', '_')
                  + ".xml";
          File outgoingFile = new File(outgoingDir, outgoingTicketName);
          fos = new FileOutputStream(outgoingFile);
          // copy the ticket to the outgoing folder
          OperationsUtil.serializeDocument(ticketDoc, fos);
          if (!OperationsUtil.deleteFile(xmlFile)) {
            LogCenter.warning("Unable to delete file " + xmlFile.getAbsolutePath());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        LogCenter.error(this, "Exception thrown while reading tickets: " + e.getMessage());
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (IOException ioe) {
            // ignore
          }
        }
        if (fos != null) {
          try {
            fos.close();
          } catch (IOException ioe) {
            // ignore
          }
        }
      }
    }
  }

  private void handleIncomingTasks() {
    final int numTasks = ticketNames.size();
    int numThreads = 0;
    int numTasksFinished = 0;
    int numTasksStarted = 0;
    List<Thread> threads = new ArrayList<Thread>();

    while (numTasksFinished < numTasks) {
      while (numTasksStarted < numTasks && maxThreads > numThreads) {
        Thread newThread =
            new Thread(new ImportTask(getApplication(), ticketNames.get(numTasksStarted),
                ticketDocs.get(numTasksStarted), xmlExtensions, incomingDir, outgoingDir));
        threads.add(newThread);
        newThread.start();
        numTasksStarted++;
        numThreads++;
      }
      int k = 0;
      while (k < threads.size()) {
        Thread thread = threads.get(k);
        if (!thread.isAlive()) {
          numThreads--;
          numTasksFinished++;
          threads.remove(thread);
        } else {
          k++;
        }
      }
    }
  }

  private boolean hasXMLExtension(String fileName) {
    String extension = OperationsUtil.getExtension(fileName);
    return extension != null && xmlExtensions.contains(extension);
  }

  private boolean isTicketDocument(Document xmlDoc) {
    Element docElem = xmlDoc.getDocumentElement();
    return docElem.getLocalName().equals("ticket")
        && docElem.getNamespaceURI().equals(DDSConstants.DDS_NAMESPACE_URI);
  }

  private class TicketErrorHandler implements DOMErrorHandler {

    private StringBuffer errors = new StringBuffer("");

    @Override
    public boolean handleError(DOMError error) {
      if (errors.length() > 0) {
        errors.append(", \n");
      }
      errors.append(error.getMessage());
      return false;
    }

    public String getErrors() {
      return errors.toString();
    }
  }

}
