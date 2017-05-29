/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.emc.dds.xmlarchiving.server.scheduling.LDMScheduledHandleIncoming;
import com.emc.dds.xmlarchiving.server.scheduling.LDMScheduledTask;
import com.emc.dds.xmlarchiving.server.scheduling.LDMScheduledUpdateXQueries;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;

public class LoadScheduledTasksOperationExecutable extends
PersistenceOperationExecutable<LoadScheduledTasksOperation, List<LDMScheduledTask>> {

  @Override
  public List<LDMScheduledTask> run(Map<String, Session> sessionMap) throws DDSException {
    List<LDMScheduledTask> result = new ArrayList<LDMScheduledTask>();
    Session session = sessionMap.get(getOperation().getStoreAlias());
    XhiveSessionIf xhiveSession = (XhiveSessionIf)session.getSession();
    String path =
        "/APPLICATIONS/" + getOperation().getApplicationName()
            + "/resources/template/template-scheduled-tasks.xml";
    XhiveDocumentIf doc = (XhiveDocumentIf)xhiveSession.getDatabase().getRoot().getByPath(path);
    NodeList scheduledTasks = doc.getElementsByTagName("scheduledTask");
    for (int k = 0; k < scheduledTasks.getLength(); k++) {
      result.add(loadScheduledTask((Element)scheduledTasks.item(k)));
    }
    return result;
  }

  private LDMScheduledTask loadScheduledTask(Element elem) {
    String type = OperationsUtil.getElementValue(elem, "type");
    String activatedStr = OperationsUtil.getElementValue(elem, "activated");
    boolean activated = activatedStr != null ? activatedStr.equals("true") : false;
    String interval = OperationsUtil.getElementValue(elem, "interval");
    String name = OperationsUtil.getElementValue(elem, "name");
    if (type != null) {
      if (type.equals("handleIncoming")) {
        String sharedLocation = OperationsUtil.getElementValue(elem, "sharedlocation");
        String xmlExtensions = OperationsUtil.getElementValue(elem, "xmlextensions");
        String maxThreadsStr = OperationsUtil.getElementValue(elem, "maxthreads");
        int maxThreads =
            maxThreadsStr == null || maxThreadsStr.equals("") ? 1 : Integer.parseInt(maxThreadsStr);
        return new LDMScheduledHandleIncoming(interval, activated, type, name, xmlExtensions,
            sharedLocation, maxThreads);
      } else if (type.equals("executeUpdateXQueries")) {
        NodeList xqueries = elem.getElementsByTagName("xquery");
        LDMScheduledUpdateXQueries task =
            new LDMScheduledUpdateXQueries(interval, activated, type, name);
        for (int k = 0; k < xqueries.getLength(); k++) {
          Element xqueryElem = (Element)xqueries.item(k);
          boolean readOnly = xqueryElem.getAttribute("readonly").equals("true");
          String xquery = OperationsUtil.getTextContent(xqueryElem);
          task.addXQuery(xquery, readOnly);
        }
        return task;
      }
    }
    return null;
  }
}
