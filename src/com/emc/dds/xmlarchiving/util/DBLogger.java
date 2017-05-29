/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.logging.Logger;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.xquery.XQueryExecutor;

/**
 * Audit logging into xDB
 */
public class DBLogger implements Logger {

  // Class Properties //

  /** property key for xDB path */
  private static final String KEY_FILEPATH = "filepath";

  /** name of XML document in xDB containing audit entries */
  private static final String AUDIT_ENTRY_FILENAME = "auditEntries.xml";

  /** element open tag */
  private static final String DATA_ELEMENT_OPEN_TAG = "<data>";

  /** element close tag */
  private static final String DATA_ELEMENT_CLOSE_TAG = "</data>";

  /** empty element */
  private static final String EMPTY_DATA = "<data/>";

  // Instance Properties //

  /** path in xDB to library containing audit entries - set based on property value */
  private String xDBPath;

  /** query template for populating with log details */
  private String queryTemplate;

  /** default logger instance */
  private static final Logger defaultLogger = LogCenter.getDefaultLogger();

  /** Constructor */
  public DBLogger() {

    initDBLogger();
  }

  /**
   * Initialize settings, including the audit log path from properties and the query template. Ensure the required paths exist in
   * the database.
   */
  private void initDBLogger() {

    // get location information for audit entries from properties file
    final ResourceBundle rb = ResourceBundle.getBundle("logger");
    if (rb.containsKey(KEY_FILEPATH)) {
      xDBPath = rb.getString(KEY_FILEPATH);
    }

    // ensure audit entry document exists
    validateOrCreateDoc();

    // construct XQuery template for updating audit entries
    queryTemplate =
        "let $auditTrail :=  <auditEntry><time>%s</time><app>%s</app><user>%s</user>\n"
            + "<searchConfiguration>%s</searchConfiguration>%s</auditEntry>\n"
            + "let $doc := doc('" + xDBPath + AUDIT_ENTRY_FILENAME + "')\n"
            + "return insert node $auditTrail as last into $doc/auditEntries";

  }

  // Logger Implementation //

  /**
   * {@inheritDoc}
   */
  @Override
  public void finalize() {

    // nothing to finalize
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {

    // no open closeables to close
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void log(final String msg) {

    // only log our audit messages. Otherwise, pass the message along to the default logger.
    if (msg.indexOf("IRM_CODE") > -1) {
      writeMessageToDB(msg);
    } else {
      defaultLogger.log(msg);
    }
  }

  /**
   * Store the audit entry to xDB
   *
   * @param msg
   *            audit entry message
   */
  private void writeMessageToDB(final String msg) {

    executeQuery(buildUpdateQuery(msg));
  }

  /**
   * Construct the query containing the log message to store
   *
   * @param msg
   *            log message
   * @return XQuery string
   */
  private String buildUpdateQuery(final String msg) {

    final String appLabel = "app : '";
    final int appLabelOffset = appLabel.length();
    final String appStart = msg.substring(appLabelOffset);
    final int appStartEndOffset = appStart.indexOf("'");
    final String appName = appStart.substring(0, appStartEndOffset);

    // Format date time in correct XML structure: yyyy-dd-mmThh:mm:ss
    final Date entryDate = new Date();
    final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
    final String strDate = sdfDate.format(entryDate);
    final String strTime = sdfTime.format(entryDate);
    final String logTime = strDate + "T" + strTime;

    final int dataElementCloseTagLength = DATA_ELEMENT_CLOSE_TAG.length();
    final int dataElementOpenTagOffset = msg.indexOf(DATA_ELEMENT_OPEN_TAG);
    String data = EMPTY_DATA;
    if (dataElementOpenTagOffset != -1) {
      final String dataElementStart = msg.substring(dataElementOpenTagOffset);
      final int dataElementCloseTagOffset = dataElementStart.indexOf(DATA_ELEMENT_CLOSE_TAG);
      data = dataElementStart.substring(0, dataElementCloseTagOffset + dataElementCloseTagLength);
    }

    final String userLabel = "user : ";
    final String commaDelimeter = ",";
    final String userStart = msg.substring(msg.indexOf(userLabel));
    final String user = userStart.substring(userLabel.length(), userStart.indexOf(commaDelimeter));
    final String searchConfigurationLabel = "searchConfiguration : '";
    final String searchConfigurationStart = msg.substring(msg.indexOf(searchConfigurationLabel));
    final String searchConfiguration =
        searchConfigurationStart.substring(searchConfigurationLabel.length(),
            searchConfigurationStart.indexOf("',"));

    final String query =
        String.format(queryTemplate, logTime, appName, user, searchConfiguration, data);
    return query;
  }

  /**
   * Ensure that the audit entries file (XML document) exists in the database
   *
   */
  private void validateOrCreateDoc() {

    final String validateQuery = "xhive:created-at('" + xDBPath + AUDIT_ENTRY_FILENAME + "')";

    final boolean exists = executeQuery(validateQuery);

    if (!exists) {
      // build the parameters we need to log to the database
      final String xquery =
          "let $doc := xhive:parse('<auditEntries></auditEntries>')\n"
              + "return xhive:insert-document('" + xDBPath + AUDIT_ENTRY_FILENAME + "',$doc)";

      // and execute it.
      executeQuery(xquery);
    }
  }

  /**
   * Execute an XQuery
   *
   * @param xquery
   *            query string
   * @return result of query execution (true on success; false on failure)
   */
  private static boolean executeQuery(final String xquery) {

    boolean success = true;
    final Store store = DDS.getApplication().getMainStore();
    Session session = null;
    final XQueryExecutor xqueryExec = store.getXQueryExecutor();

    try {
      session = store.getSession(store.getDefaultStoreUser(), false);
    } catch (final StoreSpecificException e) {
      LogCenter.log("failed to create store session, detailed err = " + e.getMessage());
      return false;
    }

    try {
      session.begin();
      xqueryExec.execute(session, xquery, null);
    } catch (final DDSException e) {
      LogCenter.error("failed to execute update query for audit logging, detailed err = "
          + e.getMessage());
      success = false;
    } finally {
      try {
        session.commit();
      } catch (final CommitFailedException e) {
        LogCenter.error("failed to commit store session, detailed err = " + e.getMessage());
        try {
          session.rollback();
        } catch (final RollbackFailedException e1) {
          LogCenter.error("failed to rollback store session, detailed err = " + e.getMessage());
        }
      }
    }

    return success;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void warning(final String msg) {

    defaultLogger.warning(msg);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void error(final String msg) {

    defaultLogger.error(msg);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void debug(final String msg) {

    defaultLogger.debug(msg);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void exception(final String msg, final Throwable throwable) {

    defaultLogger.exception(msg, throwable);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void log(final Object sender, final String msg) {

    // only log our audit messages. Otherwise, pass the message along to the default logger.
    if (msg.indexOf("IRM_CODE") > -1) {
      this.writeMessage(sender, msg, "L");
    } else {
      defaultLogger.log(sender, msg);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void warning(final Object sender, final String msg) {

    defaultLogger.warning(sender, msg);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void error(final Object sender, final String msg) {

    defaultLogger.error(sender, msg);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void debug(final Object sender, final String msg) {

    defaultLogger.debug(sender, msg);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void exception(final Object sender, final String message, final Throwable throwable) {

    defaultLogger.exception(sender, message, throwable);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void exception(final Object sender, final Throwable throwable) {

    defaultLogger.exception(sender, throwable);
  }

  /**
   * Build message and write
   *
   * @param sender
   *            object requesting message
   * @param msg
   *            message text
   * @param prefix
   *            ignored
   */
  private void writeMessage(final Object sender, final String msg, final String prefix) {

    final StringBuilder result = new StringBuilder(sender.getClass().getName());
    result.append(" : ").append(msg);
    this.writeMessage(result.toString(), prefix);
  }

  /**
   * Write message to DB
   *
   * @param msg
   *            message text
   * @param prefix
   *            ignored
   */
  @SuppressWarnings("unused")
  private void writeMessage(final String msg, final String prefix) {

    writeMessageToDB(msg);
  }
}
