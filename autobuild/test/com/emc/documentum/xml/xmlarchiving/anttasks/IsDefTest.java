/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.documentum.xml.xmlarchiving.anttasks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.junit.Test;

/**
 * Unit test for the &lt;isdef&gt; condition.
 * 
 * 
 */
public class IsDefTest {

  /**
   * Test the &lt;isdef&gt; condition.
   */
  @Test
  public void isDef() {
    Map<String, String> defProps = new HashMap<String, String>();
    defProps.put("property1", "value1");
    defProps.put("property2", "value2 ");
    defProps.put("property3", " value3");
    defProps.put("property4", " value4 ");

    Map<String, String> undefProps = new HashMap<String, String>();
    undefProps.put("property5", "  ");
    undefProps.put("property6", "");
    undefProps.put("property7", "\t");
    undefProps.put("property8", "  \t");

    Project project = new Project();
    for (String property : defProps.keySet()) {
      IsDef cond = new IsDef();
      cond.setProject(project);
      cond.setProperty(property);
      assertFalse(cond.eval());
      project.setProperty(property, defProps.get(property));
      assertTrue(cond.eval());
    }
    for (String property : undefProps.keySet()) {
      IsDef cond = new IsDef();
      cond.setProject(project);
      cond.setProperty(property);
      assertFalse(cond.eval());
      project.setProperty(property, undefProps.get(property));
      assertFalse(cond.eval());
    }
  }
}
