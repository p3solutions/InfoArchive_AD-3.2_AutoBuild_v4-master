<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:redirect="http://xml.apache.org/xalan/redirect"
                              extension-element-prefixes="redirect">
                              
  <xsl:output
	  method="xml"
	  omit-xml-declaration="yes"
	  doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	  doctype-system="../dtd/xhtml1-transitional.dtd"	                  
	  indent="yes"/>
                              
  <xsl:param name="export-dir"/>                  
  <xsl:param name="configurationId"/> 
  
  <xsl:template match="/">

    <xsl:for-each select=".//operationConfiguration">
  
       		<xsl:message>in .//operationConfiguration, configuration id = <xsl:value-of select="$configurationId"/></xsl:message>
      		<xsl:message>configuration id = <xsl:value-of select="@id"/></xsl:message>
    
      <xsl:if test="string-length($configurationId) = 0 or @id = $configurationId"> 
	      <xsl:variable name="err-file-name">e:\code\err\xxx.xhtml</xsl:variable>
	      <xsl:variable name="file-name"><xsl:value-of select="translate($export-dir, '\', '/')"/>/<xsl:value-of select="./xformName"/>.xhtml</xsl:variable>            
		            
	      <redirect:write file="{$file-name}">  
	        <xhtml xmlns="http://www.w3.org/1999/xhtml">
	         	<form method="post" accept-charset="utf-8" action="">
	            <xsl:attribute name="id">
	              <xsl:value-of select="@id"/> 
	            </xsl:attribute>    	  
	            <xsl:attribute name="name">
	              <xsl:value-of select="@id"/> 
	            </xsl:attribute>    	  
	         	  <table>
       	        <xsl:for-each select=".//operationField">
                  <tr>
         	          <td><xsl:value-of select="label"/>:</td>
                    <xsl:call-template name="field"/>
                  </tr>
                </xsl:for-each>
	         	  </table>
          	  <input type="submit" value="Submit" />
	          </form>
	        </xhtml>
	      </redirect:write>
	     </xsl:if> 
    </xsl:for-each>
  </xsl:template>  

  <xsl:template name="field">
    <xsl:param name="node" select="."/>
    <xsl:choose>
      <xsl:when test="$node/options[@type='select']">
        <td>
          <select>
            <xsl:attribute name="name">
              <xsl:value-of select="$node/name"/> 
            </xsl:attribute>	         	              
            <xsl:for-each select="$node/options/option">
               <option>
   	              <xsl:attribute name="value">
    	              <xsl:value-of select="name"/> 
  	              </xsl:attribute>	        
                  <xsl:value-of select="label"/> 	              
               </option>
            </xsl:for-each>
          </select>
        </td>                         
      </xsl:when>
      <xsl:when test="$node/options[@type='radio']">
        <td>
          <xsl:for-each select="$node/options/option">
             <input>
 	              <xsl:attribute name="id">
  	              <xsl:value-of select="name"/> 
	              </xsl:attribute>	        
 	              <xsl:attribute name="name">
  	              <xsl:value-of select="../../name"/> 
	              </xsl:attribute>	        
 	              <xsl:attribute name="type">radio</xsl:attribute>
 	              <xsl:attribute name="value">
  	              <xsl:value-of select="name"/> 
	              </xsl:attribute>	        
             </input>
             <label>
 	              <xsl:attribute name="for">
  	              <xsl:value-of select="name"/> 
	              </xsl:attribute>	        
                <xsl:value-of select="label"/> 	              
             </label>
          </xsl:for-each>
        </td>                         
      </xsl:when>
      <xsl:otherwise>
        <td>
          <input name="title" value="" type="text">
            <xsl:attribute name="name">
              <xsl:value-of select="$node/name"/> 
            </xsl:attribute>	         	              
            <xsl:if test="$node/suggestionsindex">
              <xsl:attribute name="class">suggestions-<xsl:value-of select="$node/suggestionsindex"/>---<xsl:value-of select="$node/indexPath"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="$node/type"> 
              <xsl:attribute name="type">
                <xsl:value-of select="$node/type"/> 
              </xsl:attribute>         	              
           </xsl:if>
           <xsl:if test="$node/required ='true'">                              
              <xsl:attribute name="class">required</xsl:attribute>         	              
           </xsl:if>
          </input>
        </td>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
      
</xsl:stylesheet>





