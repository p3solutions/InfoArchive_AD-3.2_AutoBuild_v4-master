/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.dds.service;

/**
 * The ThreadedService is an extension of a normal Service, which has its own thread. The thread
 * will be created and started when the Service is started, and will finish when the Service is
 * stopped.
 */
public interface ThreadedService extends Service, Runnable {

  /**
   * <p>
   * This method forces the thread to stop in a potentially unsafe way. It is provided as a
   * management tool, to deal with cases where for some reason the {@link Service} has entered a
   * state where it can no longer be controlled properly, and the need exists to bring the Service
   * down.
   * </p>
   * <p>
   * It should be used with extreme care, since the method uses the deprecated {@link Thread#stop()}
   * method.
   * </p>
   */
  void hardStop();
}
