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
            <title>Rent Options</title>
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
                    <td>type</td>
                    <td>noticeWindowBegins</td>
                    <td>noticeDate</td>
                    <td>start</td>
                    <td>end</td>
                    <td>note</td>
                    <td>salesAmount</td>
                    <td>exRights</td>
                </tr>
                <xsl:for-each select="LEASE_OPTION-ROW">
                    <xsl:text>
                        
                    </xsl:text>
                    
                    <tr>
                        <td><xsl:value-of select="lp_type"/></td>
                        <td><xsl:value-of select="lp_ex_strt"/></td>
                        <td><xsl:value-of select="lp_ex_end"/></td>
                        <td><xsl:value-of select="lp_op_strt"/></td>
                        <td><xsl:value-of select="lp_op_end"/></td>
                        <td><xsl:value-of select="lp_memo"/></td>
                        <td><xsl:value-of select="lp_kickamt"/></td>
                        <td><xsl:value-of select="lp_kickrts"/></td>
                    </tr>    
                </xsl:for-each>
            </table>
            <div class="MasterDetail">
                <div spry:region="ds1" class="MasterContainer">
                    <table width="640">
                        <tr>
                            <th align="left">Type</th><th align="left">Notice Window Begins</th><th>Notice Date</th><th>Start</th><th>End</th>
                        </tr>
                        <tr class="MasterColumn" spry:repeat="ds1" spry:setrow="ds1" spry:hover="MasterColumnHover" spry:select="MasterColumnSelected">				       
                            <td>{type}</td>
                            <td>{noticeWindowBegins}</td>
                            <td>{noticeDate}</td>
                            <td>{start}</td>
                            <td>{end}</td>
                        </tr>
                        
                    </table>
                </div>
                
                <div spry:detailregion="ds1" class="DetailContainer">
                    <table border="0" width="640">
                      <!--  <tr>
                            <td rowspan="2" align="left">Market Rent/Sq Ft:</td>
                            <td rowspan="2" align="left" class="DetailColumn">{marketRent}</td>
                                <td><input type="checkbox"   disabled="disabled" checked="checked" /></td>
                                <td>Continue to pay at previous rent rate until adjustment has been made</td>
                        </tr>
                        
                      -->
                        <xsl:variable name="type">{type}</xsl:variable>
                        <!-- enable the appropriate radio button based on the type value --> 
                        <tr>
                            <td><div spry:choose="spry:choose">
                                <div class="DetailColumn" spry:when="'{$type}' == '1'"> <!-- Renew -->
                                        <input type="checkbox" disabled="disabled" checked="checked"/>
                                    </div>    
                                    <div spry:default="spry:default">
                                        <input type="checkbox" disabled="disabled"/>
                                    </div>
                            </div>
                            </td>
                            <td>Renewal</td>
                        </tr>
                        
                        <tr>
                            <td><div spry:choose="spry:choose">
                                <div class="DetailColumn" spry:when="'{$type}' == '2'"> <!-- Space Expansion -->
                                    <input type="checkbox" disabled="disabled" checked="checked"/>
                                </div>    
                                <div spry:default="spry:default">
                                    <input type="checkbox" disabled="disabled"/>
                                </div>
                            </div>
                            </td>
                            <td>Space Expansion</td>
                        </tr>
                        <tr>
                            <td><div spry:choose="spry:choose">
                                <div class="DetailColumn" spry:when="'{$type}' == '3'"> <!-- Purchase -->
                                    <input type="checkbox" disabled="disabled" checked="checked"/>
                                </div>    
                                <div spry:default="spry:default">
                                    <input type="checkbox" disabled="disabled"/>
                                </div>
                            </div>
                            </td>
                            <td>Purchase</td>
                        </tr>
                        <tr>
                            <td><div spry:choose="spry:choose">
                                <div class="DetailColumn" spry:when="'{$type}' == '4'"> <!-- Cancellation -->
                                    <input type="checkbox" disabled="disabled" checked="checked"/>
                                </div>    
                                <div spry:default="spry:default">
                                    <input type="checkbox" disabled="disabled"/>
                                </div>
                            </div></td>    
                            <td>Cancellation</td>
                        </tr>
                        <tr>
                            <td><div spry:choose="spry:choose">
                                <div class="DetailColumn" spry:when="'{$type}' == '5'"> <!-- Kickout Provision -->
                                    <input type="checkbox" disabled="disabled" checked="checked"/>
                                </div>    
                                <div spry:default="spry:default">
                                    <input type="checkbox" disabled="disabled"/>
                                </div>
                            </div>
                            </td>
                            
                            <td>Kickout Provision</td>
                            <td rowspan="3">
                                <table border="1"  >
                                    <thead>
                                        <tr><th colspan="2">Kickout Provisions</th></tr>
                                    </thead>
                                    <tbody>
                                        <tr class="DetailColumn"><td>Sales Amount:</td><td>&#160;{salesAmount}</td></tr>
                                        <tr class="DetailColumn" ><td>Exercise Rights:</td><td>&#160;{exRights}</td></tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td><div spry:choose="spry:choose">
                                <div class="DetailColumn" spry:when="'{$type}' == '6'"> <!-- Go Dark Provision -->
                                    <input type="checkbox" disabled="disabled" checked="checked"/>
                                </div>    
                                <div spry:default="spry:default">
                                    <input type="checkbox" disabled="disabled"/>
                                </div>
                            </div>
                            </td>
                            
                            <td>Go Dark Provision</td>
                        </tr>
                        <tr>
                            <td><div spry:choose="spry:choose">
                                <div class="DetailColumn" spry:when="'{$type}' == '7'"> <!-- Miscellaneous -->
                                    <input type="checkbox" disabled="disabled" checked="checked"/>
                                </div>    
                                <div spry:default="spry:default">
                                    <input type="checkbox" disabled="disabled"/>
                                </div>
                            </div>
                            </td>
                            
                            <td>Miscellaneous</td>
                        </tr>
                        <tr>
                            <td  colspan="3" class="DetailColumn">{note}</td>
                        </tr>
                    </table> 
                </div>
                <br style="clear:both" />
            </div>
        </body>
    </xsl:template>
</xsl:stylesheet>
