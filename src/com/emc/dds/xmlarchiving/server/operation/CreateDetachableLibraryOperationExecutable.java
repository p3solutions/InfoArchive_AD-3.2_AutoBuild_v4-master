/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.HashSet;
import java.util.Map;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;

public class CreateDetachableLibraryOperationExecutable extends
PersistenceOperationExecutable<CreateDetachableLibraryOperation, Object> {

  @Override
  public Object run(Map<String, Session> sessionMap) throws DDSException {
    Session session = sessionMap.get(getOperation().getStoreAlias());
    XhiveSessionIf xhiveSession = (XhiveSessionIf)session.getSession();
    if (xhiveSession != null) {
      XhiveDatabaseIf database = xhiveSession.getDatabase();
      XhiveLibraryIf parentLibrary =
          (XhiveLibraryIf)database.getRoot().getByPath(getOperation().getParentlibraryPath());
      if (parentLibrary != null) {
        XhiveLibraryIf retentionLibrary =
            (XhiveLibraryIf)parentLibrary.get(getOperation().getLibraryName());
        if (retentionLibrary == null) {
          retentionLibrary =
              OperationsUtil.createRetentionLibrary(parentLibrary, getOperation().getLibraryName());
          if (getOperation().isCreateIndexes()) {
            // now copy the indexes of a previous sibling detachable library
            XhiveLibraryChildIf sibling = retentionLibrary.getPreviousSibling();
            while (sibling != null) {
              if (sibling instanceof XhiveLibraryIf && isDetachableLibrary((XhiveLibraryIf)sibling)) {
                OperationsUtil.addIndexes(retentionLibrary, (XhiveLibraryIf)sibling,
                    new HashSet<String>());
                break;
              }
              sibling = sibling.getPreviousSibling();
            }
          }
        }
        return retentionLibrary;
      } else {
        LogCenter.error(this, "Parent library not found: " + getOperation().getParentlibraryPath());
      }
    } else {
      LogCenter.error(this, "Unable to create a detachable library for store "
          + getOperation().getStoreAlias());
    }
    return null;
  }

  private boolean isDetachableLibrary(XhiveLibraryIf library) {
    return (library.getOptions() & XhiveLibraryIf.DETACHABLE_LIBRARY) == XhiveLibraryIf.DETACHABLE_LIBRARY;
  }
}
