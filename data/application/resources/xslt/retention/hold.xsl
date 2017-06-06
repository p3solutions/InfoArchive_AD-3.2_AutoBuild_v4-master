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
			<title>Holds</title>
			<link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
			<script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"/>
		</head>
		<body>
			<div id="CollapsiblePanel0" class="CollapsiblePanel">
				<div class="CollapsiblePanelTab" tabindex="0">Holds Details</div>
				<div class="CollapsiblePanelContent">
					<table width="100%" border="1">
						<tr>
							<td>Name</td>
							<td>
								<xsl:value-of select="result/@Name"/>
							</td>

						</tr>
						<tr>
							<td>Authority</td>
							<td>
								<xsl:value-of select="result/@Authority"/>
							</td>

						</tr>
						<tr>
							<td>Description</td>
							<td>
								<xsl:value-of select="result/@Description"/>
							</td>

						</tr>
						<tr>
							<td>Status</td>
							<td>
								<xsl:value-of select="result/@Status"/>
							</td>

						</tr>
						<tr>
							<td>RecordCount</td>
							<td>
								<xsl:value-of select="result/@RecordCount"/>
							</td>

						</tr>
						<tr>
							<td>DateCreated</td>
							<td>
								<xsl:value-of select="result/@DateCreated"/>
							</td>

						</tr>
						<tr>
							<td>DateStart</td>
							<td>
								<xsl:value-of select="result/@DateStart"/>
							</td>

						</tr>
						<tr>
							<td>DateEnd</td>
							<td>
								<xsl:value-of select="result/@DateEnd"/>
							</td>

						</tr>


					</table>
				</div>
			</div>
			<script type="text/javascript">
				var CollapsiblePanel0 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel0");
			</script>
		</body>
	</xsl:template>
</xsl:stylesheet>
