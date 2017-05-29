/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.State;
import com.emc.documentum.xml.dds.service.Task;
import com.emc.documentum.xml.dds.service.TaskBasedService;

/**
 * A type of {@link Service} to which {@link Task}s can be assigned, which will be executed
 * asynchronously, in FIFO order.
 */
public abstract class TaskBasedServiceImpl extends ThreadedServiceImpl implements TaskBasedService {

  // Instance Properties //

  // This is initialized at Object Creation to allow early addition of Tasks in the lifecycle,
  // before the Manager is initialized.
  // This way, subclasses do not have to initialize this explicitly.
  /** Queue of Tasks to be executed. */
  private List<Task> taskQueue = new ArrayList<Task>();

  /** Mutex protecting the queue of {@link Task}s. */
  private final Object taskQueueMutex = new Object();

  // Task Management Methods //

  /**
   * Adds a {@link Task} to the {@link Task} queue.
   * @param task the {@link Task}s to be added to the internal {@link Task} queue
   */
  @Override
  public void addTask(Task task) {
    synchronized (taskQueueMutex) {
      taskQueue.add(task);
    }
    synchronized (runLockMutex) {
      runLockMutex.notify();
    }
  }

  /**
   * Retrieves the next {@link Task} to be executed, and removes it from the Task queue.
   * @return the {@link Task} to be executed
   */
  @Override
  public Task getNextTask() {
    synchronized (taskQueueMutex) {
      return taskQueue.size() > 0 ? taskQueue.remove(0) : null;
    }
  }

  /**
   * Retrieves the number of {@link Task}s waiting to be executed.
   * @return an integer denoting the number of {@link Task}s waiting to be executed
   */
  @Override
  public int getTaskCount() {
    synchronized (taskQueueMutex) {
      return taskQueue.size();
    }
  }

  /**
   * Executes all the {@link Task} currently in the Task queue, and removes them. While this is
   * going on, no {@link Task} can be added to or removed from the {@link Task} queue. This method
   * is mainly provided for shutdown, to ensure that all the pending {@link Task}s have been
   * executed before the Manager shuts down.
   * @return a boolean indicating whether all the {@link Task}s were performed successfully
   */
  @Override
  public boolean processAllTasks() {
    boolean success = true;
    synchronized (taskQueueMutex) {
      Task task = getNextTask();
      while (task != null) {
        success &= processTask(task);
        task = getNextTask();
      }
    }
    return success;
  }

  /**
   * Processes the specified {@link Task}.
   * @param task the {@link Task} to be processed
   * @return a boolean indicating whether the {@link Task} was performed successfully
   */
  protected abstract boolean processTask(Task task);

  // Processing Methods //

  @Override
  protected boolean executeStartup() {
    // Process all Tasks that were added during the Initialization phases. Only when this is done
    // the main execution will be started.
    return processAllTasks() && super.executeStartup();
  }

  @Override
  protected boolean executeShutdown() {
    synchronized (runLockMutex) {
      runLockMutex.notify();
    }
    processAllTasks();
    // This resets the Task List. We do not reset the Task List to null for the same reasons it is
    // initialized at Manager Creation time.
    taskQueue = new ArrayList<Task>();
    return super.executeShutdown();
  }

  @Override
  protected boolean executePause() {
    synchronized (runLockMutex) {
      runLockMutex.notify();
      return true;
    }
  }

  @Override
  protected boolean executeResume() {
    synchronized (runLockMutex) {
      runLockMutex.notify();
      return true;
    }
  }

  @Override
  protected boolean executeRunLoop() {
    if (getTaskCount() > 0) {
      Task task = getNextTask();
      if (task != null) {
        try {
          return processTask(task);
        } catch (Exception e) {
          LogCenter.exception(this,
              "Caught unhandled Exception processing Task, the Task has not been executed :", e);
          return false;
        }
      }
    } else if (getState() == State.RUNNING) {
      synchronized (runLockMutex) {
        try {
          runLockMutex.wait();
        } catch (Exception e) {
          LogCenter.exception(this, "Interrupted while sleeping :", e);
          return false;
        }
        return true;
      }
    }
    return true;
  }
}
