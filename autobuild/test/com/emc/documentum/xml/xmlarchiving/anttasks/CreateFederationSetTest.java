/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.xmlarchiving.anttasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.emc.documentum.xml.xmlarchiving.anttasks.CreateFederationSet.Federation;
import com.emc.documentum.xml.xmlarchiving.anttasks.CreateFederationSet.FederationSet;
import com.xhive.federationset.interfaces.XhiveFederationSetFactory;
import com.xhive.federationset.interfaces.XhiveFederationSetIf;

/**
 * Unit test for the &lt;createfederationset&gt; task.
 * 
 * 
 */
public class CreateFederationSetTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Rule
  public ExpectedException exception = ExpectedException.none();

  private static final String FED = "fed";
  private static final String FED_SET = "fedSet";
  private static final String BOOTSTRAP = "bootstrap";
  private static final String FILE = "file";

  /**
   * @param name the base name to use for a new federation set file
   * @return the absolute path to use for a new federation set file
   * @throws IOException on error
   */
  private String newFederationSetFile(String name) throws IOException {
    String dir = tempFolder.newFolder("FederationSets").getAbsolutePath();
    return dir + "/" + name;
  }

  /**
   * @return a new task
   */
  private CreateFederationSet newTask() {
    CreateFederationSet task = new CreateFederationSet();
    task.setTaskName("createfederationset");
    task.setProject(new Project());
    return task;
  }

  /**
   * @param size the number of items to add to the set
   * @return the absolute path of the created federation set file
   * @throws IOException on error
   */
  private String createFederationSet(int size) throws IOException {
    return createFederationSet(size, FED, BOOTSTRAP, FED_SET, FILE);
  }

  /**
   * @param size the number of items to add to the set
   * @param fed the prefix of federation names
   * @param bootstrap the prefix of federation bootstraps
   * @param fedSet the prefix of federation set names
   * @param file the prefix of federation set files
   * @return the absolute path of the created federation set file
   * @throws IOException on error
   */
  private String createFederationSet(int size, String fed, String bootstrap, String fedSet,
      String file) throws IOException {
    String path = newFederationSetFile("LocalDev.fs");
    XhiveFederationSetFactory.createFederationSet(path);
    XhiveFederationSetIf set = XhiveFederationSetFactory.getFederationSet(path, null);
    Map<String, String> fedMap = set.getFederationMap();
    Map<String, String> fedSetMap = set.getFederationSetMap();
    for (int i = 1; i <= size; i++) {
      fedMap.put(fed + i, bootstrap + i);
      fedSetMap.put(fedSet + i, file + i);
    }
    return path;
  }

  /**
   * Test creating an empty federation set.
   */
  @Test
  public void createEmptyFederationSet() {
    try {
      CreateFederationSet task = newTask();
      String file = newFederationSetFile("LocalDev.fs");
      task.setFile(file);
      task.execute();

      XhiveFederationSetIf set = XhiveFederationSetFactory.getFederationSet(file, null);
      assertTrue(set.getFederationMap().isEmpty());
      assertTrue(set.getFederationSetMap().isEmpty());
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test creating a simple federation set and adding entries to it.
   */
  @Test
  public void createSimpleFederationSet() {
    try {
      CreateFederationSet task = newTask();
      String file = newFederationSetFile("LocalDev.fs");
      task.setFile(file);
      for (int i = 1; i <= 3; i++) {
        Federation fed = task.createFederation();
        fed.setName(FED + i);
        fed.setBootstrap(BOOTSTRAP + i);
        FederationSet fedSet = task.createFederationSet();
        fedSet.setName(FED_SET + i);
        fedSet.setFile(FILE + i);
      }
      task.execute();

      XhiveFederationSetIf set = XhiveFederationSetFactory.getFederationSet(file, null);
      Map<String, String> fedMap = set.getFederationMap();
      Map<String, String> fedSetMap = set.getFederationSetMap();
      for (int i = 1; i <= 3; i++) {
        assertEquals(BOOTSTRAP + i, fedMap.get(FED + i));
        assertEquals(FILE + i, fedSetMap.get(FED_SET + i));
      }
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test adding entries to an existing federation set.
   */
  @Test
  public void addToFederationSet() {
    try {
      int size = 3;

      String file = createFederationSet(size);

      CreateFederationSet task = newTask();
      task.setFile(file);
      for (int i = size + 1; i <= size + size; i++) {
        Federation fed = task.createFederation();
        fed.setName(FED + i);
        fed.setBootstrap(BOOTSTRAP + i);
        FederationSet fedSet = task.createFederationSet();
        fedSet.setName(FED_SET + i);
        fedSet.setFile(FILE + i);
      }
      task.execute();

      XhiveFederationSetIf set = XhiveFederationSetFactory.getFederationSet(file, null);
      Map<String, String> fedMap = set.getFederationMap();
      Map<String, String> fedSetMap = set.getFederationSetMap();
      for (int i = 1; i <= size + size; i++) {
        assertEquals(BOOTSTRAP + i, fedMap.get(FED + i));
        assertEquals(FILE + i, fedSetMap.get(FED_SET + i));
      }
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test overwriting an existing federation set.
   */
  @Test
  public void overwriteFederationSet() {
    try {
      int size = 3;

      String file = createFederationSet(size);

      CreateFederationSet task = newTask();
      task.setFile(file);
      for (int i = size + 1; i <= size + size; i++) {
        Federation fed = task.createFederation();
        fed.setName(FED + i);
        fed.setBootstrap(BOOTSTRAP + i);
        FederationSet fedSet = task.createFederationSet();
        fedSet.setName(FED_SET + i);
        fedSet.setFile(FILE + i);
      }
      task.setOverwrite(true);
      task.execute();

      XhiveFederationSetIf set = XhiveFederationSetFactory.getFederationSet(file, null);
      Map<String, String> fedMap = set.getFederationMap();
      Map<String, String> fedSetMap = set.getFederationSetMap();
      for (int i = 1; i <= size; i++) {
        assertEquals(null, fedMap.get(FED + i));
        assertEquals(null, fedSetMap.get(FED_SET + i));
      }
      for (int i = size + 1; i <= size + size; i++) {
        assertEquals(BOOTSTRAP + i, fedMap.get(FED + i));
        assertEquals(FILE + i, fedSetMap.get(FED_SET + i));
      }
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test removing entries from an existing federation set.
   */
  @Test
  public void removeFromFederationSet() {
    try {
      int size = 3;
      String file = createFederationSet(size);

      CreateFederationSet task = newTask();
      task.setFile(file);
      for (int i = 1; i <= size; i++) {
        if (i % 2 == 0) {
          Federation fed = task.createFederation();
          fed.setName(FED + i);
          fed.setRemove(true);
          FederationSet fedSet = task.createFederationSet();
          fedSet.setName(FED_SET + i);
          fedSet.setRemove(true);
        }
      }
      task.execute();

      XhiveFederationSetIf set = XhiveFederationSetFactory.getFederationSet(file, null);
      Map<String, String> fedMap = set.getFederationMap();
      Map<String, String> fedSetMap = set.getFederationSetMap();
      for (int i = 1; i <= size; i++) {
        if (i % 2 == 0) {
          assertEquals(null, fedMap.get(FED + i));
          assertEquals(null, fedSetMap.get(FED_SET + i));
        } else {
          assertEquals(BOOTSTRAP + i, fedMap.get(FED + i));
          assertEquals(FILE + i, fedSetMap.get(FED_SET + i));

        }
      }
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
