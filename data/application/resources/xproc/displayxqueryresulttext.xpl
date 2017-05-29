<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
            xmlns:c="http://www.w3.org/ns/xproc-step"
            xmlns:x="http://www.emc.com/documentum/xml/xproc"
            name="main"
            xmlns:dds="http://www.emc.com/documentum/xml/dds"
            version="1.0">

  <p:input port="source"/>
  <p:input port="query"/>
  <p:input port="stylesheet"/>
  <p:input port="parameters" kind="parameter"/>

  
 
  <p:add-attribute name="add-attr" match="dds:expression"
                   attribute-name="c:content-type" attribute-value="application/xquery">
    <p:input port="source" select="//dds:expression">
      <p:pipe step="main" port="query"/>
    </p:input>
  </p:add-attribute>

  <p:xquery>
    <p:input port="source">
      <p:pipe step="main" port="source"/>
    </p:input>
    <p:input port="query">
      <p:pipe step="add-attr" port="result"/>
    </p:input>
  </p:xquery>

  <p:xslt>
    <p:input port="stylesheet">
      <p:pipe step="main" port="stylesheet"/>
    </p:input>
    <p:input port="parameters">
      <p:empty/>
    </p:input>
  </p:xslt>

  <p:store href="transient:output.csv" method="text"/> 

</p:declare-step>
