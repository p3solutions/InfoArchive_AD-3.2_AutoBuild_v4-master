/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.scheduling.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import com.emc.documentum.xml.dds.scheduling.SchedulingService;
import com.emc.documentum.xml.dds.scheduling.TimerTask;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.impl.ServiceImpl;

/**
 * Basic implementation of the {@link SchedulingService}.
 */
public class SchedulingServiceImpl extends ServiceImpl implements SchedulingService {

  // Instance Properties //

  private Timer timer;
  private Map<String, TimerTask> taskMap;
  private final Object submissionMutex = new Object();

  // SchedulingService Implementation //

  @Override
  public void submit(TimerTask task) {

    synchronized (submissionMutex) {
      cancel(task.getId());
      Long nextExecution = task.getSchedule().getNextExecutionTime();
      if (nextExecution != null) {
        task.setSchedulingService(this);
        task.setApplication(getApplication());
        taskMap.put(task.getId(), task);
        timer.schedule(task, new Date(nextExecution));
      }
    }
  }

  @Override
  public TimerTask getScheduledTask(String id) {

    synchronized (submissionMutex) {
      return taskMap.get(id);
    }
  }

  @Override
  public List<TimerTask> getScheduledTasks() {

    synchronized (submissionMutex) {
      return new ArrayList<TimerTask>(taskMap.values());
    }
  }

  @Override
  public void cancel(String taskId) {

    synchronized (submissionMutex) {
      TimerTask oldTask = taskMap.get(taskId);
      if (oldTask != null) {
        oldTask.cancel();
        taskMap.remove(taskId);
      }
    }
  }

  // Service Implementation //

  @Override
  protected boolean checkDependencies() {
    return true;
  }

  @Override
  protected boolean executeInitialization() {

    taskMap = new HashMap<String, TimerTask>();
    timer = new Timer();
    return true;
  }

  @Override
  protected boolean executeStartup() {
    return true;
  }

  @Override
  protected boolean executeShutdown() {

    timer.cancel();
    return true;
  }

  @Override
  protected boolean executePause() {
    return true;
  }

  @Override
  protected boolean executeResume() {
    return true;
  }

  @Override
  public ServiceType getType() {
    return DDSServiceType.SCHEDULE;
  }

  @Override
  public boolean activateConfiguration() {
    return true;
  }
}
