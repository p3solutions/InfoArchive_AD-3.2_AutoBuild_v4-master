/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.scheduling;

/**
 * A Schedule provides a way of determining when something should be executed.
 */
public interface Schedule {

  /**
   * Indicates at what time the next execution is scheduled. A Java timestamp is returned, unless
   * there is no next execution time, in which case <code>null</code> is returned.
   * @return a Java timestamp specifying the next execution time
   */
  Long getNextExecutionTime();
}
