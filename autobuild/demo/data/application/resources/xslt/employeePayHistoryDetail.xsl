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
                <div class="CollapsiblePanelTab" tabindex="0">Employee Summary</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td>Employee Number</td>
                        	<td><xsl:value-of select="employee_summary/@empNumber"/></td>
                        	<td>Name</td>
                            <td><xsl:value-of select="employee_summary/@firstName"/>&#160;<xsl:value-of select="employee_summary/@lastName"/></td>
                        	<td>Position</td>                        	                        	
                            <td><xsl:value-of select="employee_summary/@posGroup"/></td>
                            <td>Title</td>                        	                        	
                            <td><xsl:value-of select="employee_summary/@groupTitle"/></td>
                            <td>Location</td>
                        	<td><xsl:value-of select="employee_summary/@location"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel2" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Pay History Record Details</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                            <th scope="col">Check Date</th>
                            <th scope="col">Check Number</th>
                            <th scope="col">Code</th>
                            <th scope="col">Hours</th>
                            <th scope="col">Amount</th>
                            <th scope="col">Date</th>
                            <th scope="col">Rate</th>
                            <th scope="col">City</th>
                            <th scope="col">State</th>
                        </tr>
                        <tr>
                            <td>
                              	<xsl:call-template name="FormatDate">
                                    <xsl:with-param name="DateTime" select="employee_summary/@checkDate"/>
                                </xsl:call-template>
							</td>
                            <td><xsl:value-of select="employee_summary/@checkNbr"/></td>
                            <td><xsl:value-of select="employee_summary/@code"/></td>
                            <td><xsl:value-of select="employee_summary/@hours"/></td>
                            <td><xsl:value-of select="employee_summary/@amount"/></td>
                            <td>
                            	<xsl:call-template name="FormatDate">
                                    <xsl:with-param name="DateTime" select="employee_summary/@date"/>
                                </xsl:call-template>
                            </td>
                            <td><xsl:value-of select="employee_summary/@rate"/></td>
                            <td><xsl:value-of select="employee_summary/@city"/></td>
                            <td><xsl:value-of select="employee_summary/@state"/></td>
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
    </xsl:template>    
</xsl:stylesheet>
