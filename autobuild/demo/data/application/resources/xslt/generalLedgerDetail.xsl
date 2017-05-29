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
            <title>General Ledger</title>
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
                            <td><xsl:value-of select="reconciler_info/reconciler_summary/@recid"/></td>
                            <td>CO-PD/FY:&#160;</td>
                            <td>SV-<xsl:value-of select="reconciler_info/reconciler_summary/@fperiod"/>/<xsl:value-of select="reconciler_info/reconciler_summary/@fyear"/></td>
                            <td>Region/Banner:</td>
                            <td>HO&#160;</td>
                        </tr>
                        <tr>
                            <td>Rec Description:</td>
                            <td><xsl:value-of select="reconciler_info/reconciler_summary/@description"/></td>
                            <td>Reconciler:</td>
                            <td><xsl:value-of select="reconciler_info/reconciler_summary/@reconciler"/></td>
                            <td>Reconciler <br />Department</td>
                            <td><xsl:value-of select="reconciler_info/reconciler_summary/@reconcilerDept"/></td>
                            <td>&#160;</td>
                        </tr>
                        <tr>
                            <td>Posting Accounts:</td>
                            <td>
							<select>
								<xsl:for-each select="reconciler_info/ACCOUNT-ROW">
									<option value='1' ><xsl:value-of select="POSTING_ACCT_ID" /></option>
								</xsl:for-each>
							</select>
                            </td>
                            <td>Status:</td>
                            <td><xsl:value-of select="reconciler_info/reconciler_summary/@status"/></td>
                            <td>GAAP Bal <br/>CheckTotal:</td>
                            <td><xsl:value-of select="reconciler_info/reconciler_summary/@gaapCheckTotal"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel2" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Reconciling Items:</div>
                <div class="CollapsiblePanelContent">
                    <table width="640" border="1">
                        <tr>
                            <th scope="col">Period/Fiscal Year</th>
                            <th scope="col">Detailed Description</th>
                            <th scope="col">Posting Account</th>
                            <th scope="col">Amount</th>
                            <th scope="col">Action Taken</th>
                            <th scope="col">Offset</th>
                        </tr>
                        <xsl:for-each select="reconciler_info/ACCOUNT-ROW">
                            <tr>
                                <td><xsl:value-of select="FISCAL_PERIOD"/>/<xsl:value-of select="FISCAL_YEAR"/></td>
                                <td><xsl:value-of select="ACCT_DESCRIPTION"/></td>
                                <td><xsl:value-of select="POSTING_ACCT_ID"/></td>
                            </tr>
                        </xsl:for-each>
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
