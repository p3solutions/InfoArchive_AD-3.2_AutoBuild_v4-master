/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.query;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.emc.dds.xmlarchiving.server.operation.OperationsUtil;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class AttachLibraries implements XhiveXQueryExtensionFunctionIf {

  private static final String NS_URI = "http://www.emc.com/documentum/xml/dds";
  private static final String XQUERY_BEGIN = "declare namespace dds = '" + NS_URI + "';"
      + "\n/dds:detached/dds:library[dds:date = '";
  private static final String XQUERY_END = "']";

  @Override
  public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] args) {
    Object obj = args[0];
    XhiveDocumentIf doc = null;
    if (obj != null) {
      XhiveXQueryValueIf value = args[0].next();
      Element input = (Element)value.asNode();
      if (input != null) {
        obj = args[1];
        value = args[1].next();
        doc = (XhiveDocumentIf)value.asNode();
        if (doc != null) {
          try {
            NodeList items = input.getElementsByTagName("result");
            for (int k = 0; k < items.getLength(); k++) {
              Element resultElem = (Element)items.item(k);
              String date = resultElem.getAttribute("date");
              Iterator<XhiveXQueryValueIf> result = doc.executeXQuery(getXQuery(date));
              if (result.hasNext()) {
                Element detachedInfo = (Element)result.next().asNode();
                if (detachedInfo != null) {
                  String path = OperationsUtil.getElementValue(detachedInfo, NS_URI, "path");
                  String segmentId =
                      OperationsUtil.getElementValue(detachedInfo, NS_URI, "segmentid");
                  int index = path.lastIndexOf('/');
                  String parentPath = index == 0 ? "/" : path.substring(0, index);
                  XhiveLibraryIf parentLibrary =
                      (XhiveLibraryIf)doc.getOwnerLibrary().getByPath(parentPath);
                  if (parentLibrary != null) {
                    XhiveLibraryIf library = parentLibrary.attach(segmentId);
                    parentLibrary.appendChild(library);
                    // remove corresponding element in detached.xml
                    detachedInfo.getParentNode().removeChild(detachedInfo);
                  }
                }
              }
            }
          } catch (Exception e) {
            throw new RuntimeException("Error in DetachLibraries extension function", e);
          }
        }
      }
    }
    return new Object[] {};
  }

  private String getXQuery(String date) {
    StringBuffer sb = new StringBuffer();
    sb.append(XQUERY_BEGIN);
    sb.append(date);
    sb.append(XQUERY_END);
    return sb.toString();
  }
}
