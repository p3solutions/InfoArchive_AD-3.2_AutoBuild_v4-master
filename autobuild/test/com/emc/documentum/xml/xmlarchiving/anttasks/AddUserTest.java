/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.xmlarchiving.anttasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.Project;
import org.junit.Test;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.configuration.baseline.security.CryptoSettings;
import com.emc.documentum.xml.dds.configuration.baseline.security.JCESettings;
import com.emc.documentum.xml.dds.configuration.baseline.security.SecureRandomSettings;
import com.emc.documentum.xml.dds.tool.SecurityTool;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.UserService;

/**
 * Unit test for the &lt;adduser&gt; task.
 * 
 * 
 */
public class AddUserTest {

  private Properties buildProperties;

  private static final String APP = "garage";

  public AddUserTest() {
    buildProperties = loadProperties();
  }

  /**
   * @return the $DDS_DIR/bin/build.properties file
   */
  private Properties loadProperties() {
    Properties buildProps = new Properties();
    try {
      String path = System.getenv("DDS_DIR") + "/bin/build.properties";
      FileInputStream in = new FileInputStream(path);
      buildProps.load(in);
      in.close();
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
    } catch (IOException e) {
      fail(e.getMessage());
    }
    return buildProps;
  }

  /**
   * @return the JCE settings configured from $DDS_DIR/bin/build.properties
   */
  private JCESettings getJCESettings() {
    JCESettings jceSettings = new JCESettings();
    CryptoSettings crypto = new CryptoSettings();
    crypto.setTransformation(buildProperties.getProperty("dds.crypto.transform"));
    crypto.setProvider(buildProperties.getProperty("dds.crypto.provider"));
    jceSettings.setCrypto(crypto);
    SecureRandomSettings secureRandom = new SecureRandomSettings();
    secureRandom.setAlgorithm(buildProperties.getProperty("dds.securerandom.algorithm"));
    secureRandom.setProvider(buildProperties.getProperty("dds.securerandom.provider"));
    jceSettings.setSecureRandom(secureRandom);
    return jceSettings;
  }

  /**
   * @param password the unencrypted password
   * @param app the application
   * @return the encrypted password
   */
  private String encrypt(String password, String app) {
    String classes = System.getenv("DDS_DIR") + "/applications/" + app + "/war/WEB-INF/classes";
    String privatePath = classes + "/DDSPrivateKey.dat";
    String publicPath = classes + "/DDSPublicKey.dat";
    JCESettings jceSettings = getJCESettings();
    return SecurityTool.doTool(SecurityTool.COMMAND_ENCRYPT, privatePath, publicPath, password,
        jceSettings);
  }

  /**
   * Test adding a user to an application.
   */
  @Test
  public void addUser() {
    AddUser task = new AddUser();
    task.setTaskName("adduser");
    task.setProject(new Project());

    String id = "user1";
    String password = "secret";

    task.setApp(APP);
    task.setId(id);
    task.setPassword(encrypt(password, APP));
    task.execute();

    Application ddsApp = task.getApplication(APP);
    UserService userService = task.getUserService(ddsApp);
    User user1 = userService.getUser(id);

    assertNotNull(user1);
    assertEquals(id, user1.getId());
    assertTrue(user1.checkPassword(password));
    assertFalse(user1.isAdministrator());
  }
}
