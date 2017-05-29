/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.Date;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.ibm.icu.text.SimpleDateFormat;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.query.interfaces.XhiveXQueryQueryIf;

public class CheckHasRetiredOperationExecutable extends
PersistenceOperationExecutable<CheckHasRetiredOperation, Boolean> {

  protected static final String OPERATION_DOC_NAME = "check-retired.xml";
  private static final String DATE_PATTERN = "yyyy-MM-dd";

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

  @Override
  public Boolean run(Map<String, Session> sessionMap) throws DDSException {
    Session session = sessionMap.get(getOperation().getStoreAlias());
    XhiveSessionIf xhiveSession = (XhiveSessionIf)session.getSession();
    XhiveLibraryIf rootLib = xhiveSession.getDatabase().getRoot();
    String date = DATE_FORMAT.format(new Date());
    Document input =
        xhiveSession.getTemporaryDOMImplementation().createDocument(null, "data", null);
    Element dateElem = input.createElementNS(null, "date");
    input.getDocumentElement().appendChild(dateElem);
    Text text = input.createTextNode(date);
    dateElem.appendChild(text);

    boolean result = checkHasRetired(rootLib, input);
    LogCenter.log("Checking retired objects " + getOperation().getId() + " time: " + date + " : "
        + (result ? "objects found " : "no objects found"));
    return result ? Boolean.TRUE : Boolean.FALSE;
  }

  private boolean checkHasRetired(XhiveLibraryIf rootLib, Document input) {
    String applicationName = getApplication().getName();
    for (String type : OperationsUtil.getAllTypes(rootLib, applicationName)) {
      String xqueryStr =
          OperationsUtil.getXQuery(rootLib, applicationName, type, OPERATION_DOC_NAME);
      if (xqueryStr != null) {
        XhiveXQueryQueryIf xquery = rootLib.createXQuery(xqueryStr);
        xquery.setVariable("input", input.getDocumentElement());
        if (xquery.execute().hasNext()) {
          return true;
        }
      }
    }
    return false;
  }
}