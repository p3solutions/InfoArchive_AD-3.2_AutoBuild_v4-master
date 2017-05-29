<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- XHTML output with XML syntax -->
	<xsl:output method="xml" encoding="utf-8" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>
	<xsl:param name="data-base-uri"/>
	<xsl:template match="/">
		<!-- always emit at least an empty HTML element -->
		<html>
			<!-- and look at the rest of the file -->
			<xsl:apply-templates/>
		</html>
	</xsl:template>
	<xsl:template match="results">
		<head>
			<style type="text/css">
				body {
				font-family : 'Lucida Sans Unicode', 'Lucida
				Grande', sans-serif;
				font-size : 12px;
				}
				.header {
				font-weight : bold;
				padding : 0px 30px 0px 10px;
				vertical-align : top;
				}
			</style>
			<title>Report Ticket Details</title>
			<link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css"/>
			<script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"/>
		</head>
		<body>
			<div id="CollapsiblePanel0" class="CollapsiblePanel">
				<div class="CollapsiblePanelTab" tabindex="0">Report Ticket Details
				</div>
				<div class="CollapsiblePanelContent">
					<table width="100%" border="1">
						<xsl:for-each select="result">
							<tr>
								<td>
									<b>Report Ticket ID</b>
								</td>
								<td>
									<xsl:value-of select="@id"/>
								</td>
							</tr>
							<tr>
								<td>
									<b>Submitted by</b>
								</td>
								<td>
									<xsl:value-of select="@username"/>
								</td>
							</tr>
							<tr>
								<td>
									<b>Name</b>
								</td>
								<td>
									<xsl:value-of select="@name"/>
								</td>
							</tr>
							<tr>
								<td>
									<b>Submitted Date</b>
								</td>
								<td>
									<xsl:value-of select="@datetime"/>
								</td>
							</tr>
							<tr>
								<td>
									<b>Current Status</b>
								</td>
								<td>
									<xsl:value-of select="@status"/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</div>
			</div>
			<table>
				<tr>
					<td> </td>
				</tr>
			</table>
			<div id="CollapsiblePanel1" class="CollapsiblePanel">
				<div class="CollapsiblePanelTab" tabindex="0">Search Inputs Used
				</div>
				<div class="CollapsiblePanelContent">
					<table width="100%" border="1">
						<thead>
							<tr align="center">
								<td>
									<b>Search Field</b>
								</td>
								<td>
									<b>Input provided</b>
								</td>
							</tr>
						</thead>
						<xsl:for-each select="searchFileds">
							<tr>
								<td>
									<xsl:value-of select="@key"/>
								</td>
								<td>
									<xsl:value-of select="@value"/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</div>
			</div>
			<table>
				<tr>
					<td> </td>
				</tr>
			</table>
			<xsl:if test="status/@status != 0">
				<div id="CollapsiblePanel2" class="CollapsiblePanel">
					<div class="CollapsiblePanelTab" tabindex="0">Ticket Processing
						details
					</div>
					<div class="CollapsiblePanelContent">
						<table width="100%" border="1">
							<xsl:for-each select="runStatus">
								<tr>
									<td>
										<xsl:value-of select="@key"/>
									</td>
									<td>
										<xsl:value-of select="@value"/>
									</td>
								</tr>
							</xsl:for-each>
							<xsl:for-each select="message">
								<tr>
									<td>
										Info
									</td>
									<td>
										<xsl:value-of select="@message"/>
									</td>
								</tr>
							</xsl:for-each>
							<xsl:for-each select="file">
								<tr>
									<td>
										Report file name
									</td>
									<td>
										<xsl:value-of select="@file"/>
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</div>
				</div>
			</xsl:if>
			<script type="text/javascript">
				var CollapsiblePanel0 = new
				Spry.Widget.CollapsiblePanel("CollapsiblePanel0");

				var
				CollapsiblePanel1 = new
				Spry.Widget.CollapsiblePanel("CollapsiblePanel1");

				var
				CollapsiblePanel2 = new
				Spry.Widget.CollapsiblePanel("CollapsiblePanel2");
			</script>
		</body>
	</xsl:template>
</xsl:stylesheet>