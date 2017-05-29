/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.Map;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.uri.resolver.DDSURIResolver;

public class CreateMissingLocationOperationExecutable extends
PersistenceOperationExecutable<CreateMissingLocationOperation, Object> {

  @Override
  public Object run(Map<String, Session> sessionMap) throws DDSException {
    Session session = sessionMap.get(getOperation().getStoreAlias());
    int indexSlash = getOperation().getUri().lastIndexOf('/');
    if (indexSlash != -1 && indexSlash != 0) {
      String uriLocation = getOperation().getUri().substring(0, indexSlash + 1);
      DDSURI ddsLocationURI = DDSURI.parseURI(uriLocation);
      DDSURIResolver resolver = new DDSURIResolver(getApplication());
      URITarget targetLocation =
          resolver.resolveURI(ddsLocationURI, getApplication().getApplicationUser());
      if (!targetLocation.getStoreChild().exists(session)) {
        String fullPath = getLocationStoragePath(ddsLocationURI);
        Store store = targetLocation.getStore();
        Location location = store.getLocation(fullPath);
        location.create(session, null, true);
      }
    }
    return null;
  }

  private String getLocationStoragePath(DDSURI ddsURI) {
    StringBuffer sb = new StringBuffer();
    boolean isDataset =
        ddsURI.getAttribute(DDSURIResolver.ATTRIBUTE_DOMAIN).equals(DDSURIResolver.DOMAIN_DATA);
    if (isDataset) {
      sb.append("/DATA/");
      sb.append(ddsURI.getAttribute(DDSURIResolver.ATTRIBUTE_DATASET));
      sb.append('/');
      sb.append("Collection");
      sb.append('/');
      String locale = ddsURI.getAttribute(DDSURIResolver.ATTRIBUTE_LOCALE);
      if (locale != null && !"".equals(locale)) {
        sb.append(locale);
      }
    } else {
      sb.append("/APPLICATIONS/");
      sb.append(getApplication().getName());
      sb.append("/resources");
    }
    sb.append(ddsURI.getDomainSpecificPart());
    return sb.toString();
  }
}
