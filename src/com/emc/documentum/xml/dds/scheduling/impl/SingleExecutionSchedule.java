/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.scheduling.impl;

import com.emc.documentum.xml.dds.scheduling.Schedule;

/**
 * This class provides a simple {@link Schedule} which will be executed once, at the specified time.
 */
public final class SingleExecutionSchedule implements Schedule {

  // Instance Properties //

  private final long timestamp;

  // Instance Constructors //

  /**
   * Constructor which specifies the timestamp for execution.
   * @param timestamp the timestamp specifying the next execution time
   */
  public SingleExecutionSchedule(long timestamp) {
    this.timestamp = timestamp;
  }

  // Schedule Implementation //

  @Override
  public Long getNextExecutionTime() {
    return System.currentTimeMillis() > timestamp ? null : timestamp;
  }
}
