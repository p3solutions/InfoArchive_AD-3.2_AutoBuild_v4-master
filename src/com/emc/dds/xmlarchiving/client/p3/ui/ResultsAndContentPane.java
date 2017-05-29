package com.emc.dds.xmlarchiving.client.p3.ui;

import com.emc.dds.xmlarchiving.client.configuration.ApplicationSettings;
import com.emc.dds.xmlarchiving.client.configuration.SearchConfiguration;
import com.emc.dds.xmlarchiving.client.event.ApplicationEvent;
import com.emc.dds.xmlarchiving.client.event.ApplicationEventListener;
import com.emc.dds.xmlarchiving.client.ui.ContentViewPane;
import com.emc.dds.xmlarchiving.client.ui.SearchResultPane;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;

/**
 * @author Curtis Fleming
 *
 */
public class ResultsAndContentPane extends DockLayoutPanel implements ApplicationEventListener {
    
    private final ApplicationSettings applicationSettings;
    
    private final SearchResultPane searchResultPane;
    
    private final ContentViewPane contentViewPane;
    
    private final int percentageWidth;
    
    public ResultsAndContentPane(ApplicationSettings applicationSettingsIn, 
            SearchResultPane searchResultPaneIn, ContentViewPane contentViewPaneIn,
            int percentageWidthIn) {
        super(Unit.PCT);
        applicationSettings = applicationSettingsIn;
        searchResultPane = searchResultPaneIn;
        contentViewPane = contentViewPaneIn;
        percentageWidth = percentageWidthIn;
        
        searchResultPane.init();

        applicationSettings.getState().addListener(this);
        
        // Initial layout until a search config is selected
        this.addWest(searchResultPane, percentageWidth);
        this.add(contentViewPane);
    }
    
    private void changeDetailedPaneVisibility() {
        SearchConfiguration config = applicationSettings.getState().getCurrentSearchSetting().getSearchConfiguration();
        contentViewPane.removeFromParent();
        searchResultPane.removeFromParent();
        if(config.usesDetailsPane()) {
            final Integer widthObj = config.getSearchResultsWidthPercent();
            int width = (widthObj == null) ? percentageWidth : widthObj.intValue();
            width = Math.max(width, 0);
            width = Math.min(width, 100);
            this.addWest(searchResultPane, width);
            this.add(contentViewPane);
        }
        else {
            this.addSouth(searchResultPane, 100);
        }
    }

    @Override
    public void handle(ApplicationEvent event) {
        switch (event.getType()) {
            // MOD Visibility of detailed pane now depends on search tab
            case ApplicationEvent.NESTED_SEARCH_SUBMIT_EVENT:
            case ApplicationEvent.SEARCH_SELECTED_EVENT:
                changeDetailedPaneVisibility();
                break;
        }
    }
    
}
