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
                            <td>Process Date:</td>
                            <td><xsl:value-of select="CHECK-ROW/PROCESS_DATE"/></td>
                            <td>Batch Number:&#160;</td>
                            <td><xsl:value-of select="CHECK-ROW/BATCH_NUMBER"/></td>
                            <td>Lead Ticket Type:</td>
                            <td><xsl:value-of select="CHECK-ROW/LEAD_TICKET_TYPE"/></td>
                        </tr>
                        <tr>
                            <td>Transaction ID:</td>
                            <td><xsl:value-of select="CHECK-ROW/TRANSACTION_ID"/></td>
                            <td>Card Acct Number:&#160;</td>
                            <td><xsl:value-of select="CHECK-ROW/CARD_ACCT_NUMBER"/></td>
                            <td>Routing Transit:</td>
                            <td><xsl:value-of select="CHECK-ROW/ROUTING_TRANSIT"/></td>
                        </tr>
                        <tr>
                            <td>Account Number:</td>
                            <td><xsl:value-of select="CHECK-ROW/ACCT_NUMBER"/></td>
                            <td>Check Amount:&#160;</td>
                            <td><xsl:value-of select="CHECK-ROW/CHECK_AMOUNT"/></td>
                            <td>Bank Code:</td>
                            <td><xsl:value-of select="CHECK-ROW/BANK_CODE"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel2" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Check Image:</div>
                <div class="CollapsiblePanelContent">
                    <table width="640" border="0" cellspacing="0">
                        <tr>
                            <td valign="top" width="128"><b>Image</b></td>
                            <td>
                                FileName: <xsl:value-of select="CHECK-ROW/FILE_NAME"/>
                                <img>
                                    <xsl:attribute name="src">BlobServlet?uri=dds://DOMAIN=data;DATASET=changeme/CHECKS/<xsl:value-of select="CHECK-ROW/FILE_NAME"/></xsl:attribute>
                                </img>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" width="128"><b>Download</b></td>
                            <td>
                                <a>
                                    <xsl:attribute name="href">BlobServlet?uri=dds://DOMAIN=data;DATASET=changeme/CHECKS/<xsl:value-of select="CHECK-ROW/FILE_NAME"/></xsl:attribute>check
                                </a>
                            </td>
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
