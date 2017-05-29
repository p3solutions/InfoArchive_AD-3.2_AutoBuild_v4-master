/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.emc.documentum.xml.dds.logging.Logger;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.persistence.Session;

public class FileLogger implements Logger {

  // Class Properties //

  private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
  private final SimpleDateFormat filenameDateFormatter = new SimpleDateFormat("yyyyMMdd_hhmmss");

  private static final Object FNDFMUTEX = new Object();
  private static final Object DFMUTEX = new Object();

  // Instance Properties //

  private PrintWriter writer;
  private boolean writerInitialized;
  private Logger defaultLogger;
  private String logFileDateTime;
  private String logPrefix;
  private String logSuffix;
  private Session session;

  // Instance Constructors //

  /**
   * Constructor which allows you to specify elements of the file name of the log file. The file
   * name will be &lt;prefix&gt;_&lt;timestamp&gt;&ltsuffix&gt;. For example, if the prefix is "app"
   * and the suffix ".log", the file name will be something like app_20080908_164523.log.
   * <p>
   * The logPath should be specified as an absolute path appropriate to the file system. Some
   * examples :
   * </p>
   * <p>
   * <ul>
   * <li>logPath = "/tmp/" for the /tmp/ dir on Unix</li>
   * <li>logPath = "C:\\tmp\\" for the C:\tmp\ dir on Windows</li>
   * <li>logPath = "\\\\foo\\myDir" for the \\foo\myDir\ directory on the foo network share on
   * Windows</li>
   * </ul>
   * </p>
   * <p>
   * If the directory doesn't exist, it will be created automatically.
   * </p>
   * <p>
   * This is a special purpose logger used to log audit messages. Only log messages with the string
   * 'IRM_CODE' will be sent to the audit file. Other log messages and other types of message
   * (warning, error, debug, exception) will be sent along to the default logger that is passed into
   * the constructor.
   * </p>
   * <p>
   * This logger creates a new log file the first time a log message is written and the current date
   * != the date when the current log file was created. Note that this class does not delete old log
   * files and does not wrap log the file based on a maximum size.
   * </p>
   * @param logPath the directory where the log file will be created
   * @param logPrefix the prefix of the file name
   * @param logSuffix the suffix of the file name
   * @param defaultLogger logger used to log warning, error, debug or exception messages
   */
  public FileLogger(String logPath, String logPrefix, String logSuffix, Logger defaultLogger) {

    synchronized (FileLogger.FNDFMUTEX) {

      // save the default logger. We'll use this logger for all messages except log message.
      this.defaultLogger = defaultLogger;

      StringBuilder path = new StringBuilder(logPath == null ? "." : logPath);

      try {

        if (!path.toString().endsWith(File.separator)) {
          path.append(File.separator);
        }

        path.append(logPrefix);
        path.append("_");
        this.logPrefix = path.toString();
        this.logSuffix = logSuffix;
        logFileDateTime = filenameDateFormatter.format(new Date());
        path.append(logFileDateTime);
        path.append(logSuffix);

        File file = new File(path.toString());
        file.getParentFile().mkdirs();

        writer =
            new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true),
                StandardCharsets.UTF_8));
        writerInitialized = true;

      } catch (Exception e) {
        System.out.println("Failed to open logfile " + path.toString());
        e.printStackTrace();
      }
    }
  }

  @Override
  public void finalize() {
    if (session != null) {
      if (session.isOpen()) {
        try {
          session.commit();
        } catch (CommitFailedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Constructor which takes a URI.
   * @param fileUri the URI of the file in which the logger will write the log statements
   */
  public FileLogger(URI fileUri) {
    try {
      writer =
          new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(fileUri)),
              StandardCharsets.UTF_8));
      writerInitialized = true;
    } catch (Exception e) {
      System.out.println("Failed to open logfile " + fileUri);
      e.printStackTrace();
    }
  }

  // Logger Implementation //

  @Override
  public void close() {
    if (writerInitialized) {
      try {
        writer.close();
        writerInitialized = false;
      } catch (Exception e) {
        System.out.println("Failed to close logfile : " + e);
        e.printStackTrace();
      }
    }
  }

  @Override
  public void log(String msg) {
    // only log our audit messages. Otherwise, pass the message along to the default logger.
    if (msg.indexOf("IRM_CODE") > -1) {
      // I(Eric) deleted a check to see if we should use the file system logger or the xDB logger
      this.writeMessage(msg, "L");
    } else {
      defaultLogger.log(msg);
    }
  }

  @Override
  public void warning(String msg) {
    defaultLogger.warning(msg);
  }

  @Override
  public void error(String msg) {
    defaultLogger.error(msg);
  }

  @Override
  public void debug(String msg) {
    defaultLogger.debug(msg);
  }

  @Override
  public void exception(String msg, Throwable throwable) {
    defaultLogger.exception(msg, throwable);
  }

  @Override
  public void log(Object sender, String msg) {
    // only log our audit messages. Otherwise, pass the message along to the default logger.
    if (msg.indexOf("IRM_CODE") > -1) {
      this.writeMessage(sender, msg, "L");
    } else {
      defaultLogger.log(sender, msg);
    }
  }

  @Override
  public void warning(Object sender, String msg) {
    defaultLogger.warning(sender, msg);
  }

  @Override
  public void error(Object sender, String msg) {
    defaultLogger.error(sender, msg);
  }

  @Override
  public void debug(Object sender, String msg) {
    defaultLogger.debug(sender, msg);
  }

  @Override
  public void exception(Object sender, String message, Throwable throwable) {
    defaultLogger.exception(sender, message, throwable);
  }

  @Override
  public void exception(Object sender, Throwable throwable) {
    defaultLogger.exception(sender, throwable);
  }

  private void writeMessage(Object sender, String msg, String prefix) {
    StringBuilder result = new StringBuilder(sender.getClass().getName());
    result.append(" : ").append(msg);
    this.writeMessage(result.toString(), prefix);
  }

  private void writeMessage(String msg, String prefix) {
    StringBuilder result;
    synchronized (FileLogger.DFMUTEX) {
      // on every write, check to see if we're in a new day and if so, create a new log
      checkLogFileCurrency();
      result = new StringBuilder("#");
      result.append(dateFormatter.format(new Date()));
    }
    result.append(" ").append(prefix).append(" ");
    result.append(msg).append("#").append("\n");

    try {
      if (writerInitialized) {
        writer.write(result.toString());
        writer.flush();
      } else {
        System.out.println(result.toString());
      }
    } catch (Exception e) {
      System.out.println(result.toString());
    }
  }

  private void checkLogFileCurrency() {
    // make a file name and see if it's the same day as the current log file name. If it is, there's
    // nothing to do. If it's not, we need to flush and close the current log file and make a new
// one with the
    // current date.
    String newLogFileName = filenameDateFormatter.format(new Date());
    String newLogDate = newLogFileName.substring(0, newLogFileName.indexOf("_"));
    String curLogDate = logFileDateTime.substring(0, logFileDateTime.indexOf("_"));
    if (!newLogDate.equals(curLogDate)) {
      writer.flush();
      writer.close();
      writerInitialized = false;
      String path = logPrefix + newLogFileName + logSuffix;
      File file = new File(path.toString());
      file.getParentFile().mkdirs();

      try {
        writer =
            new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true),
                StandardCharsets.UTF_8));
        writerInitialized = true;
      } catch (IOException e) {
        System.out.println("Failed to open logfile " + path.toString());
        e.printStackTrace();
      }
    }

  }
}
