/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.internal.DDSConstants;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDOMImplementationIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.query.interfaces.XhiveXQueryQueryIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class HandleXMLArchivingOperationExecutable extends
PersistenceOperationExecutable<HandleXMLArchivingOperation, Object> {

  @Override
  public Object run(Map<String, Session> sessionMap) throws DDSException {
    Session session = sessionMap.get(getOperation().getStoreAlias());
    XhiveSessionIf xhiveSession = (XhiveSessionIf)session.getSession();

    XhiveLibraryIf rootLib = xhiveSession.getDatabase().getRoot();

    // get hold properties
    XhiveDOMImplementationIf impl = xhiveSession.getTemporaryDOMImplementation();
    LSParser parser =
        impl.createLSParser(org.w3c.dom.ls.DOMImplementationLS.MODE_SYNCHRONOUS,
            DDSConstants.SCHEMA_TYPE_XSD);
    LSInput input = impl.createLSInput();
    input.setStringData(getOperation().getInput());
    XhiveDocumentIf records = (XhiveDocumentIf)parser.parse(input);
    handleOperation(xhiveSession, rootLib, records);
    return Boolean.TRUE;
  }

  private void
      handleOperation(XhiveSessionIf session, XhiveLibraryIf rootLib, XhiveDocumentIf input)
          throws DDSException {
    // get all types
    HashMap<String, String> expressionsByType = new HashMap<String, String>();
    Element xqueryInput = input.getDocumentElement();
    for (XhiveXQueryValueIf value : input
        .executeXQuery("distinct-values(/data/selection/result/@type)")) {
      String type = value.asString();
      String expression =
          OperationsUtil.getXQuery(rootLib, getApplication().getName(), type, getOperation()
              .getXQueryName());
      if (expression != null) {
        expressionsByType.put(type, expression);
      }
    }

    for (String type : expressionsByType.keySet()) {
      XhiveXQueryQueryIf xquery = OperationsUtil.createXQuery(session, expressionsByType.get(type));
      xquery.setVariable("input", xqueryInput);
      xquery.setVariable("currentuser", getOperation().getUser());
      xquery.execute();
    }
  }
}
