<?xml version="1.0" encoding="UTF-8"?>
<p:pipeline xmlns:p="http://www.w3.org/ns/xproc"
            xmlns:c="http://www.w3.org/ns/xproc-step"
            xmlns:x="http://www.emc.com/documentum/xml/xproc"
            name="main"
            xmlns:dds="http://www.emc.com/documentum/xml/dds"
            version="1.0">

  <p:input port="query"/>
  <p:input port="stylesheet"/>

  <p:serialization port="result" method="xhtml"
                   doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                   doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" omit-xml-declaration="false"/>
 
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

</p:pipeline>
