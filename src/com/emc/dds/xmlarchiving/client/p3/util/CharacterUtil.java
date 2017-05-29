package com.emc.dds.xmlarchiving.client.p3.util;

public class CharacterUtil {
    
    public static final String escapeXMLAttribute(String rawAttr) {
        return rawAttr.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
    }
    
}
