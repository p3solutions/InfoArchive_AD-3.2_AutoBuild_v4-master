<?xml version="1.0" encoding="UTF-8"?>

<p:pipeline name="main" xmlns:p="http://www.w3.org/ns/xproc"
            xmlns:fo="http://www.w3.org/1999/XSL/Format"
            xmlns:dds="http://www.emc.com/documentum/xml/dds"
            version="1.0">

  <p:input port="stylesheet"/>

  <p:xslt name="xslt">
    <p:input port="stylesheet">
      <p:pipe step="main" port="stylesheet"/>
    </p:input>
  </p:xslt>
  
  <p:make-absolute-uris match="fo:external-graphic/@src"/>

  <p:viewport match="fo:external-graphic">
    <p:string-replace match="/fo:external-graphic/@src" replace="concat('url(&quot;', ., '&quot;)')"/>
  </p:viewport>

  <p:unwrap match="dds:wrapper"/>
  
  <!-- FOP does not like tables where the declared number of table columns
       does not match the actual number of row cells in the table -->
  <p:delete match="fo:table-column"/>

  <p:xsl-formatter name="formatter" content-type="application/pdf" href="transient:output.pdf"/>
  
  <p:identity>
    <p:input port="source">
      <p:pipe step="formatter" port="result"/>
    </p:input>
  </p:identity>
  
</p:pipeline>