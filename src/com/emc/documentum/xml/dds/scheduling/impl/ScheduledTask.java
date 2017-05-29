/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.scheduling.impl;

import com.emc.documentum.xml.dds.scheduling.Schedule;
import com.emc.documentum.xml.dds.scheduling.TimerTask;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.Task;
import com.emc.documentum.xml.dds.service.TaskBasedService;

/**
 * {@link TimerTask} which submits the specified {@link Task} to the specified {@link Service}
 * according to the specified {@link Schedule}.
 */
public final class ScheduledTask extends TimerTask {

  private final Task task;
  private final String serviceId;

  /**
   * Creates a new {@link ScheduledTask}.
   * @param id the Id of the {@link TimerTask}
   * @param schedule the schedule for execution
   * @param serviceId the Id of the {@link Service} which should execute the {@link Task}
   * @param task the {@link Task} to be executed
   */
  public ScheduledTask(String id, Schedule schedule, String serviceId, Task task) {
    super(id, schedule);
    this.serviceId = serviceId;
    this.task = task;
  }

  @Override
  public ScheduledTask copy() {
    return new ScheduledTask(getId(), getSchedule(), serviceId, task);
  }

  @Override
  public void execute() {
    ((TaskBasedService)application.getService(serviceId)).addTask(task);
  }
}
