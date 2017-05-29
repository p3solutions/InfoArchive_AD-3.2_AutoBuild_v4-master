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
            <title>Details</title>
            <script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"></script>
            <link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
            
        </head>
        <body>
             <div id="CollapsiblePanel1" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Header Information</div>
                <div class="CollapsiblePanelContent">
                    <table width="640" border="0">
                        <tr>
                            <td>Rec ID:</td>
                            <td><xsl:value-of select="reconciliations/RECONCILIATION-ROW/REC_ID"/></td>
                            <td>CO-PD/FY:&#160;</td>
                            <td>SV-<xsl:value-of select="reconciliations/RECONCILIATION-ROW/FPERIOD"/>/<xsl:value-of select="reconciliation/RECONCILIATION-ROW/FYEAR"/></td>
                            <td>Region/Banner:</td>
                            <td>HO&#160;</td>
                        </tr>
                        <tr>
                            <td>Rec Description:</td>
                            <td><xsl:value-of select="reconciliations/RECONCILIATION-ROW/RECDESC"/></td>
                            <td>Reconciler:</td>
                            <td><xsl:value-of select="reconciliations/RECONCILIATION-ROW/RECONCILER"/></td>
                            <td>Reconciler <br />Department</td>
                            <!-- <td>&#160;</td> -->
                            <td><xsl:value-of select="reconciliations/RECONCILIATION-ROW/RECONCILINGDEPT"/></td>                        </tr>
                        <tr>
                            <td>Posting Accounts:</td>
                            <td>
								<select>
									<xsl:for-each select="reconciliation_accounts/RECONCILIATION_ACCOUNT-ROW">
										<option value='1' ><xsl:value-of select="POSTING_ACCT_ID" /></option>
									</xsl:for-each>
								</select>
							</td>
                            <td>Status:</td>
                            <td><xsl:value-of select="reconciliations/RECONCILIATION-ROW/STATUS"/></td>
                            <td>GAAP Bal: <br/>CheckTotal:</td>
                            <td><xsl:value-of select="reconciliations/RECONCILIATION-ROW/GAAP_BALANCE_CHECK"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel2" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Reconciling Items:</div>
                <div class="CollapsiblePanelContent">
                    <table width="640" border="0" cellspacing="0">
                        <tr>
                        	<td valign="top" width="128"><b>Account Purpose</b></td>
							<td><xsl:value-of select="reconciliations/RECONCILIATION-ROW/ACCT_PURPOSE"/></td>
                        </tr>
                        <tr>
                        	<td valign="top" width="128"><b>Detailed Description</b></td>
							<td><xsl:value-of select="reconciliations/RECONCILIATION-ROW/DETAILED_DESCRIPTION"/></td>
                        </tr>
                        <tr>
                        	<td valign="top" width="128"><b>Notes</b></td>
							<td><xsl:value-of select="reconciliations/RECONCILIATION-ROW/NOTES"/></td>
                        </tr>                        
                    </table>
                </div>
            </div>
            <script type="text/javascript">
var CollapsiblePanel1 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel1");
var CollapsiblePanel2 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel2");
</script>
        </body>
    </xsl:template>
</xsl:stylesheet>
