/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.scheduling;

import java.util.List;

import com.emc.documentum.xml.dds.service.Service;

/**
 * This {@link Service} provides functionality for scheduling {@link TimerTask}s, which will be
 * executed according to their {@link Schedule}.
 */
public interface SchedulingService extends Service {

  /**
   * Schedules the {@link TimerTask}, and returns the assigned Id. If an existing {@link TimerTask}
   * is resubmitted, the corresponding {@link TimerTask} will be canceled before scheduling the
   * task. This corresponds to treating the submission as an update.
   * @param task the {@link TimerTask} to be scheduled
   */
  void submit(TimerTask task);

  /**
   * Retrieves the {@link TimerTask} with the specified Id.
   * @param id the id of the {@link TimerTask} to be retrieved
   * @return the {@link TimerTask} with the specified Id
   */
  TimerTask getScheduledTask(String id);

  /**
   * Retrieves a {@link List} containing all submitted {@link TimerTask}s.
   * @return a {@link List} containing all submitted {@link TimerTask}s
   */
  List<TimerTask> getScheduledTasks();

  /**
   * Cancels the {@link TimerTask} with the specified Id.
   * @param taskId the Id of the {@link TimerTask} to be cancelled
   */
  void cancel(String taskId);
}
