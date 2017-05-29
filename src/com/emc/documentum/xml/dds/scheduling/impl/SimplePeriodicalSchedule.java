/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.scheduling.impl;

import com.emc.documentum.xml.dds.scheduling.Schedule;

/**
 * This class provides a simple {@link Schedule} which will generate execution times based on a
 * reference timestamp and an interval in milliseconds. The reference timestamp is the timestamp
 * when the task is first executed, and can lie in the future. The next execution time is calculated
 * as <code>referenceTimestamp + (N * interval)</code> with N such that the execution time is in
 * the future, and there is no smaller integer M such that
 * <code>referenceTimestamp + (M * interval)</code> lies in the future. If no reference timestamp
 * is specified, the time of the first {@link #getNextExecutionTime()} call to the Schedule will be
 * used.
 */
public final class SimplePeriodicalSchedule implements Schedule {

  // Class Properties //

  /** One minute interval. */
  public static final long MINUTEINTERVAL = 60L * 1000L;
  /** One hour interval. */
  public static final long HOURINTERVAL = 60L * 60L * 1000L;
  /** One day interval. */
  public static final long DAYINTERVAL = 24L * 60L * 60L * 1000L;
  /** One week interval. */
  public static final long WEEKINTERVAL = 7L * 24L * 60L * 60L * 1000L;

  // Instance Properties //

  private Long referenceTimestamp;
  private final long interval;

  // Instance Constructors //

  /**
   * Constructor which specifies the reference timestamp as well as the interval.
   * @param referenceTimestamp the timestamp used for calculating the next execution time
   * @param interval the periodicity of the schedule, in milliseconds
   */
  public SimplePeriodicalSchedule(Long referenceTimestamp, long interval) {

    this.referenceTimestamp = referenceTimestamp;
    this.interval = interval;
  }

  // Schedule Implementation //

  @Override
  public Long getNextExecutionTime() {

    if (referenceTimestamp == null) {
      referenceTimestamp = System.currentTimeMillis();
    }

    long currentTime = System.currentTimeMillis();
    if (referenceTimestamp > currentTime) {
      return referenceTimestamp;
    }

    long intervalsPassed = (currentTime - referenceTimestamp) / interval;
    return referenceTimestamp + interval * (intervalsPassed + 1);
  }
}
