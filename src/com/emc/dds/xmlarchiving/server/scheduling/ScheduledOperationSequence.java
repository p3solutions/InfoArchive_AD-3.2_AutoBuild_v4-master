/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.scheduling;

import java.util.List;

import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.scheduling.Schedule;
import com.emc.documentum.xml.dds.scheduling.TimerTask;
import com.emc.documentum.xml.dds.scheduling.impl.ScheduledOperation;

public class ScheduledOperationSequence extends TimerTask {

  private final List<Operation<?>> operations;

  /**
   * Creates a new {@link ScheduledOperation}.
   * @param id the Id of the {@link TimerTask}
   * @param schedule the schedule for execution
   * @param operation the {@link Operation} to be executed
   */
  public ScheduledOperationSequence(String id, Schedule schedule, List<Operation<?>> operations) {
    super(id, schedule);
    this.operations = operations;
  }

  @Override
  public ScheduledOperationSequence copy() {
    return new ScheduledOperationSequence(getId(), getSchedule(), operations);
  }

  @Override
  public void execute() {
    try {
      for (Operation<?> operation : operations) {
        Object returnValue = application.execute(application.getApplicationUser(), operation);
        if (returnValue.equals(Boolean.FALSE)) {
          break;
        }
      }
    } catch (OperationFailedException ofe) {
      LogCenter.exception(this, ofe);
    }
  }
}
