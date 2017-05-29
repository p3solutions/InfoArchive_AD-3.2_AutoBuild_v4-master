/*******************************************************************************
 * Copyright (c) 2011 EMC Corporation All Rights Reserved
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import java_cup.internal_error;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.emc.dds.xmlarchiving.client.configuration.SearchConfiguration;
import com.emc.dds.xmlarchiving.client.configuration.SearchField;
import com.emc.dds.xmlarchiving.client.configuration.SearchSetting;
import com.emc.documentum.xml.dds.gwt.client.AbstractDDSXQueryDataSource;
import com.emc.dds.xmlarchiving.client.ui.RefreshListener;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableElement;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXDBException;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXQueryValue;
import com.emc.documentum.xml.gwt.client.xml.XMLParser;
import com.emc.documentum.xml.gwt.client.AbstractDataSource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.regexp.shared.*;

public class StoredQueryDataSource extends AbstractSettingDataSource {
    
    private static final String WHERE_CLAUSE_INSERT_FLAG = "(: xmlarchiving-insert-where-clause-parameter-values :)";
    private static final String WHERE_CLAUSE_APPEND_FLAG = "(: xmlarchiving-append-where-clause-parameter-values :)";
    private static final String ARG = "%ARG%";
    
    private static final String CURRENT_USER = "currentuser";
    private static final String RESTRICTIONS = "restrictions";
    private static final String FIRST = "first";
    private static final String LAST = "last";
    
    private static final boolean VERBOSE_FTS_SYNTAX = true;
    
    private String xquery;
    private int count;
    private SearchSetting searchSetting;
    private String currentUserName;
    private String restrictions;
    private final AsyncCallback<?> failureHandler;
    private final ArrayList<RefreshListener> refreshListeners = new ArrayList<RefreshListener>(4);
    
    public StoredQueryDataSource(String xquery, Map<String, String> fields, 
            SearchSetting searchSetting, String currentUserName, String restrictions,
            AsyncCallback<?> failureHandler) {
        super();
        this.failureHandler = failureHandler;
        setFields(fields);
        this.searchSetting = searchSetting;
        this.currentUserName = currentUserName;
        this.restrictions = restrictions;
        setXQuery(xquery);
    }
      public String getCurrentUserName() {
        return currentUserName;
    } 
    public void setXQuery(String rawXQuery) {
        this.xquery = injectXFormData(rawXQuery);
    }
    
    public void addRefreshListener(RefreshListener listener) {
        refreshListeners.add(listener);
    }
    
    public void removeRefreshListener(RefreshListener listener) {
        refreshListeners.remove(listener);
    }
    
    @Override
    public void onFailure(Throwable t) {
        super.onFailure(t);
        if(this.failureHandler != null)
            failureHandler.onFailure(t);
    }
    
    private Set<String> getPredefinedFields() {
        HashSet<String> set = new HashSet<String>();
        set.add(CURRENT_USER);
        set.add(FIRST);
        set.add(LAST);
        return set;
    }
    
    private boolean needsWhereClause() {
        if(getFields().isEmpty()) {
            return false;
        }
        Set<String> predefined = getPredefinedFields();
        for(String fieldName : getFields().keySet()) {
            if(!predefined.contains(fieldName)) {
                return true;
            }
        }
        return false;
    }
    
    private String injectXFormData(final String query) {
        String result = injectWhereClause(query);
        result = injectFlexibleCode(result);
        result = purgeComentFlags(result);
        return result;
    }
    
    private String purgeComentFlags(String result) {
        return result.replaceAll("\\(: \\w+ :\\)", "");
    }
    
    private String injectWhereClause(final String query) {
        StringBuffer preds = new StringBuffer();
        Map<String, String> xformValues = getFields();
        SearchConfiguration searchConfiguration = this.searchSetting.getSearchConfiguration();
        Map<String, SearchField> definedFields = searchConfiguration.getSearchFields();
        
        boolean previous = false;
        if(needsWhereClause()) {
            String predicate = searchConfiguration.getPredicate();
            if(predicate != null && predicate.length() > 0) {
                preds.append(predicate);
                previous = true;
            }
            
            for(Map.Entry<String, SearchField> entry : definedFields.entrySet()) {
                String fieldName = entry.getKey();
                SearchField field = entry.getValue();
                
                // if the field supplies a where-clause part and the xform has a value for it,
                // inject it.  Only search fields with <whereclauseparameter> are injected in
                // a where-clause, as opposed to <flexiblecode> which is not. 
                String xFrag = field.getXQuery();
                if(xformValues.containsKey(fieldName) && xFrag != null && xFrag.length() > 0) {
                    boolean fts = field.isFullText();
                    String userText = xformValues.get(fieldName);
                    userText = userText.replaceAll("&(?![#a-zA-Z0-9]+;)", "&amp;");
                    
                    if(previous) {
                        preds.append(" and ");
                    }
                    
                    // Inject quoted user text into fragment
                    boolean usesArgInjection = xFrag.contains(ARG);
                    xFrag = xFrag.replaceAll(ARG, '\'' + userText + '\'');
                    
                    if(fts) {
                        // It is expected that full text search uses a fts index with
                        // option to convert terms to lower case
                        if(VERBOSE_FTS_SYNTAX) {
                            preds.append(xFrag);
                            preds.append(" contains text '");
                            preds.append(userText.toLowerCase());
                            preds.append("' using wildcards");
                        }
                        else {
                            preds.append("xhive:fts(");
                            preds.append(xFrag);
                            preds.append(", '");
                            preds.append(userText.toLowerCase());
                            preds.append("')");
                        }
                    }
                    else {
                        preds.append(xFrag);
                        if(!usesArgInjection) {
                            preds.append(' ');
                            preds.append(field.getOperator());
                            preds.append(" '");
                            preds.append(userText);
                            preds.append('\'');
                        }
                    }
                    previous = true;
                }
            }
        }
        
        String predicates = preds.toString();
        String insertPredicates = "where " + predicates;
        String appendPredicates = "and " + predicates;
        
        String result = query.replace(WHERE_CLAUSE_INSERT_FLAG, insertPredicates);
        result = result.replace(WHERE_CLAUSE_APPEND_FLAG, appendPredicates);
        return result;
    }
    
    private String injectFlexibleCode(final String query) {
        String result = query;
        
        Map<String, String> xformValues = getFields();
        Map<String, SearchField> definedFields = this.searchSetting.getSearchConfiguration().getSearchFields();
        for(Map.Entry<String, SearchField> entry : definedFields.entrySet()) {
            String fieldName = entry.getKey();
            SearchField field = entry.getValue();
            
            // if the field supplies a code part, inject it
            String code = field.getFlexibleCode();
            if(xformValues.containsKey(fieldName) && code != null && code.length() > 0) {
                // Replace the arg flags with the xform values
                code = code.replace(ARG, xformValues.get(fieldName));
				code = code.replaceAll("&(?![#a-zA-Z0-9]+;)", "&amp;");
                String target = createReplacementPattern(fieldName);
                result = result.replace(target, code);
            }
        }
        return result;
    }
    
    private static String createReplacementPattern(String fieldName) {
        StringBuilder b = new StringBuilder();
        b.append("(: ");
        b.append(fieldName);
        b.append(" :)");
        return b.toString();
    }
    
    @Override
    protected String getURI(SerializableXQueryValue contextValue) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    protected Map<String, String> getVariables(SerializableXQueryValue contextValue) {
        return getFields();
    }
    
    @Override
	public String getXQuery(SerializableXQueryValue contextValue) {
        Map<String, String> variables = getFields();
        int lastItem = getLast();
        variables.put(FIRST, "" + (getFirst() + 1));
        variables.put(LAST, "" + (lastItem == -1 ? -1 : lastItem + 1));
        variables.put(CURRENT_USER, this.currentUserName);
        variables.put(RESTRICTIONS, restrictions);
        return this.xquery;
    }
    
    public String getRestrictions() {
      return restrictions;
    }
    
    @Override
    public AbstractDataSource<SerializableXQueryValue> cloneDataSource() {
        return new StoredQueryDataSource(this.xquery, getFields(), this.searchSetting, 
                this.currentUserName, this.restrictions, this.failureHandler);
    }
    
    @Override
    public SerializableXQueryValue getParent(SerializableXQueryValue userObject) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void onSuccess(List<SerializableXQueryValue> result) {
        if(result.size() == 1) {
            SerializableElement element = null;
            try {
                element = (SerializableElement) result.get(0).asNode();
            }
            catch(SerializableXDBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String total = element.getAttribute("total");
            this.count = Integer.parseInt(total);
        }
        else {
            this.count = -1;
        }
        setData(result, this.count);
    }
    
    @Override
    public void refresh() {
        for(RefreshListener listener : refreshListeners) {
            listener.dataSourceAboutToChange();
        }
        super.refresh();
    }
}
