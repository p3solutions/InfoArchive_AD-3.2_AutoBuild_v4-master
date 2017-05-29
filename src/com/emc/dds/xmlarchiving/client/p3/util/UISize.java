package com.emc.dds.xmlarchiving.client.p3.util;

public class UISize {

    // The fudge-factor in determining the entire app's height.  
    private static final int BASE_HEIGHT = 150;
    
    public static int getInteriorResultsHeightInt() {
        // Also update with relative changes
        // .template-SearchResultPane .core-LDMBorderDecorator
        return 330;
    }
    
    public static int getSearchHeightInt() {
        // Also update with relative changes
        // .template-SearchPane td.core-LDMBorderDecorator-middle-center
        return 250;
    }
    
    public static int getMenuHeightInt() {
        return 20;
    }
    
    public static String getInteriorResultsHeightStr() {
        return getInteriorResultsHeightInt() + "px";
    }
    
    public static String getEntireHeightStr() {
        int height = BASE_HEIGHT + getInteriorResultsHeightInt() + getSearchHeightInt() +
                getMenuHeightInt();
        return height + "px";
    }

	public static int getDetaSetHeightInt() {
		return 250;
	}

	public static String getEntireHeightWithDSStr() {
		int height = BASE_HEIGHT + getInteriorResultsHeightInt() + getSearchHeightInt() +
                getDetaSetHeightInt();
        return height + "px";
	}
    
}