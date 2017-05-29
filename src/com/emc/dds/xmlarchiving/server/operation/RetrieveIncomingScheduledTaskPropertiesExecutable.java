/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;

public class RetrieveIncomingScheduledTaskPropertiesExecutable extends
PersistenceOperationExecutable<RetrieveIncomingScheduledTaskProperties, Map<String, String>> {

  @Override
  public Map<String, String> run(Map<String, Session> sessionMap) throws DDSException {
    Map<String, String> result = new HashMap<String, String>();
    Session session = sessionMap.get(getOperation().getStoreAlias());
    XhiveSessionIf xhiveSession = (XhiveSessionIf)session.getSession();
    String path =
        "/APPLICATIONS/" + getOperation().getApplicationName()
            + "/resources/template/template-features.xml";
    XhiveDocumentIf doc = (XhiveDocumentIf)xhiveSession.getDatabase().getRoot().getByPath(path);
    Element setting = getFirstElementByName(doc.getDocumentElement(), "handleIncoming");
    Element scheduledTask = getFirstElementByName(setting, "scheduledTask");
    String activated = "activated";
    String interval = "interval";
    String sharedlocation = "sharedlocation";
    String xmlextensions = "xmlextensions";
    String maxthreads = "maxthreads";
    result.put(activated, getElementValue(scheduledTask, activated));
    result.put(interval, getElementValue(scheduledTask, interval));
    result.put(sharedlocation, getElementValue(setting, sharedlocation));
    result.put(xmlextensions, getElementValue(setting, xmlextensions));
    result.put(maxthreads, getElementValue(setting, maxthreads));
    return result;
  }

  private String getElementValue(Element elem, String elementName) {
    NodeList elements = elem.getElementsByTagName(elementName);
    if (elements.getLength() > 0) {
      Element result = (Element)elements.item(0);
      return result.getTextContent();
    }
    return null;
  }

  private Element getFirstElementByName(Element root, String name) {
    NodeList elements = root.getElementsByTagNameNS(null, name);
    if (elements.getLength() > 0) {
      return (Element)elements.item(0);
    }
    return null;
  }
}
