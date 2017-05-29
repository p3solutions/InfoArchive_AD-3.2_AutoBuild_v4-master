/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.scheduling.impl;

import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.scheduling.Schedule;
import com.emc.documentum.xml.dds.scheduling.TimerTask;

/**
 * {@link TimerTask} which executes an {@link Operation} according to the specified {@link Schedule}.
 */
public final class ScheduledOperation extends TimerTask {

  private final Operation<?> operation;

  /**
   * Creates a new {@link ScheduledOperation}.
   * @param id the Id of the {@link TimerTask}
   * @param schedule the schedule for execution
   * @param operation the {@link Operation} to be executed
   */
  public ScheduledOperation(String id, Schedule schedule, Operation<?> operation) {
    super(id, schedule);
    this.operation = operation;
  }

  @Override
  public ScheduledOperation copy() {
    return new ScheduledOperation(getId(), getSchedule(), operation);
  }

  @Override
  public void execute() {

    try {
      application.execute(application.getApplicationUser(), operation);
    } catch (OperationFailedException ofe) {
      LogCenter.exception(this, ofe);
    }
  }
}
