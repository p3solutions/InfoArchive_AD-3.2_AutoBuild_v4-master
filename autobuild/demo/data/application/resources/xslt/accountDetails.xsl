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
            <title>Account Details</title>
            <script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"></script>
            <link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
            
        </head>
        <body>
            <div id="CollapsiblePanel1" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Account Details</div>
                <div class="CollapsiblePanelContent">
                    <table width="640" border="0">
                        <tr>
                            <td>Post ID:</td>
                            <td><xsl:value-of select="account_info/account_summary/@postid"/></td>
                            <td>CO-PD/FY:&#160;</td>
                            <td>SV-<xsl:value-of select="account_info/account_summary/@fperiod"/>/<xsl:value-of select="account_info/account_summary/@fyear"/></td>
                            <td>PFTC:&#160;</td>
                            <td><xsl:value-of select="account_info/account_summary/@pftc"/></td>
                        </tr>
                        <tr>
                            <td>Description:</td>
                            <td><xsl:value-of select="account_info/account_summary/@desc"/></td>
                            <td>Assigned Preparer:</td>
                            <td><xsl:value-of select="account_info/account_summary/@preparer"/></td>
                            <td>Status:</td>
                            <td><xsl:value-of select="account_info/account_summary/@status"/></td>
                        </tr>
                        <tr>
                            <td>Center Description:</td>
                            <td><xsl:value-of select="account_info/account_summary/@center"/></td>
                            <td>Rec Dept Risk:</td>
                            <td><xsl:value-of select="account_info/account_summary/@rec_dept_risk"/></td>
                            <td>HO Risk:</td>
                            <td><xsl:value-of select="account_info/account_summary/@horisk"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel2" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Reconciling Items:</div>
                <div class="CollapsiblePanelContent">
                    <table width="640" border="1">
                        <tr>
                            <th scope="col">Record ID</th>
                            <th scope="col">Fiscal Period/Year</th>
                            <th scope="col">Record Description</th>
                            <th scope="col">Reconciler</th>
                            <th scope="col">Status</th>
                            <th scope="col">GL GAAP Balance</th>
                        </tr>
                        <xsl:for-each select="account_info/RECONCILIATION-ROW">
                            <tr>
                                <td><xsl:value-of select="REC_ID"/></td>
                                <td><xsl:value-of select="FPERIOD"/>/<xsl:value-of select="FYEAR"/></td>
                                <td><xsl:value-of select="RECDESC"/></td>
                                <td><xsl:value-of select="RECONCILER"/></td>
                                <td><xsl:value-of select="STATUS"/></td>
                                <td><xsl:value-of select="GL_GAAP_BALANCE"/></td>
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
