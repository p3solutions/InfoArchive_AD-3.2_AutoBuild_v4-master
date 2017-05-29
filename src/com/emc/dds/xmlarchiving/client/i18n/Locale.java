/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.i18n;

import com.emc.documentum.xml.dds.gwt.client.i18n.DDSErrors;
import com.google.gwt.core.client.GWT;

/**
 * Convenience class to get locale specific messages.
 */
public final class Locale {

  private static Labels labels;
  private static Messages messages;
  private static DDSErrors errors;

  /**
   * Private constructor, should not be used.
   */
  private Locale() {
    //
  }

  /**
   * Gets the labels for the current locale.
   * @return the labels for the current locale
   */
  public static Labels getLabels() {
    if (labels == null) {
      labels = (Labels)GWT.create(Labels.class);
    }
    return labels;
  }

  /**
   * Gets the messages for the current locale.
   * @return the messages for the current locale
   */
  public static Messages getMessages() {
    if (messages == null) {
      messages = (Messages)GWT.create(Messages.class);
    }
    return messages;
  }

  /**
   * Gets the error messages for the current locale.
   * @return the error messages for the current locale
   */
  public static DDSErrors getErrors() {
    if (errors == null) {
      errors = (DDSErrors)GWT.create(DDSErrors.class);
    }
    return errors;
  }
}
