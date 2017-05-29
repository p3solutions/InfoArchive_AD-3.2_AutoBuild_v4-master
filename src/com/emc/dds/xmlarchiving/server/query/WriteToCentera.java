/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.query;

import java.util.Iterator;

import org.w3c.dom.Element;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.persistence.CopyStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.CreateLocationOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.DeleteStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemStore;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class WriteToCentera implements XhiveXQueryExtensionFunctionIf {

  @Override
  public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] args) {

    FileSystemStore fsRootStore = (FileSystemStore)DDS.getApplication().getStore("xdbFSRoot");
    Store xamStore = DDS.getApplication().getStore("centera");

    if (fsRootStore == null | xamStore == null) {
      LogCenter
      .log("Archive to Centera not configured. Please configure a XAMStore with alias \"centera\" and a FileSystemStore with alias \"xdbFSRoot\" which points to the file system where the xDB segments are stored.");
      return new Object[] {};
    }

    Iterator<? extends XhiveXQueryValueIf> iterator = args[0];

    if (iterator != null) {

      while (iterator.hasNext()) {

        Element element = (Element)iterator.next().asNode();
        String fileFullPath = element.getTextContent();

        if (!fileFullPath.startsWith(fsRootStore.getPrefix())) {
          throw new RuntimeException(
              "Cannot archive the detached segments to Centera. Check that a "
                  + "FileSystemStore with name \"xdbFSRoot\" is properly "
                  + "defined and points to the drive where the segments of the "
                  + "xDB Store are located.");
        }

        String fileName =
            fileFullPath.substring(fileFullPath.lastIndexOf(fsRootStore.getSeparator()) + 1);
        String relativePath =
            fileFullPath.substring(fsRootStore.getPrefix().length(),
                fileFullPath.lastIndexOf(fsRootStore.getSeparator()));
        relativePath = relativePath.replace(fsRootStore.getSeparator(), "/");

        Container source = fsRootStore.getContainer(relativePath, fileName);
        Container target = xamStore.getContainer(relativePath, fileName);

        try {

          // Ensure parent location exists
          Location targetLocation = target.getLocation();
          if (!targetLocation.isRoot()) {
            boolean exists =
                DDS.getApplication()
                    .getOperationManager()
                    .execute(DDS.getApplication().getApplicationUser(),
                        new ExistsStoreChildOperation(targetLocation));
            if (!exists) {
              DDS.getApplication()
              .getOperationManager()
              .execute(DDS.getApplication().getApplicationUser(),
                  new CreateLocationOperation(targetLocation, null, true));
            }

            DDS.getApplication()
            .getOperationManager()
            .execute(DDS.getApplication().getApplicationUser(),
                new CopyStoreChildOperation(source, target, true));
          }
        } catch (OperationException oe) {
          LogCenter.exception(this, oe);
          throw new RuntimeException("Failed to move file " + fileFullPath
              + " to XAM Store, location = " + relativePath + ", container name = " + fileName);
        }

        try {
          DDS.getApplication()
          .getOperationManager()
          .execute(DDS.getApplication().getApplicationUser(),
              new DeleteStoreChildOperation(source));
        } catch (OperationException oe) {
          LogCenter.warning("Segment " + fileFullPath
              + " was not be deleted. It should be removed manually.");
        }

        element.setAttribute("ctrPath", relativePath);
        element.setAttribute("ctrName", fileName);
      }
    }
    return new Object[] {};
  }
}
