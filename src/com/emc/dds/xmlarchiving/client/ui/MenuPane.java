/***************************************************************************************************
 * Copyright (c) 2009, 2011 EMC Corporation All Rights Reserved
 **************************************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.emc.dds.xmlarchiving.client.configuration.ApplicationSettings;
import com.emc.dds.xmlarchiving.client.configuration.Hierarchy;
import com.emc.dds.xmlarchiving.client.configuration.NodeSetting;
import com.emc.dds.xmlarchiving.client.event.ApplicationEvent;
import com.emc.dds.xmlarchiving.client.event.PaneLoadedEvent;
import com.emc.dds.xmlarchiving.client.i18n.Locale;
import com.emc.dds.xmlarchiving.client.ui.image.MainImageBundle;
import com.emc.documentum.xml.dds.gwt.client.rpc.DDSServices;
import com.emc.documentum.xml.dds.gwt.client.util.ApplicationContext;
import com.emc.documentum.xml.gwt.client.FailureHandler;
import com.emc.documentum.xml.gwt.client.ui.MenuBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Updated by EMC/Flatirons (Curtis Fleming) to show the heirarchy
 * 
 * Pane that represents a menu bar. The current implementation only has sub menu's for languages and
 * locales. WARNING: This class is experimental and may change in future DDS releases.
 */
public class MenuPane extends ContentPane {
    
    private Map<String, String> languageByLocale;
    
    private final boolean showLanguages;
    
    private MenuBar languagesMenu;
    
    public MenuPane(ApplicationSettings applicationSettings, boolean showLanguages, boolean showHierarchy) {
        super(applicationSettings);
        applicationSettings.getState().addListener(this);
        this.showLanguages = showLanguages;
        
        MenuBar menuBar = new MenuBar();
        
        if(showHierarchy) {
            Hierarchy hierarchy = applicationSettings.getHierarchy();
            MenuItem item = createMenuItem(hierarchy.getRootNodeSetting(), applicationSettings, false);
            menuBar.addItem(item);
        }
        
        if(showLanguages) {
            this.languagesMenu = new MenuBar(true);
            menuBar.addItem(new MenuItem(Locale.getLabels().language(), this.languagesMenu));
        }
        initWidget(menuBar);
        this.setVisible(showLanguages || showHierarchy);
        addStyleName(getPaneStyle());
    }
    
    @Override
    public int getPaneType() {
        return MENU_PANE;
    }
    
    @Override
    public String getPaneName() {
        return MENU_PANE_NAME;
    }
    
    @Override
    public void loadData() {
        if(this.showLanguages) {
            initLanguagesMenuBar();
        }
    }
    
    private void initLanguagesMenuBar() {
        
        DDSServices.getI18NService().getISO3Languages("eng", new AsyncCallback<Map<String, String>>() {
            @Override
            public void onFailure(Throwable caught) {
                FailureHandler.handle(this, caught);
            }
            
            @Override
            public void onSuccess(Map<String, String> result) {
                
                MenuPane.this.languageByLocale = result;
                
                if(!isLoaded()) {
                    setLoaded(true);
                    fireEvent(new PaneLoadedEvent(getPaneType()));
                }
            }
        });
    }
    
    private void addLanguageMenuItem(final MenuBar menuBar, final String newLocale) {
        final String currentLocale = getApplicationSettings().getState().getCurrentLocale();
        boolean selected = currentLocale.equals(newLocale);
        String newLanguage = this.languageByLocale.get(newLocale);
        AbstractImagePrototype image = selected ? MainImageBundle.INSTANCE.check16() : MainImageBundle.INSTANCE.empty16();
        String text = selected ? "<b>" + newLanguage + "</b>" : newLanguage;
        
        menuBar.addItem(image, text, true, new Command() {
            @Override
            public void execute() {
                if(!currentLocale.equals(newLocale)) {
                    getApplicationSettings().getState().setCurrentLocale(newLocale);
                    refreshMenuItems();
                }
            }
        });
    }
    
    private void refreshMenuItems() {
        if(this.showLanguages) {
            this.languagesMenu.clearItems();
            ApplicationContext applicationContext = getApplicationSettings().getApplicationContext();
            Collection<String> locales = applicationContext.getLocaleCollection();
            if(locales != null && locales.size() > 0) {
                for(String locale : applicationContext.getLocaleCollection()) {
                    addLanguageMenuItem(this.languagesMenu, locale);
                }
                this.setVisible(true);
            }
            else {
                this.setVisible(false);
            }
        }
    }
    
    @Override
    public void handle(ApplicationEvent event) {
        super.handle(event);
        switch(event.getType()) {
            case ApplicationEvent.NODE_SELECTED_EVENT:
                refreshMenuItems();
                break;
        }
    }
    
    /**
     * 
     * @param node
     * @param applicationSettings
     * @param hasAction this item will fire if hasAction is true or 
     *      this itemis a leaf
     * @return
     */
    public static MenuItem createMenuItem(final NodeSetting node, 
            final ApplicationSettings applicationSettings, boolean hasAction) {
        if(node == null)
            return null;
        
        Command command = new Command() {
            @Override
            public void execute() {
                applicationSettings.getState().setCurrentSetting(node);
            }
        };
        
        List<NodeSetting> children = node.getChildren(applicationSettings.getRole());
        // Recursion case, has children
        if(children != null && children.size() > 0) {
            MenuBar bar = new MenuBar(true);
            // IMPORTANT: IE7 menu popup doesn't fully expand without this height
            bar.setHeight("100%");
            for(NodeSetting child : children) {
                MenuItem childItem = createMenuItem(child, applicationSettings, true);
                if(childItem != null)
                    bar.addItem(childItem);
            }
            MenuItem item = new MenuItem(node.getLabel(), bar);
            if(hasAction) {
                item.setCommand(command);
            }
            return item;
        }
        // Leaf case
        else {
            return new MenuItem(node.getLabel(), command);
        }
    }
    
}
