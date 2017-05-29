/*******************************************************************************
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.emc.dds.xmlarchiving.client.authorization.Role;
import com.emc.dds.xmlarchiving.client.configuration.ApplicationSettings;
import com.emc.dds.xmlarchiving.client.configuration.ContentViewSetting;
import com.emc.dds.xmlarchiving.client.configuration.NodeSetting;
import com.emc.dds.xmlarchiving.client.event.ApplicationEvent;
import com.emc.dds.xmlarchiving.client.event.ApplicationEventListener;
import com.emc.dds.xmlarchiving.client.event.NodeSelectedEvent;
import com.emc.dds.xmlarchiving.client.i18n.Locale;
import com.emc.dds.xmlarchiving.client.ui.image.MainImageBundle;
import com.emc.documentum.xml.dds.gwt.client.LogCenterFailureListener;
import com.emc.documentum.xml.dds.gwt.client.rpc.DDSServices;
import com.emc.documentum.xml.dds.gwt.client.rpc.LogCenterServiceAsync;
import com.emc.documentum.xml.dds.gwt.client.util.DDSURI;
import com.emc.documentum.xml.gwt.client.Dialog;
import com.emc.documentum.xml.gwt.client.ui.Button;
import com.emc.documentum.xml.gwt.client.ui.DialogBox;
import com.emc.documentum.xml.xproc.gwt.client.rpc.SerializablePipelineInput;
import com.emc.documentum.xml.xproc.gwt.client.rpc.SerializableQName;
import com.emc.documentum.xml.xproc.gwt.client.rpc.SerializableSource;
import com.emc.documentum.xml.xproc.gwt.client.ui.XProcFrame;
import com.emc.documentum.xml.xproc.gwt.client.ui.XProcFrame.DefaultXProcRequestSerializer;
import com.emc.documentum.xml.xproc.gwt.client.ui.XProcFrame.XProcRequestSerializer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The content view pane shows the HTML or PDF of an XML document. The settings
 * of the stylesheets and xproc pipelines used are configurable. In addition to
 * resize events, the content view listens to search result item and tree node
 * selected events. WARNING: This class is experimental and may change in future
 * DDS releases.
 */
public class ContentViewPane extends ContentPane implements ApplicationEventListener {

	public static final int RECORD_DETAILS_TYPE = 0;
	public static final int TICKET_INFO_TYPE = 1;

	private final XProcFrame xprocFrame = new XProcFrame();

	private SerializableSource lastSource;
	private String lastStyleSheetURI;
	// private String lastPipelineURI;
	// private String lastContentType;
	private String lastXQueryURI;
	// private boolean lastUseExternalParameters;
	private boolean ticketInfoWasCleared = false;
	private Map<SerializableQName, String> lastParameters;
	private String lastTitle;

	private int type;
	private LDMBorderDecorator decorator;
	private String paneTitle;
	private Element currentElement;
	private NodeSetting currentNodeSetting;

	public ContentViewPane(ApplicationSettings applicationSettings, String paneTitle, int type) {
		super(applicationSettings);
		this.paneTitle = paneTitle;
		this.type = type;
		applicationSettings.getState().addListener(this);
		VerticalPanel contentViewPanel = new VerticalPanel();
		contentViewPanel.add(xprocFrame);
		xprocFrame.getElement().setPropertyString("frameborder", "no");
		if (type == RECORD_DETAILS_TYPE) {
			xprocFrame.setHeight(SearchResultPane.CORE_HEIGHT);
		}
		contentViewPanel.addStyleName(getPaneStyle());
		decorator = new LDMBorderDecorator(paneTitle, contentViewPanel);
		initWidget(decorator);
	}

	@Override
	public int getPaneType() {
		return CONTENT_VIEW_PANE;
	}

	@Override
	public String getPaneName() {
		if (type == TICKET_INFO_TYPE) {
			return CONTENT_VIEW_PANE_NAME + "TicketInfo";
		}
		return CONTENT_VIEW_PANE_NAME;
	}

	@Override
	public void loadData() {
		// getDocumentInfo();
	}

	private class ZoomResultsButton extends Button implements ClickHandler {

		private ContentViewPane parent;

		public ZoomResultsButton() {
			setText("Expand");
			addClickHandler(this);
		}

		public void setParent(ContentViewPane theParent) {
			parent = theParent;
		}

		@Override
		public void onClick(ClickEvent event) {
			runExport();
		}

		private void runExport() {
			String htmlTable = parent.toString();
			ContentViewPane viewPane = new ContentViewPane(getApplicationSettings(), "Test", RECORD_DETAILS_TYPE);
			viewPane.displayObject(currentElement, currentNodeSetting);
			htmlTable = viewPane.toString();
			// just take the iframe from this html
			int iFrameOffset = htmlTable.indexOf("<iframe");
			int iFrameClose = htmlTable.indexOf("</iframe>");
			String iFrameHTMLwithStyles = htmlTable.substring(iFrameOffset, iFrameClose) + "</iframe>";
			// now strip out the style info
			String iFrameHTML = iFrameHTMLwithStyles.substring(0, iFrameHTMLwithStyles.indexOf("style="))
					+ "style='height: 800px; width: 800px;'</iframe>";
			final DialogBox zoomBox = new DialogBox();

			zoomBox.setText("Details");
			zoomBox.setWidth("1200px");
			zoomBox.setHeight("900px");
			viewPane.xprocFrame.setHeight("900px");
			viewPane.xprocFrame.setWidth("1200px");
			zoomBox.setGlassEnabled(true);
			VerticalPanel contents = new VerticalPanel();
			SimplePanel htmlPanel = new SimplePanel();
			HTML htmlContents = new HTML("<html><body>" + iFrameHTML + "</body></html>");
			htmlPanel.add(htmlContents);
			contents.add(viewPane.xprocFrame);
			zoomBox.add(contents);
			// leave room for the panel on the bottom that contains the close
			// button
			contents.setSize("100%", "800px");

			// now add the button and the panel
			ClickHandler listener = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					zoomBox.hide();
				}
			};
			Button button = new Button("Close", listener);
			SimplePanel holder = new SimplePanel();
			holder.add(button);

			contents.add(holder);
			zoomBox.setVisible(true);
			zoomBox.show();
		}

	}

	private class ExportPDFButton extends Button implements ClickHandler {

		/**
		 * Used to temporarily encode '&' in content of the encoded URL
		 */
		private static final String AMP_FLAG = "-_AMP_FLAG_-";
		private ContentViewSetting previewSetting;

		public ExportPDFButton() {
			setText("Export PDF");
			addClickHandler(this);
			if (currentElement != null) {
				// Get the ContentViewSetting associated with the click
				String typeAttr = currentElement.getAttribute("type");
				previewSetting = currentNodeSetting.getContentViewSetting(getApplicationSettings(), typeAttr);
			}
		}

		@Override
		public void onClick(ClickEvent event) {
			// Check if the exported PDF should be generated OR
			// fetched from the database
			String baseBlobURL = previewSetting.getExportPDFFromDatabase();
			if ("".equalsIgnoreCase(baseBlobURL)) {
				runExport();
			} else {
				SerializableQName blobQName = new SerializableQName("relativePath");
				String relativeBlobURL = lastParameters.get(blobQName);
				SerializableQName titleQName = new SerializableQName("pdfFileName");
				String docTitle = lastParameters.get(titleQName);

				Window.open(baseBlobURL + relativeBlobURL + "/" + docTitle, "_self", "enabled");
			}
		}

		private void runExport() {
			String pipeline = previewSetting.getExportPFFXprocURI();
			String pipelineURI = "".equalsIgnoreCase(pipeline) ? "dds://DOMAIN=resource/xproc/xqueryResultsToPDF.xpl"
					: pipeline;
			String contentType = "application/pdf";

			Map<String, String> headers = new HashMap<String, String>(2);
			headers.put("Content-Disposition", "attachment; filename=" + lastTitle + ".pdf");

			SerializablePipelineInput input = new SerializablePipelineInput();
			input.addInput("source", lastSource);
			input.addInput("query", new SerializableSource(lastXQueryURI));
			input.addInput("stylesheet", new SerializableSource(lastStyleSheetURI));
			if (lastParameters != null) {
				for (SerializableQName qname : lastParameters.keySet()) {
					input.addParameter("parameters", qname, lastParameters.get(qname));
				}
			}

			XProcRequestSerializer requestSerializer = new DefaultXProcRequestSerializer();
			String url = requestSerializer.getRequestString(pipelineURI, input, contentType, true, headers);

			submitPostForm(url);
		}

		/**
		 * The trick to this is that the content to POST is generated
		 *
		 * The content can have the '&' character which is escaped as a AMP_FLAG
		 * and unescaped when the URL is parsed into the form. This is due to
		 * URLs using & as the parameter delimiter.
		 *
		 * @param theGETURL
		 */
		private void submitPostForm(String theGETURL) {
			final int qIndex = theGETURL.indexOf('?');
			String baseURL = theGETURL.substring(0, qIndex);
			String[] args = theGETURL.substring(qIndex + 1).split("&");

			// Build form that points to __download iframe
			// FormPanel form = new FormPanel(downloadID);
			FormPanel form = new FormPanel();
			form.setAction(baseURL);
			form.setMethod(FormPanel.METHOD_POST);

			// Create a panel to hold all of the form widgets.
			VerticalPanel panel = new VerticalPanel();
			form.setWidget(panel);
			for (String arg : args) {
				arg = arg.replaceAll(AMP_FLAG, "&");
				final int eIndex = arg.indexOf('=');
				final String name = arg.substring(0, eIndex);
				final String value = arg.substring(eIndex + 1);
				panel.add(new Hidden(name, value));
			}

			RootPanel.get().add(form);

			form.submit();
		}

	}

	private class ExportSourceButton extends Button implements ClickHandler {

		/**
		 * Used to temporarily encode '&' in content of the encoded URL
		 */
		private static final String AMP_FLAG = "-_AMP_FLAG_-";
		private ContentViewSetting previewSetting;

		public ExportSourceButton() {
			setText("Export Source");
			addClickHandler(this);
			if (currentElement != null) {
				// Get the ContentViewSetting associated with the click
				String typeAttr = currentElement.getAttribute("type");
				previewSetting = currentNodeSetting.getContentViewSetting(getApplicationSettings(), typeAttr);
			}
		}

		@Override
		public void onClick(ClickEvent event) {
			// Check if the exported PDF should be generated OR
			// fetched from the database
			String baseBlobURL = previewSetting.getExportSRCFromDatabase();
			SerializableQName blobQName = new SerializableQName("relativePath");
			String relativeBlobURL = lastParameters.get(blobQName);
			SerializableQName titleQName = new SerializableQName("srcFileName");
			String docTitle = lastParameters.get(titleQName);

			Window.open(baseBlobURL + relativeBlobURL + "/" + docTitle, "_self", "enabled");

		}
	}

	private void showExportButton() {
		if (!ticketInfoWasCleared) {
			ZoomResultsButton zoomBtn = new ZoomResultsButton();
			zoomBtn.setParent(this);
			HorizontalPanel hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LOCALE_START);
			hPanel.setSize("100%", "100%");

			Role role = getApplicationSettings().getRole();
			if (role.hasOperationAuthorization("export")) {
				if (displayPDFButton()) {
					hPanel.add(new ExportPDFButton());
				}
				if (displaySourceButton()) {
					hPanel.add(new ExportSourceButton());
				}
			}
			hPanel.add(zoomBtn);
			decorator.setFooterWidget(hPanel);
		}
	}

	private boolean displayPDFButton() {
		ContentViewSetting previewSetting;
		if (currentElement != null) {
			// Get the ContentViewSetting associated with the click
			String typeAttr = currentElement.getAttribute("type");
			previewSetting = currentNodeSetting.getContentViewSetting(getApplicationSettings(), typeAttr);
			String baseBlobURL = previewSetting.getExportPDFFromDatabase();

			if (!baseBlobURL.equals("") && lastParameters != null) {
				SerializableQName titleQName = new SerializableQName("pdfFileName");
				String docTitle = lastParameters.get(titleQName);
				if (docTitle.equals("")) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean displaySourceButton() {
		if (currentElement != null) {
			// Get the ContentViewSetting associated with the click
			String typeAttr = currentElement.getAttribute("type");
			ContentViewSetting previewSetting = currentNodeSetting.getContentViewSetting(getApplicationSettings(),
					typeAttr);

			// Check if export doc has been enabled
			String baseBlobURL = previewSetting.getExportSRCFromDatabase();
			if ("".equalsIgnoreCase(baseBlobURL)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private void setXProcFrame(SerializableSource source, String styleSheetUri, String pipelineUri, String contentType,
			String xqueryURI, boolean useExternalOutput, String fileName, Map<SerializableQName, String> parameters,
			String exportStyleSheetUri) {

		// mod Get handles on latest request parameters
		lastSource = source;
		lastStyleSheetURI = "".equalsIgnoreCase(exportStyleSheetUri) ? styleSheetUri : exportStyleSheetUri;
		// lastPipelineURI = pipelineUri;
		// lastContentType = contentType;
		lastXQueryURI = xqueryURI;
		// lastUseExternalParameters = useExternalParameters;
		lastParameters = parameters == null ? null : new HashMap<SerializableQName, String>(parameters);

		Role role = getApplicationSettings().getRole();
		SerializablePipelineInput input = new SerializablePipelineInput();
		input.addInput("source", source);
		input.addInput("stylesheet", new SerializableSource(styleSheetUri));
		if (!(xqueryURI == null || xqueryURI.isEmpty())) {
			input.addInput("query", new SerializableSource(xqueryURI));
		}
		String unauthorizedFields = role.getUnauthorizedFields();
		if (unauthorizedFields.length() > 0) {
			input.addParameter("parameters", new SerializableQName("not-authorized"), unauthorizedFields);
		}
		if (parameters != null) {
			for (SerializableQName qname : parameters.keySet()) {
				input.addParameter("parameters", qname, parameters.get(qname));
			}
		}
		xprocFrame.setPipeline(pipelineUri);
		xprocFrame.setPipelineInput(input);
		xprocFrame.setContentType(contentType);
		if (fileName.equals("")) {
			xprocFrame.clearResponseHeaders();
		} else {
			xprocFrame.setResponseHeader("Content-Disposition", "attachment; filename=" + fileName);
		}
		xprocFrame.setUseExternalOutput(useExternalOutput);
		xprocFrame.refresh();

		// mod this line adds the export button
		if (source != null && (source.getSystemID() == null || source.getSystemID().indexOf("chainofcustody") < 0)) {
			showExportButton();
		}
	}

	private void setXProcFrame(String xqueryURI, SerializableSource source, ContentViewSetting previewSetting,
			Map<SerializableQName, String> parameters) {

		setXProcFrame(source, previewSetting.getXslURI(), previewSetting.getPipelineURI(),
				previewSetting.getContentType(), xqueryURI, previewSetting.useExternalOutput(),
				previewSetting.getFileName(), parameters, previewSetting.getExportPDFXslURI());
	}

	private void setTitle(String title, boolean noTitle) {
		title = new HTML(new HTML(title).getText()).getText();
		lastTitle = title;
		if (title == null || title.length() == 0) {
			title = Locale.getLabels().noTitle();
		}
		if (type != TICKET_INFO_TYPE) {
			title = noTitle ? paneTitle : paneTitle + ": " + title;
			decorator.setTopText(title, false);
		}
	}

	public void displayData(String data, ContentViewSetting previewSetting, String title, boolean noTitle,
			Map<SerializableQName, String> parameters) {
		setTitle(title, false);
		setXProcFrame(null, new SerializableSource(data, null, null), previewSetting, parameters);
	}

	public void displayObject(Element selectedElement, NodeSetting nodeSetting) {
		currentElement = selectedElement;
		currentNodeSetting = nodeSetting;
		ApplicationSettings applicationSettings = getApplicationSettings();
		String typeAttr = selectedElement.getAttribute("type");
		String uri = selectedElement.getAttribute("uri");
		String title = selectedElement.getAttribute("title");
		ContentViewSetting previewSetting = nodeSetting.getContentViewSetting(applicationSettings, typeAttr);
		if (previewSetting == null) {
			Dialog.alert(Locale.getMessages().noContentViewSettingFound(typeAttr),
					MainImageBundle.INSTANCE.error48().createImage());
		} else {
			String xquery = previewSetting.getXquery();
			if (xquery != null && !"".equals(xquery)) {
				displayBusinessObject(title, typeAttr, selectedElement, previewSetting);
				Role role = applicationSettings.getRole();
				// logRequest(applicationSettings.getHierarchy().getTitle(),
				// typeAttr, selectedElement,
				// applicationSettings.getUserName(),
				// role.getUnauthorizedFields());
				// logRequest("yuva", typeAttr, selectedElement,"test",
				// role.getUnauthorizedFields());
			} else if (uri != null && !"".equals(uri)) {
				displayUri(uri, title, previewSetting, null);
			} else {
				Dialog.alert(Locale.getMessages().noURIFound(), MainImageBundle.INSTANCE.error48().createImage());
			}
		}
	}

	public void displayUri(String uri, String title, ContentViewSetting previewSetting,
			Map<SerializableQName, String> parameters) {
		setTitle(title, false);
		// this.decorator.setTopText(title, false);
		setXProcFrame(null, new SerializableSource(uri), previewSetting, parameters);
	}

	// Malik
	public void displayprepObject(Element selectedElement, NodeSetting nodeSetting) {
		currentElement = selectedElement;
		currentNodeSetting = nodeSetting;
	}
	public void displayprepObject(Element selectedElement, NodeSetting nodeSetting, String type) {
		currentElement = selectedElement;
		currentNodeSetting = nodeSetting;
		currentElement.setAttribute("type", type);
	}

	public void displayBusinessObject(String title, String typ, Element selectedElement,
			ContentViewSetting previewSetting) {
		currentElement.setAttribute("type", typ);
		Map<SerializableQName, String> parameters = new HashMap<SerializableQName, String>();
		NamedNodeMap attributes = selectedElement.getAttributes();
		for (int k = 0; k < attributes.getLength(); k++) {
			Attr attr = (Attr) attributes.item(k);
			String variablename = attr.getName();
			parameters.put(new SerializableQName(variablename), attr.getValue().replace("#", "%23"));
		}
		setTitle(title, false);

		String uri = "dds://DOMAIN=resource/xqueries/" + typ + "/" + previewSetting.getXquery();
		setXProcFrame(uri, new SerializableSource(selectedElement.toString(), null, null), previewSetting, parameters);
	}

	public void clearContentViewPane() {
		ticketInfoWasCleared = true;
		displayData("<empty></empty>", getApplicationSettings().getContentViewSettings().get("empty"), "", true, null);
		ticketInfoWasCleared = false;
	}

	@Override
	public void handle(ApplicationEvent event) {
		super.handle(event);

		switch (event.getType()) {
		case ApplicationEvent.NODE_SELECTED_EVENT:
			if (type == TICKET_INFO_TYPE) {
				NodeSetting nodeSetting = ((NodeSelectedEvent) event).getNewSetting();
				// If this node has chain of custody info, display it
				// Otherwise, clear the entity info pane
				if (nodeSetting.isChainOfCustody()) {
					String contextType = nodeSetting.getId();
					DDSURI ddsUri = new DDSURI("/" + contextType + "/chainofcustody.xml");
					ddsUri.setAttribute(DDSURI.ATTRIBUTE_DATASET, "metadata");
					String currentUri = ddsUri.toString();
					String typ = "chainofcustody.xsd";
					ContentViewSetting previewSetting = nodeSetting.getContentViewSetting(getApplicationSettings(),
							typ);

					if (previewSetting == null) {
						Dialog.alert(Locale.getMessages().noContentViewSettingFound(typ),
								MainImageBundle.INSTANCE.error48().createImage());
					} else {
						String title = paneTitle + ": " + nodeSetting.getLabel();
						displayUri(currentUri, title, previewSetting, null);
					}
				} else {
					clearContentViewPane();
				}
			}
			break;
		}
	}

	private void logRequest(String appName, String searchID, Element fields, String currentUserName,
			String restrictions) {

		// build up the string we'll put in the audit log
		StringBuilder auditLogEntry = new StringBuilder();
		auditLogEntry.append("app : '");
		auditLogEntry.append(appName);
		auditLogEntry.append("', IRM_CODE : E10, user : ");
		auditLogEntry.append(currentUserName);
		auditLogEntry.append(", searchConfiguration : '");
		auditLogEntry.append(searchID);
		auditLogEntry.append("', fields : ");
		auditLogEntry.append(fields.toString());
		auditLogEntry.append(", role restrictions = '");
		auditLogEntry.append(restrictions);
		auditLogEntry.append("'");

		// String auditLogEntry = "app : '" + appName + "', IRM_CODE : E10, " +
		// " user : " +
		// currentUserName + ", searchConfiguration : '" + searchID + "', fields
		// : " + formInput +
		// ", role restrictions = '" + restrictions + "'";
		LogCenterServiceAsync logger = DDSServices.getLogCenterService();
		logger.log(auditLogEntry.toString(), loggerListener);
	}

	LogCenterFailureListener loggerListener = new LogCenterFailureListener();

}
