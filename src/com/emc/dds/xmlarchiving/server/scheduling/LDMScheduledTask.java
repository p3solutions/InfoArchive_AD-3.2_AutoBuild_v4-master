/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.scheduling;

import com.emc.documentum.xml.dds.scheduling.impl.SimplePeriodicalSchedule;

public class LDMScheduledTask {

  private long interval;

  private boolean activated;

  private String type;

  private String name;

  public LDMScheduledTask(String interval, boolean activated, String type, String name) {
    super();
    this.interval = getScheduleInterval(interval);
    this.activated = activated;
    this.type = type;
    this.name = name;
  }

  public long getInterval() {
    return interval;
  }

  public boolean isActivated() {
    return activated;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  private long getScheduleInterval(String intervalAsString) {
    if (intervalAsString.equals("minute")) {
      return SimplePeriodicalSchedule.MINUTEINTERVAL;
    }
    if (intervalAsString.equals("hour")) {
      return SimplePeriodicalSchedule.HOURINTERVAL;
    }
    if (intervalAsString.equals("day")) {
      return SimplePeriodicalSchedule.DAYINTERVAL;
    }
    if (intervalAsString.equals("week")) {
      return SimplePeriodicalSchedule.WEEKINTERVAL;
    }
    return -1;
  }

}
