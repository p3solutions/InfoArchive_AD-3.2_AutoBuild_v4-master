/***************************************************************************************************
 * Copyright (c) 2009, 2011 EMC Corporation All Rights Reserved
 **************************************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

import java.util.Map;
import java.util.Set;

/**
 * A search configuration object stores all information required for a search.
 * This information defines how queries are generated and how the result is
 * shown in a pane. WARNING: This class is experimental and may change in future
 * DDS releases.
 */
public class SearchConfiguration {

	private final int contentResultPaneWidth;

	private final String xformURI;

	private final String xquery;

	private final String predicate; // MOD introduced a scalar attribute

	private final Map<String, SearchField> searchFields;

	private final Map<String, SearchResultItem> searchResultItems;

	private final Map<String, NestedSearch> nestedSearches;

	private final Map<String, Report> reports;

	private final Set<String> operations;

	private final boolean usesDetailsPane; // MOD introduced new attribute,
											// usesDetailsPane

	private final Integer searchResultsWidthPercent; // MOD new attribute

	private final boolean isPersistentReport; // MOD new attribute

	private final String reportOutputType; // MOD new attribute

	private final String persistentReportEmail; // MOD new attribute

	public SearchConfiguration(boolean usesDetailsPane,
			Integer searchResultsWidthPercent, String xformURI, String xquery,
			String predicate, Map<String, SearchField> searchFields,
			Map<String, SearchResultItem> searchResultItems,
			Map<String, NestedSearch> nestedSearches,
			Map<String, Report> reports, Set<String> operations,
			boolean isPersistentReport, String reportOutputType,
			String persistentReportEmail,int contentResultsPaneWidth) {
		super();
		this.usesDetailsPane = usesDetailsPane;
		this.searchResultsWidthPercent = searchResultsWidthPercent;
		this.searchFields = searchFields;
		this.searchResultItems = searchResultItems;
		this.nestedSearches = nestedSearches;
		this.xformURI = xformURI;
		this.reports = reports;
		this.xquery = xquery;
		this.predicate = predicate;
		this.operations = operations;
		this.isPersistentReport = isPersistentReport;
		this.reportOutputType = reportOutputType;
		this.persistentReportEmail = persistentReportEmail;
		this.contentResultPaneWidth = contentResultsPaneWidth;
	}

	public boolean usesDetailsPane() {
		return this.usesDetailsPane;
	}

	public Integer getSearchResultsWidthPercent() {
		return searchResultsWidthPercent;
	}

	public String getXformURI() {
		return this.xformURI;
	}

	public String getXquery() {
		return this.xquery;
	}

	public String getPredicate() {
		return this.predicate;
	}

	public Map<String, SearchField> getSearchFields() {
		return this.searchFields;
	}

	public Map<String, SearchResultItem> getSearchResultItems() {
		return this.searchResultItems;
	}

	public Map<String, NestedSearch> getNestedSearches() {
		return this.nestedSearches;
	}

	public Map<String, Report> getReports() {
		return this.reports;
	}

	public Set<String> getOperations() {
		return this.operations;
	}

	public boolean isPersistentReport() {
		return this.isPersistentReport;
	}

	public String getReportOutputType() {
		return this.reportOutputType;
	}

	public String getPersistentReportEmail() {
		return this.persistentReportEmail;
	}

	public int getContentResultsPaneWidth() { // Flatirons
		return this.contentResultPaneWidth;
	}
}
