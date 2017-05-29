/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;

import com.emc.dds.xmlarchiving.server.operation.HandleIncomingOperation;
import com.emc.dds.xmlarchiving.server.operation.HandleScheduledXMLArchivingOperation;
import com.emc.dds.xmlarchiving.server.operation.LoadScheduledTasksOperation;
import com.emc.dds.xmlarchiving.server.scheduling.LDMScheduledHandleIncoming;
import com.emc.dds.xmlarchiving.server.scheduling.LDMScheduledTask;
import com.emc.dds.xmlarchiving.server.scheduling.LDMScheduledUpdateXQueries;
import com.emc.dds.xmlarchiving.server.scheduling.ScheduledOperationSequence;
import com.emc.dds.xmlarchiving.util.DBLogger;
import com.emc.dds.xmlarchiving.util.FileLogger;
import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.application.exception.InitializationException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.scheduling.Schedule;
import com.emc.documentum.xml.dds.scheduling.SchedulingService;
import com.emc.documentum.xml.dds.scheduling.TimerTask;
import com.emc.documentum.xml.dds.scheduling.impl.SimplePeriodicalSchedule;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.servlet.ApplicationStarter;

public class LDMApplicationStarter extends ApplicationStarter {

  @Override
  protected void initializeApplication(final String configurationPath)
      throws InitializationException {
    LogCenter.log("Start LDM application starter");
    super.initializeApplication(configurationPath);

    // Check the property file to determine whether to log to DB or File
    String logLocation = "fileSystem"; // default to file system in case the property file is
// missing or corrupted
    try {
      final ResourceBundle rb = ResourceBundle.getBundle("logger");
      for (final Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
        final String key = keys.nextElement();
        if (key.equals("logToLocation")) {
          logLocation = rb.getString(key);
          break;
        }
      }
    } catch (final MissingResourceException e) // Property file does not exist
    {
      LogCenter
          .log("Logger property file missing. File system audit logging will be used instead.");
      logLocation = "fileSystem";
    }
    if (logLocation.equals("database")) {
      LogCenter.log("Starting up Audit logger, using DBLogger for logging");

      // Check if there is a file path in the property file
      boolean keyExists = false;
      final ResourceBundle rb = ResourceBundle.getBundle("logger");
      for (final Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
        final String key = keys.nextElement();
        if (key.equals("filepath")) {
          keyExists = true;
          break;
        }
      }

      if (keyExists) {
        // we're going to log back to xDB
        LogCenter.setDefaultLogger(new DBLogger());
      } else // Use the file system for logging because there is no path for the database
      {
        LogCenter
            .log("Logger property file corrupt. Ensure there is a \"filepath\" key with your desired database path. File system audit logging will be used instead.");
        LogCenter.log("Starting up Audit logger, using the file system for logging");
        // we're going to log to the file system
        LogCenter.setDefaultLogger(new FileLogger("logs", "FSC", ".log", LogCenter
            .getDefaultLogger()));
      }
    } else {
      LogCenter.log("Starting up Audit logger, using the file system for logging");
      // we're going to log to the file system
      LogCenter
          .setDefaultLogger(new FileLogger("logs", "FSC", ".log", LogCenter.getDefaultLogger()));
    }

    final Application application = DDS.getApplication();
    final ServiceManager serviceManager = application.getServiceManager();
    final SchedulingService scheduleService =
        (SchedulingService)serviceManager.getService(DDSServiceType.SCHEDULE);

    scheduleService.setApplication(application);
    final Store store = application.getMainStore();

    try {
      for (final LDMScheduledTask task : application.execute(application.getApplicationUser(),
          new LoadScheduledTasksOperation(store, application.getName()))) {
        final String scheduledTimerTaskID = getTimerTaskID(task.getName(), application.getName());
        final List<Operation<?>> operations = new ArrayList<Operation<?>>();
        if (task.getType().equals("handleIncoming")) {
          final LDMScheduledHandleIncoming incomingTask = (LDMScheduledHandleIncoming)task;
          operations.add(new HandleIncomingOperation(store, scheduledTimerTaskID, incomingTask
              .getSharedlocation(), incomingTask.getXmlextensions(), incomingTask.getMaxThreads()));
          handleScheduledTask(application, scheduleService, scheduledTimerTaskID, operations, task);
        } else if (task.getType().equals("executeUpdateXQueries")) {
          final LDMScheduledUpdateXQueries updateTask = (LDMScheduledUpdateXQueries)task;
          for (final LDMScheduledUpdateXQueries.XQuery xquery : updateTask.getXqueries()) {
            operations.add(new HandleScheduledXMLArchivingOperation(store, scheduledTimerTaskID,
                xquery.getXquery(), xquery.isReadonly()));
          }
          handleScheduledTask(application, scheduleService, scheduledTimerTaskID, operations, task);
        }
      }
    } catch (final OperationException e) {
      LogCenter.exception(this, e);
      throw new InitializationException("Failed to initialize timertask", e);
    }
  }

  private void handleScheduledTask(final Application application,
      final SchedulingService scheduleService, final String scheduledTaskTimerTaskID,
      final List<Operation<?>> operations, final LDMScheduledTask task)
          throws InitializationException {

    final boolean activate = task.isActivated();
    final long interval = task.getInterval();

    TimerTask timerTask = scheduleService.getScheduledTask(scheduledTaskTimerTaskID);
    if (timerTask == null) {
      // is the task activated?
      if (activate) {
        LogCenter.log("Create a scheduled task \"" + scheduledTaskTimerTaskID + "\" with interval "
            + interval);
        final Schedule schedule =
            new SimplePeriodicalSchedule(System.currentTimeMillis(), interval);
        timerTask = new ScheduledOperationSequence(scheduledTaskTimerTaskID, schedule, operations);
        scheduleService.submit(timerTask);
      }
    } else if (!activate) {
      LogCenter.log("Stop timer task " + scheduledTaskTimerTaskID);
      scheduleService.cancel(scheduledTaskTimerTaskID);
    }
  }

  @Override
  public void contextDestroyed(final ServletContextEvent event) {
    LogCenter.log("Context destroyed LDM application");
    final Application application = DDS.getApplication();
    final ServiceManager serviceManager = application.getServiceManager();
    final SchedulingService scheduleService =
        (SchedulingService)serviceManager.getService(DDSServiceType.SCHEDULE);

    for (final TimerTask timerTask : scheduleService.getScheduledTasks()) {
      LogCenter.log("Stop timer task \"" + timerTask.getId() + "\"");
      scheduleService.cancel(timerTask.getId());
    }
    super.contextDestroyed(event);
  }

  private String getTimerTaskID(final String name, final String applicationName) {
    return name + "-" + applicationName;
  }

}
