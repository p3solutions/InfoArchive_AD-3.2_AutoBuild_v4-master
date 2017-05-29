/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.query;

/*
 * <dds:library> <dds:path>{$uri}</dds:path> <dds:segmentid>{replace($uri, '/',
 * '-')}</dds:segmentid> <dds:date>{$date}</dds:date> </dds:library>
 */
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.framework.OperationManager;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.MoveStoreChildOperation;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemStore;
import com.emc.documentum.xml.dds.user.User;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class ReadFromCentera implements XhiveXQueryExtensionFunctionIf {

  private static final String NS_URI = "http://www.emc.com/documentum/xml/dds";
  private static final String XQUERY_BEGIN = "declare namespace dds = '" + NS_URI + "';"
      + "\n/dds:detached/dds:library[dds:date = '";
  private static final String XQUERY_END = "']/dds:segmentfile";

  @Override
  public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] args) {

    FileSystemStore fsRootStore = (FileSystemStore)DDS.getApplication().getStore("xdbFSRoot");

    Store xamStore = DDS.getApplication().getStore("centera");

    if (fsRootStore == null | xamStore == null) {
      LogCenter
      .log("Archive to Centera not configured. Please configure a XAMStore with alias \"centera\" and a FileSystemStore with alias \"xdbFSRoot\" which points to the file system where the xDB segments are stored.");
      return new Object[] {};
    }

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
                Element segmentFileElement = (Element)result.next().asNode();
                if (segmentFileElement != null) {
                  String relativePath = segmentFileElement.getAttribute("ctrPath");
                  String fileName = segmentFileElement.getAttribute("ctrName");
                  StoreChild source = xamStore.getContainer(relativePath, fileName);
                  StoreChild target = fsRootStore.getContainer(relativePath, fileName);

                  if (!"".equals(fileName)) {
                    try {
                      // Only put back when the file does not yet exist.
                      // In some cases the file is already copied to the location but the file is
// not yet re-attached.
                      OperationManager operationManager =
                          DDS.getApplication().getOperationManager();
                      User user = DDS.getApplication().getApplicationUser();
                      if (!operationManager.execute(user, new ExistsStoreChildOperation(target))) {
                        DDS.getApplication()
                        .getOperationManager()
                        .execute(DDS.getApplication().getApplicationUser(),
                            new MoveStoreChildOperation(source, target, true));
                      }
                    } catch (OperationException oe) {
                      throw new RuntimeException("Failed to move file from XAM Store, location = "
                          + relativePath + ", container name = " + fileName + " to file system : "
                          + target.getCanonicalPath(), oe);
                    }
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
