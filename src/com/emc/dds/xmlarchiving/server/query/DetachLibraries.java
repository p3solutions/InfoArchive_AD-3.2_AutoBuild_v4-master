/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.query;

/*
 * <dds:library> <dds:path>{$uri}</dds:path> <dds:segmentid>{replace($uri, '/',
 * '-')}</dds:segmentid> <dds:date>{$date}</dds:date> </dds:library>
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.emc.dds.xmlarchiving.server.operation.OperationsUtil;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveFileIf;
import com.xhive.core.interfaces.XhiveSegmentIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveLibraryIf.LibraryState;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class DetachLibraries implements XhiveXQueryExtensionFunctionIf {

  private static final String NS_URI = "http://www.emc.com/documentum/xml/dds";

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
            for (String uri : getUris(input)) {
              XhiveLibraryIf library = (XhiveLibraryIf)doc.getOwnerLibrary().getByPath(uri);
              if (library != null) {
                if (library.isDetachable()) {
                  String libName = library.getName();
                  XhiveLibraryIf detachableLib = library;
                  detachableLib.setState(LibraryState.READ_ONLY);
                  Collection<String> segmentIds = detachableLib.detach();
                  insertDetachInfo(doc, uri, libName, segmentIds);
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

  private List<String> getUris(Element input) {
    ArrayList<String> uris = new ArrayList<String>();
    NodeList result = input.getElementsByTagName("result");
    for (int k = 0; k < result.getLength(); k++) {
      Element resultElem = (Element)result.item(k);
      uris.add(resultElem.getAttribute("uri"));
    }
    return uris;
  }

  private void insertDetachInfo(XhiveDocumentIf document, String uri, String date,
      Collection<String> segmentIds) {
    Element detachNode = document.createElementNS(NS_URI, "library");
    OperationsUtil.addElement(document, detachNode, NS_URI, "path", uri);
    if (!segmentIds.isEmpty()) {
      Iterator<String> idIterator = segmentIds.iterator();
      String segmentId = idIterator.next();
      // Only store the first Segment Id, which is the "root" segment of the detachable library. All
      // sublibraries or other segments will automatically be detached/attached along with this
      // segmentId.
      OperationsUtil.addElement(document, detachNode, NS_URI, "segmentid", segmentId);
      // However we do need to get hold of all the segment files for archiving to Centera.
      do {
        XhiveDatabaseIf db = document.getOwnerLibrary().getDatabase();
        XhiveSegmentIf segment = db.getSegment(segmentId);
        for (XhiveFileIf file : segment.getFiles()) {
          OperationsUtil
          .addElement(document, detachNode, NS_URI, "segmentfile", file.getFileName());
        }
        segmentId = idIterator.hasNext() ? idIterator.next() : null;
      } while (segmentId != null);
    }
    OperationsUtil.addElement(document, detachNode, NS_URI, "date", date);
    document.getDocumentElement().appendChild(detachNode);
  }
}
