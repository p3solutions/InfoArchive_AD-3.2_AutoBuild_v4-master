/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.emc.dds.xmlarchiving.client.authorization.Restriction;
import com.emc.dds.xmlarchiving.client.authorization.Restriction.RestrictionType;
import com.emc.dds.xmlarchiving.client.authorization.Role;
import com.emc.documentum.xml.gwt.client.xml.XMLParser;

public class RoleLoader {

  public Role getRole(String role) {
    return getRole(XMLParser.parse(role).getDocumentElement());
  }

  public Role getRole(Element role) {
    Element roleInfo = (Element)role.getFirstChild();
    if (roleInfo == null) {
      return null;
    }
    return new Role(getRestrictions(roleInfo));
  }

  private List<Restriction> getRestrictions(Element role) {
    List<Restriction> restrictions = new ArrayList<Restriction>();

    addRestrictions(role, restrictions, "node", RestrictionType.NODE);
    addRestrictions(role, restrictions, "operation", RestrictionType.OPERATION);
    addRestrictions(role, restrictions, "field", RestrictionType.FIELD);
    return restrictions;
  }

  private void addRestrictions(Element role, List<Restriction> restrictions, String elementName,
      RestrictionType restrictionType) {
    NodeList operations = role.getElementsByTagName(elementName);
    for (int k = 0; k < operations.getLength(); k++) {
      Element element = (Element)operations.item(k);
      restrictions.add(new Restriction(restrictionType, element.getFirstChild().getNodeValue()));
    }
  }
}
