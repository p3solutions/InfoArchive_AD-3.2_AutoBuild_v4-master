/*******************************************************************************
 * Copyright (c) 2011 EMC Corporation All Rights Reserved
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import org.w3c.dom.NamedNodeMap;

import com.emc.documentum.xml.gwt.client.Dialog;
import com.emc.documentum.xml.gwt.client.FailureHandler;
import com.emc.documentum.xml.gwt.client.i18n.Locale;
import com.emc.documentum.xml.gwt.client.ui.DialogBox;
import com.emc.documentum.xml.xforms.gwt.client.control.XFormsControl;
import com.emc.documentum.xml.xforms.gwt.client.control.core.XFormsCoreControl;
import com.emc.documentum.xml.xforms.gwt.client.ext.xhtml.ui.XHTMLGWTUIHandler;
import com.emc.documentum.xml.xforms.gwt.client.ui.XFormsCoreWidget;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;


public class LDMUIHandler extends XHTMLGWTUIHandler {

  public LDMUIHandler() {
    super();
  }

  @Override
  public XFormsCoreWidget create(XFormsCoreControl control) {
    if (control.getType() == XFormsControl.TYPE_INPUT) {
      String suggestionsAttr = control.getElement().getAttribute("class"); 
      
     
      String prefix = "suggestions-";
      if (suggestionsAttr != null && suggestionsAttr.contains(prefix)) {
        int indexPathIndex = suggestionsAttr.lastIndexOf("---");
        /*
         * BRKC 07-19-2014
         * 
         * Here’s why suggestionsindex wasn’t working

			1)	The indexPath attribute in template-content is ignored completely.  Instead, there is an undocumented convention that expects the index path and index name to be combined in the suggestionsindex parameter using three dashes ‘---‘ as the delimiter, e.g.,
			<index-name>---<index-path>.  For example:
			  <suggestionsindex>IX_PV_Nm---/DATA/demo/Collection/DISNEY/INSIGHT/CNTRY/</suggestionsindex>
			2)	Apparently when the form is created, the code prepends the string ‘suggestions-‘ and appends a single dash ‘-‘ to the specified value, producing something like this:
			suggestions-IX_PV_Nm---/DATA/demo/Collection/DISNEY/INSIGHT/CNTRY/-
			
			Rather than try to change this silly convention I just modified LDMUIHandler.create() to strip the ‘suggestions-‘ and the trailing ‘–‘ from the string.
			
			So, if you follow the convention described above, you magically get typeaheads.

         * 
         */
        
        String indexName = suggestionsAttr.substring(prefix.length(), indexPathIndex);
        //String indexName = suggestionsAttr;
        String indexPath = suggestionsAttr.substring(indexPathIndex + 3);
        if(indexPath.substring(indexPath.length() - 1).equals("-"))
        {
        	indexPath = indexPath.substring(1,indexPath.length() - 1);
        }
        
        XFormsCoreWidget result = new LDMInputSuggestWidget(control, indexName, indexPath);
        return result;
      }
    /**
    // NOTE: The following code is superseded by changes to SearchPane.java
    // Flatirons: customize select1 widget to pre-populate choices from xDB
    } else if (control.getType() == XFormsControl.TYPE_SELECT1) {
      String xquery = control.getElement().getAttribute("class");
      //System.out.println("class xquery is " + xquery);
      if (xquery != null) {
        XFormsCoreWidget result = new LDMChoicesWidget(control, xquery);
        return result;
      }
    **/
    }    
    return super.create(control);
  }
  
  // Flatirons: mod better error reporting, overriding parent behavior
  @Override
  public void onException(final Throwable t) {
    final DialogBox confirm = Dialog.confirm("Oops! An error occurred", "", "Full Details");
    confirm.addCloseHandler(new CloseHandler<PopupPanel>() {

      @Override
      public void onClose(CloseEvent<PopupPanel> event) {
        if(!confirm.isCanceled()) {
          final StringBuilder builder = new StringBuilder();
          String headMessage = t.getMessage();
          if (headMessage == null) {
            headMessage = Locale.getErrors().unexpectedException(t.getClass().getName());
          }
          builder.append(headMessage);
          for(StackTraceElement s : t.getStackTrace()) {
            builder.append('\n');
            builder.append(s.toString());
          }
          Dialog.alert(builder.toString());
        }
      }
    });
  }

  /**
     * Shows a dialog with the optional friendly message and a button to display a detailed trace.
     * Does nothing if the Throwable is null.
     * 
     * @param t
     * @param optionalFriendlyMsg
     */
    public static void displayFriendlyException(final Throwable t, String optionalFriendlyMsg) {
        if(t == null || "Session timed out".equals(t.getMessage()))
            return;
        
        
        String msg = (optionalFriendlyMsg == null || optionalFriendlyMsg.length() == 0) ? 
                "Oops! An error occurred" : optionalFriendlyMsg;
        final DialogBox confirm = Dialog.confirm(msg, "", "Full Details");
        confirm.addCloseHandler(new CloseHandler<PopupPanel>() {
            
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                if(!confirm.isCanceled()) {
                    Throwable rootCause = t;
                    Throwable next = rootCause.getCause();
                    while (next != null && next != rootCause) {
                        rootCause = next;
                        next = rootCause.getCause();
                    }
                    
                    final StringBuilder builder = new StringBuilder();
                    String headMessage = t.getMessage();
                    if(headMessage == null) {
                        headMessage = Locale.getErrors().unexpectedException(t.getClass().getName());
                    }
                    builder.append("Throwable message:\n");
                    builder.append(headMessage);
                    
                    builder.append("\nRoot cause trace:\n");
                    String rootMessage = rootCause.getMessage();
                    if(rootMessage == null) {
                        rootMessage = Locale.getErrors().unexpectedException(rootCause.getClass().getName());
                    }
                    builder.append(rootMessage);
                    
                    StackTraceElement[] stackTrace = rootCause.getStackTrace();
                    int limit = 5;
                    for(int index = 0; index < stackTrace.length && index < limit; ++index) {
                        builder.append('\n');
                        builder.append(stackTrace[index].toString());
                    }
                    if (stackTrace.length > limit) {
                        builder.append('\n');
                        builder.append("[Omitting remaining trace]");
                    }
                    Dialog.alert(builder.toString());
                }
            }
        });
    }
}

