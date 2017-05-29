/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.service;

/**
 * A type of {@link Service} to which {@link Task}s can be assigned, which will be executed
 * asynchronously, in FIFO order. The main API is the {@link TaskBasedService#processAllTasks()}
 * method, which should implement the behaviour for the accepted {@link Task}s.
 * <p>
 * {@link Task}s will only be executed while the {@link Service} is in the {@link State#RUNNING}
 * {@link State}.
 * </p>
 * <p>
 * When stopped, all remaining {@link Task}s will be executed before the {@link Service} enters the
 * {@link State#STOPPED} {@link State}.
 * </p>
 */
public interface TaskBasedService extends ThreadedService {

  /**
   * Adds the task to the {@link Task} queue of the {@link Service}.
   * @param task the {@link Task} to be added to the queue
   */
  void addTask(Task task);

  /**
   * Returns the next {@link Task} which will be executed from the {@link Task} queue of the
   * {@link Service}.
   * @return the the next task which will be executed
   */
  Task getNextTask();

  /**
   * Returns the number of {@link Task}s currently in the {@link Task} queue.
   * @return the number of {@link Task}s currently in the {@link Task} queue
   */
  int getTaskCount();

  /**
   * Executes all the {@link Task}s currently in the {@link Task} queue in one go.
   * @return a boolean indicating whether all {@link Task}s were performed successfully
   */
  boolean processAllTasks();
}
