<?xml version="1.0"?>
<p:declare-step type="ext:rtf2html"
				xmlns:p="http://www.w3.org/ns/xproc"
				xmlns:ext="http://flatironssolutions.com/decomm/xproc"
				version="1.0">
				
	<p:input port="source" sequence="true"/>
	<p:output port="result" sequence="true"/>
	<p:option name="stdout" select="'true'"/>
	<p:option name="stderr" select="'false'"/>
	
	<p:option name="convertType" select="'null'"/>
	<p:option name="imgreftype" select="'null'"/>
	<p:option name="imgroot" select="'null'"/>

</p:declare-step>