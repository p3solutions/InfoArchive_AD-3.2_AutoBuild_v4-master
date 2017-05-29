/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.query;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class GenerateWhereClause implements XhiveXQueryExtensionFunctionIf {

  @Override
  public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] args) {
    Object obj = args[0];
    Element definition = null;
    if (obj != null) {
      XhiveXQueryValueIf value = args[0].next();
      Element input = (Element)value.asNode();
      if (input != null) {
        obj = args[1];
        value = args[1].next();
        definition = (Element)value.asNode();
        if (definition != null) {
          Map<String, SearchField> searchFileds = getSearchFields(definition);
        }
      }
    }
    return null;
  }

  private Map<String, SearchField> getSearchFields(Element elem) {
    Map<String, SearchField> searchFields = new LinkedHashMap<String, SearchField>();
    NodeList searchFieldElements = elem.getElementsByTagName("searchField");
    for (int k = 0; k < searchFieldElements.getLength(); k++) {
      Element searchFieldElement = (Element)searchFieldElements.item(k);
      String name = getSingleElementValue(searchFieldElement, "name");
      SearchField searchField = loadSearchField(searchFieldElement);
      searchFields.put(name, searchField);
    }
    return searchFields;
  }

  private SearchField loadSearchField(Element elem) {
    boolean fullText = Boolean.parseBoolean(getSingleElementValue(elem, "fullText"));
    boolean metadata = Boolean.parseBoolean(getSingleElementValue(elem, "metadata"));
    String whereClauseParameter = getSingleElementValue(elem, "whereclauseparameter");
    String label = getSingleElementValue(elem, "label");
    String operator = getSingleElementValue(elem, "operator");
    return new SearchField(fullText, whereClauseParameter, metadata, operator);
  }

  private String getSingleElementValue(Element elem, String name) {
    NodeList result = elem.getElementsByTagName(name);
    if (result.getLength() > 0) {
      return getTextContent((Element)result.item(0));
    }
    return "";
  }

  private static String getTextContent(Element element) {
    StringBuffer sb = new StringBuffer("");
    Node child = element.getFirstChild();
    while (child != null) {
      if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
        sb.append(child.getNodeValue());
      }
      child = child.getNextSibling();
    }
    return sb.toString();
  }
}

class SearchField {

  private final String xquery;
  private final String operator;
  private final boolean fullText;
  private final boolean metadata;

  SearchField(boolean fullText, String xquery, boolean metadata, String operator) {
    super();
    this.fullText = fullText;
    this.xquery = xquery;
    this.metadata = metadata;
    this.operator = operator;
  }

  String getXQuery() {
    return xquery;
  }

  boolean isFullText() {
    return fullText;
  }

  boolean isMetadata() {
    return metadata;
  }

  String getOperator() {
    if (operator == null || operator.equals("")) {
      return "=";
    }
    return operator;
  }
}
