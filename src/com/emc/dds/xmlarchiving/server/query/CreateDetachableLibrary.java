/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.query;

import java.util.HashSet;
import java.util.Iterator;

import com.emc.dds.xmlarchiving.server.operation.OperationsUtil;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class CreateDetachableLibrary implements XhiveXQueryExtensionFunctionIf {

  @Override
  public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] args) {
    Object obj = args[0];
    if (obj != null) {
      XhiveXQueryValueIf value = args[0].next();
      String parentLibraryPath = value.asString();
      if (parentLibraryPath != null && !"".equals(parentLibraryPath)) {
        obj = args[1];
        if (obj != null) {
          value = args[1].next();
          String libraryName = value.asString();
          obj = args[2];
          boolean createIndexes = obj == null ? false : args[2].next().asBoolean();
          if (libraryName != null) {
            obj = args[3];
            value = args[3].next();
            XhiveDocumentIf doc = (XhiveDocumentIf)value.asNode();
            if (doc != null) {
              String segmentId = null;
              if (args.length >= 5) {
                obj = args[4];
                value = args[4].next();
                segmentId = value.asString();
              }
              try {
                XhiveLibraryIf parentLibrary =
                    (XhiveLibraryIf)doc.getOwnerLibrary().getByPath(parentLibraryPath);
                if (parentLibrary != null) {
                  XhiveLibraryIf retentionLibrary = (XhiveLibraryIf)parentLibrary.get(libraryName);
                  if (retentionLibrary == null) {
                    if (segmentId == null) {
                      retentionLibrary =
                          OperationsUtil.createRetentionLibrary(parentLibrary, libraryName);
                    } else {
                      retentionLibrary =
                          OperationsUtil.createRetentionLibrary(parentLibrary, libraryName,
                              segmentId);
                    }
                    if (createIndexes) {
                      // now copy the indexes of a previous sibling detachable library
                      XhiveLibraryChildIf sibling = retentionLibrary.getPreviousSibling();
                      while (sibling != null) {
                        if (sibling instanceof XhiveLibraryIf
                            && isDetachableLibrary((XhiveLibraryIf)sibling)) {
                          OperationsUtil.addIndexes(retentionLibrary, (XhiveLibraryIf)sibling,
                              new HashSet<String>());
                          break;
                        }
                        sibling = sibling.getPreviousSibling();
                      }
                    }
                  }
                }
              } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error in CreateDetachableLibrary extension function", e);
              }
            }
          }
        }
      }
    }
    return null;
  }

  private boolean isDetachableLibrary(XhiveLibraryIf library) {
    return (library.getOptions() & XhiveLibraryIf.DETACHABLE_LIBRARY) == XhiveLibraryIf.DETACHABLE_LIBRARY;
  }

}
