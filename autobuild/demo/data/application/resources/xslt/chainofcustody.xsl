<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!-- XHTML output with XML syntax --><xsl:output method="xml" encoding="utf-8" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>

<xsl:output method="html" indent="yes"/>

<xsl:template match="dataset">
  <html>
    <head>
      <title><xsl:value-of select="name"/></title>
      <script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"></script>
      <link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
      <style type="text/css">
        body { font-family:'Lucida Sans Unicode', 'Lucida Grande', sans-serif; font-size:12px; }
        tr > td:first-child { font-weight: bold; padding: 0px 30px 0px 10px; vertical-align: top; }
      </style>
    </head>
    <body>
      <table>
        <tr>
          <td>Data Set:</td>
          <td><xsl:value-of select="name"/></td>
        </tr>
      </table>
      <div id="CollapsiblePanel0" class="CollapsiblePanel">
        <div class="CollapsiblePanelTab" tabindex="0">Table Information (Count: <xsl:value-of select="count(/dataset/table)"/>)</div>
        <div class="CollapsiblePanelContent">
          <table>
	        <xsl:variable name="temp">
	          <xsl:for-each select="/dataset/table">
	            <tr>
	              <td><xsl:value-of select="@name"/></td>
	              <td><xsl:value-of select="@validation"/></td>
	            </tr>
	           </xsl:for-each>
	         </xsl:variable>
	         <xsl:choose>
	           <xsl:when test="$temp=''">
	             <tr><td colwidth="2">No tables listed</td></tr>
	           </xsl:when>
	           <xsl:otherwise>
	             <xsl:copy-of select="$temp"/>
	           </xsl:otherwise>
	         </xsl:choose>
          </table>
        </div>
      </div>
            <script type="text/javascript">
var CollapsiblePanel0 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel0");
</script>      
    </body>
  </html>
</xsl:template>

</xsl:stylesheet>