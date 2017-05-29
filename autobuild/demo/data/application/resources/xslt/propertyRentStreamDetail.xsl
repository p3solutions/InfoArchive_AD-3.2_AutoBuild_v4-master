<?xml version="1.0" encoding="UTF-8" ?> 
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:spry="http://www.dreamweaver.com/SpryFramework">
    <!--  XHTML output with XML syntax -->
    <xsl:output method="html"   encoding="utf-8" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
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
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
            <title>Rent Streams</title>
            <style type="text/css">
#apDiv1 {
	position:absolute;
	width:640px;
	height:130px;
	z-index:1;
}
</style>
            <script type="text/javascript" src="SpryAssets/SpryDataSet.js"> </script>
            <script src="SpryAssets/SpryData.js" type="text/javascript"> </script>
            <script src="SpryAssets/SpryHTMLDataSet.js" type="text/javascript"> </script>
            <script type="text/javascript">
var ds1 = new Spry.Data.HTMLDataSet(null, "rentStreamTable", {sortOnLoad: "col1", sortOrderOnLoad: "ascending"});
</script>
            <link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
            <link href="SpryAssets/SpryMasterDetail.css" rel="stylesheet" type="text/css" />
        </head>
        <body>
            <table width="640" border="1" id="rentStreamTable">
                <tr>
                    <td>date</td>
                    <td>amount</td>
                    <td>manAdjust</td>
                    <td>hold</td>
                    <!-- <td>used</td> -->
                    <td>marketRent</td>
                    <td>note</td>
                </tr>
                <xsl:for-each select="RENT_STREAM-ROW">
                    <tr>
                        <td><xsl:value-of select="lr_date"/></td>
                        <td><xsl:value-of select="lr_amount"/></td>
                        <td><xsl:value-of select="lr_man_adj"/></td>
                        <td><xsl:value-of select="lr_hold"/></td>
                        <!--  <td><xsl:value-of select="USED"/></td> -->
                        <td><xsl:value-of select="lr_mktrent"/></td>
                        <td><xsl:value-of select="lr_memo"/></td>
                    </tr>    
                </xsl:for-each>
            </table>
            <div class="MasterDetail">
                <div spry:region="ds1" class="MasterContainer">
                    <table width="640">
                        <tr>
                            <th align="left">Date</th><th align="left">Amount</th><th>Manual Adjust</th><th>Hold Changed</th><!-- <th>Used</th> -->
                        </tr>
                        <tr class="MasterColumn" spry:repeat="ds1" spry:setrow="ds1" spry:hover="MasterColumnHover" spry:select="MasterColumnSelected">				       
                            <td>{date}</td><td>{amount}</td><td>{manAdjust}</td><td>{hold}</td><!-- <td>{used}</td> -->
                        </tr>
                        
                    </table>
                </div>
                
                <div spry:detailregion="ds1" class="DetailContainer">
                    <table border="0" width="640">
                        <tr>
                            <td rowspan="2" align="left">Market Rent/Sq Ft:</td>
                            <td rowspan="2" align="left" class="DetailColumn">{marketRent}</td>
                                <td><input type="checkbox"   disabled="disabled" checked="checked" /></td>
                                <td>Continue to pay at previous rent rate until adjustment has been made</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox" disabled="disabled" checked="checked" /></td><td>Adjustment needed for deferred rent</td>
                        </tr>
                        <tr>
                            <td colspan="4" align="center">{note}</td>
                        </tr>
                    </table> 
                </div>
                <br style="clear:both" />
            </div>
        </body>
    </xsl:template>
</xsl:stylesheet>
