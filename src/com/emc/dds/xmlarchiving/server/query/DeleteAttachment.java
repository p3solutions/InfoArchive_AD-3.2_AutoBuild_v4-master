/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.query;

import java.util.Iterator;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.operation.library.persistence.DeleteStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.user.User;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class DeleteAttachment implements XhiveXQueryExtensionFunctionIf {

  @Override
  public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] args) {
    Application application = DDS.getApplication();
    Object obj = args[0];
    if (obj != null) {
      for (Iterator<? extends XhiveXQueryValueIf> results = args[0]; results.hasNext();) {
        String uri = ((XhiveXQueryValueIf)results.next()).asString();
        try {
          DDSURI ddsURI = DDSURI.parseURI(uri);
          User user = application.getApplicationUser();
          URITarget targetContainer =
              application.getDefaultURIResolver().resolveURI(ddsURI,
                  application.getApplicationUser());
          StoreChild storeChild = targetContainer.getStoreChild();
          if (application.execute(user, new ExistsStoreChildOperation(storeChild))) {
            application.execute(application.getApplicationUser(), new DeleteStoreChildOperation(
                targetContainer.getStoreChild()));
          }
        } catch (Exception e) {
          throw new RuntimeException("Error in DeleteAttachment extension function", e);
        }
      }
    }
    return new Object[0];
  }
}
