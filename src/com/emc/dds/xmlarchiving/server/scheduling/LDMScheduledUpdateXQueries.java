/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.scheduling;

import java.util.ArrayList;
import java.util.List;

public class LDMScheduledUpdateXQueries extends LDMScheduledTask {

  private List<XQuery> xqueries = new ArrayList<XQuery>();

  public LDMScheduledUpdateXQueries(String interval, boolean activated, String type, String name) {
    super(interval, activated, type, name);
  }

  public List<XQuery> getXqueries() {
    return xqueries;
  }

  public void addXQuery(final String xquery, final boolean readOnly) {
    xqueries.add(new XQuery(xquery, readOnly));
  }

  public class XQuery {

    private String xquery;
    private boolean readonly;

    public XQuery(String xquery, boolean readonly) {
      this.xquery = xquery;
      this.readonly = readonly;
    }

    public String getXquery() {
      return xquery;
    }

    public boolean isReadonly() {
      return readonly;
    }
  }
}
