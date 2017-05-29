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
import com.xhive.query.interfaces.XhiveXQueryValueIf;
import com.xhive.util.interfaces.IterableIterator;

public class RetrieveScheduledTaskPropertiesExecutable extends
PersistenceOperationExecutable<RetrieveScheduledTaskProperties, Map<String, String>> {

  @Override
  public Map<String, String> run(Map<String, Session> sessionMap) throws DDSException {
    Map<String, String> result = new HashMap<String, String>();
    Session session = sessionMap.get(getOperation().getStoreAlias());
    XhiveSessionIf xhiveSession = (XhiveSessionIf)session.getSession();
    String xquery =
        "doc('/APPLICATIONS/" + getOperation().getApplicationName()
            + "/resources/template/template-features.xml')/features/ "
            + getOperation().getScheduledTaskName() + "/scheduledTask";
    IterableIterator<? extends XhiveXQueryValueIf> values =
        xhiveSession.getDatabase().getRoot().executeXQuery(xquery);
    if (values.hasNext()) {
      Element elem = (Element)((XhiveXQueryValueIf)values.next()).asNode();
      String activated = "activated";
      result.put(activated, getElementValue(elem, activated));
      String interval = "interval";
      result.put(interval, getElementValue(elem, interval));
    }
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
}
