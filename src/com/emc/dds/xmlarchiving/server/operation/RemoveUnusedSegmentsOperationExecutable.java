/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.io.File;
import java.util.Date;
import java.util.Map;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveFileIf;
import com.xhive.core.interfaces.XhiveSegmentIf;
import com.xhive.core.interfaces.XhiveSessionIf;

public class RemoveUnusedSegmentsOperationExecutable extends
PersistenceOperationExecutable<RemoveUnusedSegmentsOperation, Object> {

  @Override
  public Object run(Map<String, Session> sessionMap) throws DDSException {
    LogCenter.log("Start removal unused segments " + getOperation().getId() + " time: "
        + new Date().toString());
    Session session = sessionMap.get(getOperation().getStoreAlias());
    XhiveSessionIf xhiveSession = (XhiveSessionIf)session.getSession();

    // cleanup unused segments
    cleanupUnusedSegments(xhiveSession.getDatabase());

    return Boolean.TRUE;
  }

  private void cleanupUnusedSegments(XhiveDatabaseIf database) {
    for (XhiveSegmentIf segment : database.getSegments()) {
      if (segment.getState() == XhiveSegmentIf.SegmentState.DETACH_POINT) {
        deleteSegment(segment);
      }
    }
  }

  private void deleteSegment(XhiveSegmentIf segment) {
    LogCenter.log("Cleanup segment " + segment.getId());
    try {
      boolean deletionSucces = true;
      for (XhiveFileIf file : segment.getFiles()) {
        LogCenter.log("Cleanup file " + file.getFileName());
        File fileToDelete = new File(file.getFileName());
        if (fileToDelete.exists()) {
          if (!fileToDelete.delete()) {
            deletionSucces = false;
            LogCenter.log("Failed to cleanup file " + file.getFileName());
          } else {
            LogCenter.log("Cleanup file succeeded");
          }
        }
      }
      if (deletionSucces) {
        segment.delete();
        LogCenter.log("Cleanup segment succeeded");
      }
    } catch (Exception e) {
      e.printStackTrace();
      LogCenter.exception("Cleanup segment failed ", e);
    }
  }
}
