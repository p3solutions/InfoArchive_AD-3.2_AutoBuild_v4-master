/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.service;

/**
 * This interface models a task which can be executed by a {@link TaskBasedService}.
 */
public interface Task {

  /**
   * Returns the type of the Task.
   * @return the type of the Task
   */
  TaskType getType();

  /**
   * Returns the id of the Task.
   * @return the id of the Task
   */
  String getId();
}
