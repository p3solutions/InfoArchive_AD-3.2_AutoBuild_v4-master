/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.service.impl;

import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.service.Action;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.State;
import com.emc.documentum.xml.dds.service.ThreadedService;

/**
 * Extension of {@link ServiceImpl} which implements a {@link Service} with its own thread. The
 * thread will be started when the START {@link Action} is performed, and stopped when the STOP
 * {@link Action} is performed.
 * <p>
 * The processing performed by the Thread is defined in the runloop. The {@link #executeRunLoop()}
 * performs one run cycle of the runloop, and will be called everytime the previous cycle has
 * finished. When paused or stopped, the current {@link #executeRunLoop()} will be allowed to finish
 * before the {@link Service} enters the {@link State#PAUSED} or {@link State#STOPPED} state.
 * </p>
 */
public abstract class ThreadedServiceImpl extends ServiceImpl implements ThreadedService {

  // Instance Properties //

  /** Thread for the RunLoop. */
  private Thread thread;

  /** Mutex to enable wait() in RunLoops that can be paused and stopped. */
  protected Object runLockMutex = new Object();

  // Instance Constructor //

  /**
   * Basic Constructor.
   */
  protected ThreadedServiceImpl() {
  }

  // Action Processing //

  @Override
  protected boolean executeInitialization() {
    thread = new Thread(this);
    return true;
  }

  @Override
  protected boolean executeStartup() {
    thread.start();
    return true;
  }

  @Override
  protected boolean executeShutdown() {
    // Ensures that the Runloop is not processing anymore.
    // If the Runloop has not truly exited yet, the next State change will stop it anyway.
    synchronized (runLockMutex) {
      thread = null;
    }
    return true;
  }

  @Override
  protected boolean executePause() {
    // Ensures that the PAUSED state is not reached before current Runloop processing has finished.
    synchronized (runLockMutex) {
      return true;
    }
  }

  @Override
  protected boolean executeResume() {
    return true;
  }

  /**
   * Executes one cycle of processing.
   * @return a boolean indicating whether processing was successful
   */
  abstract boolean executeRunLoop();

  /**
   * Stops the {@link Service} thread. This should be initiated only by the Administrator, and only
   * when he knows what he is doing :) This should be overridden by Managers where there are known
   * possible issues.
   */
  @Override
  @SuppressWarnings("deprecation")
  public void hardStop() {
    thread.stop();
  }

  /**
   * Starts iterating the runloop of the Service, until the Service is stopped.
   */
  @Override
  public final void run() {
    if (!waitOnStateChange()) {
      return;
    }

    State state = getState();
    // STOPPING is the only State which should cause the loop to exit.
    while (state != State.STOPPING) {
      if (state == State.RUNNING) {
        synchronized (runLockMutex) {
          try {
            executeRunLoop();
          } catch (Exception e) {
            LogCenter.exception(this, "Uncaught Exception in RunLoop :", e);
          }
        }
      } else {
        waitOnStateChange();
      }
    }
  }

  /**
   * This will wait until a State change occurs.
   * @return a boolean indicating whether the wait was exited without interruptions
   */
  private boolean waitOnStateChange() {
    synchronized (getStateMutex()) {
      try {
        // Wait until the State changes - the next State should by design be RUNNING.
        getStateMutex().wait();
        return true;
      } catch (InterruptedException ie) {
        LogCenter.exception(this, "Interrupted while waiting for kick-off :", ie);
        return false;
      }
    }
  }
}
