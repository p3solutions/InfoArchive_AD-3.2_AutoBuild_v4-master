<?xml version="1.0" encoding="UTF-8"?>

<p:pipeline name="main" xmlns:p="http://www.w3.org/ns/xproc" xmlns:x="http://www.emc.com/documentum/xml/xproc" version="1.0">

  <p:input port="stylesheet"/>

  <p:option name="binary-data-mode" select="'false'"/>

  <p:serialization port="result" method="xhtml"
                   doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                   doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>
  
  <!--  -->

  <x:get-base-uri name="pipeline-base-uri" step-base-uri="true"/>

  <x:get-base-uri name="source-base-uri">
    <p:input port="source">
      <p:pipe step="main" port="source"/>
    </p:input>
  </x:get-base-uri>
  
  <x:get-base-uri name="stylesheet-base-uri">
    <p:input port="source">
      <p:pipe step="main" port="stylesheet"/>
    </p:input>
  </x:get-base-uri>

  <p:parameters name="parameters">
    <p:input port="parameters">
      <p:pipe step="main" port="parameters"/>
    </p:input>
    <p:with-param name="pipeline-base-uri" select="//text()" port="parameters">
      <p:pipe step="pipeline-base-uri" port="result"/>
    </p:with-param>
    <p:with-param name="data-base-uri" select="//text()" port="parameters">
      <p:pipe step="source-base-uri" port="result"/>
    </p:with-param>
    <p:with-param name="stylesheet-base-uri" select="//text()" port="parameters">
      <p:pipe step="stylesheet-base-uri" port="result"/>
    </p:with-param>
  </p:parameters>
  
  <!-- -->

  <p:choose>
    <p:when test="$binary-data-mode = 'true'">
      <!-- fetching binary data -->
      <p:make-absolute-uris match="*">
        <p:input port="source">
          <p:pipe step="main" port="source"/>
        </p:input>
        <p:with-option name="base-uri" select="//text()">
          <p:pipe step="source-base-uri" port="result"/>
        </p:with-option>
      </p:make-absolute-uris>
      <x:fetch name="fetch-data">
        <p:with-option name="href" select="//text()"/>
      </x:fetch>
      <!-- x:fetch has no primary output port -->
      <p:identity>
        <p:input port="source">
          <p:pipe step="fetch-data" port="result"/>
        </p:input>
      </p:identity>
    </p:when>

    <p:otherwise>
      <!-- XSL transformation -->
      <p:xslt name="xslt">
        <p:input port="source">
          <p:pipe step="main" port="source"/>
        </p:input>
        <p:input port="parameters">
          <p:pipe step="parameters" port="result"/>
        </p:input>
        <p:input port="stylesheet">
          <p:pipe step="main" port="stylesheet"/>
        </p:input>
      </p:xslt>
    </p:otherwise>
  </p:choose>

</p:pipeline>