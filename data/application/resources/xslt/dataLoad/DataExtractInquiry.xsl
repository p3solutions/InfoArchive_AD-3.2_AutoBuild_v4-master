<?xml version="1.0" encoding="UTF-8" ?> 
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!--  XHTML output with XML syntax -->
	<xsl:output method="xml" encoding="utf-8" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>
	<xsl:param name="data-base-uri"/>
	<xsl:template match="/">
		<!--  always emit at least an empty HTML element -->
		<html>
			<!--  and look at the rest of the file -->
			<xsl:apply-templates/>
		</html>
	</xsl:template>
	<xsl:template match="results">
		<head>
			<style type="text/css">
				body {
					font-family : 'Lucida Sans Unicode', 'Lucida Grande', sans-serif;
					font-size : 12px;
				}
				.header {
					font-weight : bold;
					padding : 0px 30px 0px 10px;
					vertical-align : top;
					}</style>
			<title>Data Load</title>
			<link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
			<script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"/>
		</head>
		<body>
			<div id="CollapsiblePanel0" class="CollapsiblePanel">
				<div class="CollapsiblePanelTab" tabindex="0">Extraction Details</div>
				<div class="CollapsiblePanelContent">
					<table width="100%" border="1">
						<tr>
							<td>
								<b>Table Name</b>
							</td>
							<td>
								<xsl:value-of select="@tablename"/>
							</td>

						</tr>
						<tr>
							<td>
								<b>Date of Import</b>
							</td>
							<td>
								<xsl:value-of select="@date"/>
							</td>
						</tr>
						<tr>
							<td>
								<b>Total Record Count</b>
							</td>
							<td>
								<xsl:value-of select="@totalcount"/>
							</td>
						</tr>

					</table>
				</div>
			</div>

			<table>
				<tr>
					<td>&#160;</td>
				</tr>
			</table>   
			<div id="CollapsiblePanel1" class="CollapsiblePanel">
				<div class="CollapsiblePanelTab" tabindex="0">Extracted File Details</div>
				<div class="CollapsiblePanelContent">
					<table width="100%" border="1">

						<tr>
							<td>
								<b>File Name</b>
							</td>
							<td>
								<b>Count</b>
							</td>
						</tr>
						<xsl:for-each select="result">
							<tr>
								<td>
									<xsl:value-of select="@FILE"/>
								</td>
								<td>
									<xsl:value-of select="@ROW_COUNT"/>
								</td>
							</tr>
						</xsl:for-each>

					</table>
				</div>
			</div>
			<table>
				<tr>
					<td>&#160;</td>
				</tr>
			</table>     
			<script type="text/javascript">
				var CollapsiblePanel0 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel0");
				var CollapsiblePanel1 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel1");
			</script>
		</body>
	</xsl:template>
</xsl:stylesheet>
