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
            <link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
			<script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"/>
            
            
        </head>
        <body>
            <div id="CollapsiblePanel0" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Player Details</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                            <td>Name</td>
                            <td><xsl:value-of select="player_summary/@nameLast"/>,&#160;<xsl:value-of select="player_summary/@nameGiven"/></td>
                            <td>Birthday</td>
                            <td><xsl:value-of select="player_summary/@birthMonth"/>/<xsl:value-of select="player_summary/@birthDay"/>/<xsl:value-of select="player_summary/@birthYear"/></td>
                            <td>Throws</td>
                            <td><xsl:value-of select="player_summary/@bats"/></td>
                            <td>Bats</td>
                            <td><xsl:value-of select="player_summary/@throws"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel1" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Salary Statistics</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                       	    <td>Year</td>
                            <td>Salary</td>
                        </tr>
                        <xsl:variable name="temp1">
                            <xsl:for-each select="salary_results/BASEBALL_SALARIES-ROW">
                                <tr>
                                    <td><xsl:value-of select="YEARID"/></td>
                                    <td>$<xsl:value-of select="SALARY"/></td>
                                </tr>
                            </xsl:for-each>
                        </xsl:variable>
                        <xsl:choose>
	                    <xsl:when test="$temp1=''">
				<tr>
                        	    <td>No records found</td>
                        	</tr>
			    </xsl:when>
			    <xsl:otherwise>
				<xsl:copy-of select="$temp1"/>
		            </xsl:otherwise>
			</xsl:choose>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel2" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Batting Statistics</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                       	    <td>Year</td>
                            <td>Home Runs</td>
                        </tr>
                        <xsl:variable name="temp2">
                            <xsl:for-each select="batting_results/BASEBALL_BATTING-ROW">
                                <tr>
                                    <td><xsl:value-of select="YEARID"/></td>
                                    <td><xsl:value-of select="HR"/></td>
                                </tr>
                            </xsl:for-each>
                        </xsl:variable>
                        <xsl:choose>
	                    <xsl:when test="$temp2=''">
				<tr>
                        	    <td>No records found</td>
                        	</tr>
			    </xsl:when>
			    <xsl:otherwise>
				<xsl:copy-of select="$temp2"/>
		            </xsl:otherwise>
			</xsl:choose>
                    </table>
                </div>
            </div>
            <script type="text/javascript">
var CollapsiblePanel0 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel0");
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
        <xsl:if test="$mo != ''">
            <xsl:value-of select="'/'"/>
	        <xsl:value-of select="$day"/>
	        <xsl:value-of select="'/'"/>
	        <xsl:value-of select="$year"/>
        </xsl:if>
    </xsl:template>    
</xsl:stylesheet>
