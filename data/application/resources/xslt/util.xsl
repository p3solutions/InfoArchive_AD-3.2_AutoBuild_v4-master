<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:decimal-format name="euro" decimal-separator="." grouping-separator=","/>
    <!--  XHTML output with XML syntax -->
    <xsl:output method="xml" encoding="utf-8" indent="no"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>
    <xsl:param name="data-base-uri"/>
    <!-- This will replace all CRLF, CR, and LF 'new line' character sequences with html br elements -->
    <xsl:template name="markup-newlines">
        <xsl:param name="content"/>
        <!-- Pass 1: Replace all CRLF with LF -->
        <xsl:variable name="pass1">
            <xsl:call-template name="replace">
                <xsl:with-param name="content" select="$content"/>
                <xsl:with-param name="target" select="'&#xD;&#xA;'"/>
                <xsl:with-param name="replacement" select="'&#xA;'"/>
            </xsl:call-template>
        </xsl:variable>
        <!-- Pass 2: Replace all CR with LF-->
        <xsl:variable name="pass2">
            <xsl:call-template name="replace-with-br">
                <xsl:with-param name="content" select="$pass1"/>
                <xsl:with-param name="target" select="'&#xD;'"/>
                <xsl:with-param name="replacement" select="'&#xA;'"/>
            </xsl:call-template>
        </xsl:variable>
        <!-- Pass 3: Replace all LF with html br elements -->
        <xsl:call-template name="replace-with-br">
            <xsl:with-param name="content" select="$pass2"/>
            <xsl:with-param name="target" select="'&#xA;'"/>
        </xsl:call-template>
    </xsl:template>
    <!-- Replaces the target substring with the replacement string -->
    <xsl:template name="replace">
        <xsl:param name="content"/>
        <xsl:param name="target"/>
        <xsl:param name="replacement"/>
        <xsl:choose>
            <xsl:when test="contains($content, $target)">
                <xsl:variable name="left" select="substring-before($content, $target)"/>
                <xsl:variable name="right" select="substring-after($content, $target)"/>
                <xsl:variable name="left-length" select="string-length($left)"/>
                <xsl:variable name="right-length" select="string-length($right)"/>
                <xsl:value-of select="$left"/>
                <xsl:value-of select="$replacement"/>
                <xsl:if test="$right">
                    <xsl:call-template name="replace">
                        <xsl:with-param name="content" select="$right"/>
                        <xsl:with-param name="target" select="$target"/>
                        <xsl:with-param name="replacement" select="$replacement"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$content"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- Replaces the target substring with empty br elements -->
    <xsl:template name="replace-with-br">
        <xsl:param name="content"/>
        <xsl:param name="target"/>
        <!--<xsl:variable name="target-length" select="string-length($target)"/>
            <xsl:variable name="has-target" select="boolean($target)"/>-->
        <xsl:choose>
            <xsl:when test="contains($content, $target)">
                <xsl:variable name="left" select="substring-before($content, $target)"/>
                <xsl:variable name="right" select="substring-after($content, $target)"/>
                <xsl:variable name="left-length" select="string-length($left)"/>
                <xsl:variable name="right-length" select="string-length($right)"/>
                <xsl:choose>
                    <xsl:when test="$left">
                        <xsl:value-of select="$left"/>
                    </xsl:when>
                    <!-- Output a placeholder space so the FO's block for p doesn't collapse -->
                    <xsl:otherwise>
                        <xsl:text> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:element name="br" namespace="http://www.w3.org/1999/xhtml"/>
                <xsl:if test="$right">
                    <xsl:call-template name="replace-with-br">
                        <xsl:with-param name="content" select="$right"/>
                        <xsl:with-param name="target" select="$target"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$content"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="PDFHeader"> Patient: <xsl:value-of select="@displayName"/><br/> Provider:
        <xsl:value-of select="CV3CLIENTVISIT-ROW/PROVIDERDISPLAYNAME"/><br/> DOB: <xsl:value-of
            select="@birthDate"/><br/> Visit: <xsl:value-of select="CV3CLIENTVISIT-ROW/IDCODE"/>/
        <xsl:value-of select="CV3CLIENTVISIT-ROW/VISITIDCODE"/></xsl:template>
    <xsl:template name="html-head">
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
            <title>
                <xsl:call-template name="PDFHeader"/>
            </title>
            <script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript">
        	// For some (unknown) reason, there needs to be content in this element or the
        	// header won't be rendered correctly by the Spry Javascript
        </script>
            <link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css"/>
        </head>
    </xsl:template>
    <xsl:template name="Header">
        <xsl:param name="report-name"/>
        <div id="CollapsiblePanel000" class="CollapsiblePanel">
            <div class="CollapsiblePanelTab" tabindex="0">Header</div>
            <div class="CollapsiblePanelContent">
                <table width="100%" border="0">
                    <tr>
                        <td>
                            <b>Mountain University - <xsl:value-of select="@location"/></b>
                        </td>
                    </tr>
                    <tr>
                        <td>4747 Table Mesa Drive, Boulder, Co. 80305</td>
                    </tr>
                    <tr>
                        <td> </td>
                    </tr>
                    <tr>
                        <td>Patient: <b><xsl:value-of select="@displayName"/></b></td>
                    </tr>
                    <tr>
                        <td>Provider: <b><xsl:value-of
                            select="CV3CLIENTVISIT-ROW/PROVIDERDISPLAYNAME"/></b></td>
                    </tr>
                    <tr>
                        <td> </td>
                    </tr>
                    <tr>
                        <td>
                            <b>
                                <xsl:value-of select="CV3CLIENTVISIT-ROW/CURRENTLOCATION"/>
                            </b>
                        </td>
                    </tr>
                    <tr>
                        <td>DOB: <b><xsl:value-of select="@birthDate"/></b></td>
                    </tr>
                    <tr>
                        <td>
                            <b><xsl:value-of select="CV3CLIENTVISIT-ROW/IDCODE"/>/ <xsl:value-of
                                select="CV3CLIENTVISIT-ROW/VISITIDCODE"/></b>
                        </td>
                    </tr>
                    <tr>
                        <td> </td>
                    </tr>
                    <tr>
                        <td>
                            <b><xsl:value-of select="@reportType"/> - <xsl:value-of
                                select="$report-name"/> Report</b>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <script type="text/javascript">
		var collapsiblePanel000 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel000");
	</script>
    </xsl:template>
    <xsl:template name="FormatNumber">
        <xsl:param name = "number"/>

        <xsl:variable name = "result">
        <xsl:value-of select="format-number($number,'###.##', 'euro')"/>
        </xsl:variable>
        <xsl:value-of select="$result"/>

    </xsl:template>
    
    
    <xsl:template name="FormatDate">
        <xsl:param name="DateTime"/>
        <xsl:param name="mode"/>
        <!-- convert from format 2006-01-14T08:55:22.00000 -->
        <!-- To format 01/14/2006 08:55:22 -->
        <xsl:variable name="year">
            <xsl:value-of select="substring($DateTime,1,4)"/>
        </xsl:variable>
        <xsl:variable name="month">
            <xsl:value-of select="substring($DateTime,6,2)"/>
        </xsl:variable>
        <xsl:variable name="day">
            <xsl:value-of select="substring($DateTime,9,2)"/>
        </xsl:variable>
        <xsl:variable name="hour">
            <xsl:value-of select="substring($DateTime,12,2)"/>
        </xsl:variable>
        <xsl:variable name="minute">
            <xsl:value-of select="substring($DateTime,15,2)"/>
        </xsl:variable>
        <xsl:if test="$month != ''">
            <xsl:if test="$mode = 'DateTime'">
                <xsl:value-of select="concat($month,'/',$day,'/',$year,' ',$hour,':',$minute)"/>
            </xsl:if>
            <xsl:if test="$mode='Date'">
                <xsl:value-of select="concat($month,'/',$day,'/',$year)"/>
            </xsl:if>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>