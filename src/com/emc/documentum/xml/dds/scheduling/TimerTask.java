/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.scheduling;

import com.emc.documentum.xml.dds.application.Application;

/**
 * The TimerTask extends the Java TimerTask, by adding an Id for management purposes, and a
 * {@link Schedule} to work with the {@link SchedulingService}.
 */
public abstract class TimerTask extends java.util.TimerTask {

  // Instance Properties //

  private final String id;
  private final Schedule schedule;
  private SchedulingService schedulingService;

  /**
   * This field gives the subclasses access to the environment in which the {@link TimerTask} is
   * executed.
   */
  protected Application application;

  // Instance Constructors //

  /**
   * Basic Constructor which takes an Id and a {@link Schedule} as parameters.
   * @param id the id of the {@link TimerTask}
   * @param schedule the {@link Schedule} determining the execution schedule
   */
  public TimerTask(String id, Schedule schedule) {
    this.id = id;
    this.schedule = schedule;
  }

  // Instance Accessors //

  /**
   * Retrieves the Id of the {@link TimerTask}.
   * @return the Id of the {@link TimerTask}
   */
  public String getId() {
    return id;
  }

  /**
   * Retrieves the {@link Schedule} of the {@link TimerTask}.
   * @return the {@link Schedule} of the {@link TimerTask}
   */
  public Schedule getSchedule() {
    return schedule;
  }

  /**
   * Sets the {@link SchedulingService} which will execute the {@link TimerTask}. This method
   * should only used by the {@link SchedulingService} on submission, and allows the run() method to
   * reschedule the {@link TimerTask} after execution.
   * @param schedulingService the {@link SchedulingService} which will execute the {@link TimerTask}
   */
  public void setSchedulingService(SchedulingService schedulingService) {
    this.schedulingService = schedulingService;
  }

  /**
   * Sets the {@link Application} in whose context the {@link TimerTask} will be executed. This
   * method should only be used by the {@link SchedulingService} on submission.
   * @param application the {@link Application} where the {@link TimerTask} will be executed
   */
  public void setApplication(Application application) {
    this.application = application;
  }

  /**
   * Executes the {@link TimerTask}, and submits a new copy to the {@link SchedulingService} after
   * execution.
   */
  @Override
  public final void run() {
    execute();
    schedulingService.submit(copy());
  }

  /**
   * Creates a copy of the {@link TimerTask} for resubmission, since a {@link TimerTask} instance
   * can only be scheduled once.
   * @return a copy of the {@link TimerTask}
   */
  public abstract TimerTask copy();

  /**
   * Executes the actual processing for the {@link TimerTask}.
   */
  public abstract void execute();
}
