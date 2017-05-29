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
			<title>Audit Details</title>
			<link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
			<script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"/>
		</head>
		<body>
			<div id="CollapsiblePanel0" class="CollapsiblePanel">
				<div class="CollapsiblePanelTab" tabindex="0">Audit Details</div>
				<div class="CollapsiblePanelContent">
					<table width="100%" border="0">
						<tr>
							<td>Time</td>
							<td>
								<xsl:value-of select="result/time" />
							</td>
						</tr>
						<tr>
							<td>User</td>
							<td>
								<xsl:value-of select="result/user" />
							</td>
						</tr>
					</table>
				</div>
			</div>
			<br/>
			<div id="CollapsiblePanel1" class="CollapsiblePanel">
				<div class="CollapsiblePanelTab" tabindex="0">Additional Information
				</div>
				<div class="CollapsiblePanelContent">
					<table width="100%" border="0">
						<thead>
						<tr>
							<td>Criteria</td>
							<td>Value</td>
							</tr>
						</thead>
						<xsl:for-each select="result/data/*">
							<tr>
								<td>
									<xsl:value-of select="name(.)" />
								</td>
								<td>
									<xsl:value-of select="text()" />
								</td>
							</tr>
						</xsl:for-each>
						<tr>
						<td colspan="2"><hr/></td></tr>
					</table>
				</div>
			</div>
			<script type="text/javascript">
				var CollapsiblePanel0 = new
				Spry.Widget.CollapsiblePanel("CollapsiblePanel0");
				var CollapsiblePanel1 = new
				Spry.Widget.CollapsiblePanel("CollapsiblePanel1");
			</script>
		</body>
	</xsl:template>
</xsl:stylesheet>