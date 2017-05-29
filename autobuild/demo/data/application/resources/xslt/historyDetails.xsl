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
            <title>History</title>
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
                            <td><xsl:value-of select="reconciler_summary/@recid"/></td>
                            <td>CO-PD/FY:&#160;</td>
                            <td>SV-<xsl:value-of select="reconciler_summary/@fperiod"/>/<xsl:value-of select="reconciler_summary/@fyear"/></td>
                            <td>Region/Banner:</td>
                            <td>HO&#160;</td>
                        </tr>
                        <tr>
                            <td>Rec Description:</td>
                            <td><xsl:value-of select="reconciler_summary/@description"/></td>
                            <td>Reconciler:</td>
                            <td><xsl:value-of select="reconciler_summary/@reconciler"/></td>
                            <td>Reconciler <br />Department</td>
                            <!-- <td>&#160;</td> -->
                            <td><xsl:value-of select="reconciler_summary/@reconcilerDept"/></td>
                        </tr>
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
                            <td><xsl:value-of select="reconciler_summary/@status"/></td>
                            <td>GAAP Bal <br/>CheckTotal:</td>
                            <td><xsl:value-of select="reconciler_summary/@gaapCheckTotal"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel2" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Reconciling Items:</div>
                <div class="CollapsiblePanelContent">
                    <table width="640" border="1">
                        <tr>
                            <th scope="col">Action</th>
                            <th scope="col">Comment</th>
                            <th scope="col">User</th>
                            <th scope="col">Date/Time</th>
                        </tr>
                        <xsl:for-each select="reconciliation_history/RECONCILIATION_LOG-ROW">
                            <tr>
                                <td><xsl:value-of select="ACTIONTYPEDESC"/></td>
                                <td><xsl:value-of select="COMMENTS"/></td>
                                <td><xsl:value-of select="REVIEWERNAME"/></td>
                                <td>
                                    <xsl:call-template name="FormatDate">
                                        <xsl:with-param name="DateTime" select="LOGDATE"/>
                                    </xsl:call-template>
                                    
                                </td>
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
    <xsl:template name="FormatDate">
        <xsl:param name="DateTime" />
        <!-- convert from format 2006-01-14T08:55:22.00000 -->
        <!-- To format 01/14/2006 08:55:22 -->
        <xsl:variable name="year">
            <xsl:value-of select="substring($DateTime,1,4)" />
        </xsl:variable>
        <xsl:variable name="mo-temp">
            <xsl:value-of select="substring-after($DateTime,'-')" />
        </xsl:variable>
        <xsl:variable name="mo">
            <xsl:value-of select="substring-before($mo-temp,'-')" />
        </xsl:variable>
        <xsl:variable name="day-temp">
            <xsl:value-of select="substring-after($mo-temp,'-')" />
        </xsl:variable>
        <xsl:variable name="day">
            <xsl:value-of select="substring($day-temp,1,2)" />
        </xsl:variable>
        <xsl:variable name="time">
            <xsl:value-of select="substring-after($mo-temp,'T')" />
        </xsl:variable>
        <xsl:variable name="hh">
            <xsl:value-of select="substring($time,1,2)" />
        </xsl:variable>
        <xsl:variable name="mm">
            <xsl:value-of select="substring($time,4,2)" />
        </xsl:variable>
        <xsl:variable name="ss">
            <xsl:value-of select="substring($time,7,2)" />
        </xsl:variable>
        <xsl:value-of select="$mo"/>
        <xsl:value-of select="'/'"/>
        <xsl:value-of select="$day"/>
        <xsl:value-of select="'/'"/>
        <xsl:value-of select="$year"/>
        <xsl:value-of select="' '"/>
        <xsl:value-of select="$hh"/>
        <xsl:value-of select="':'"/>
        <xsl:value-of select="$mm"/>
        <xsl:value-of select="':'"/>
        <xsl:value-of select="$ss"/>
    </xsl:template>
</xsl:stylesheet>
