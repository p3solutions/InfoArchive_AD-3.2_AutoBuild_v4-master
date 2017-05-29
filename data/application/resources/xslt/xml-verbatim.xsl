<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!-- XHTML output with XML syntax --><xsl:output method="xml" encoding="utf-8" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/><xsl:strip-space elements="*"/><xsl:template name="js-tree"><!--Old MSIE-only script--><!--<script>
        <xsl:comment>
          function f(e){ if (e.className=="ci"){if (e.children(0).innerText.indexOf("\n")>0) fix(e,"cb");} if (e.className=="di"){if (e.children(0).innerText.indexOf("\n")>0) fix(e,"db");} e.id=""; } function fix(e,cl){ e.className=cl; e.style.display="block"; j=e.parentElement.children(0); j.className="c"; k=j.children(0); k.style.visibility="visible"; k.href="#"; } function ch(e){ mark=e.children(0).children(0); if (mark.innerText=="+"){ mark.innerText="-"; for (var i=1;i!=e.children.length;i++) e.children(i).style.display="block"; } else if (mark.innerText=="-"){ mark.innerText="+"; for (var i=1;i!=e.children.length;i++) e.children(i).style.display="none"; }} function ch2(e){ mark=e.children(0).children(0); contents=e.children(1); if (mark.innerText=="+"){ mark.innerText="-"; if (contents.className=="db"||contents.className=="cb") contents.style.display="block"; else contents.style.display="inline"; } else if (mark.innerText=="-"){ mark.innerText="+"; contents.style.display="none"; }} function cl(){ e=window.event.srcElement; if (e.className!="c"){e=e.parentElement;if (e.className!="c"){return;}} e=e.parentElement; if (e.className=="e") ch(e); if (e.className=="k") ch2(e); } function ex(){} function h(){window.status=" ";} document.onclick=cl;
        </xsl:comment>
        </script>--><!--New MSIE-and-Mozilla script--><script><xsl:text>
        //<![CDATA[
        //MSIE
        function f(e) {
          if (e.className=="ci") {
            if (e.children(0).innerText.indexOf("\n") != 0)
              fix(e,"cb");
          }
          if (e.className=="di") {
            if (e.children(0).innerText.indexOf("\n") != 0)
              fix(e,"db");
          } e.id="";
        }
        
        function fix(e,cl) {
          e.className=cl;
          e.style.display="block";
          j=e.parentElement.children(0);
          j.className="c";
          k=j.children(0);
          k.style.visibility="visible";
          k.href="#";
        }
        
        function ch(e) {
          mark=e.children(0).children(0);
          if (mark.innerText=="+") {
            mark.innerText="-";
            for (var i=1;i != e.children.length;i++) {
              e.children(i).style.display="block";
            }
          }
          else if (mark.innerText=="-") {
            mark.innerText="+";
            for (var i=1;i != e.children.length;i++) {
              e.children(i).style.display="none";
            }
          }
        }
        
        function ch2(e) {
          mark=e.children(0).children(0);
          contents=e.children(1);
          if (mark.innerText=="+") {
            mark.innerText="-";
            if (contents.className=="db"||contents.className=="cb") {
              contents.style.display="block";
            }
            else {
              contents.style.display="inline";
            }
          }
          else if (mark.innerText=="-") {
            mark.innerText="+";
            contents.style.display="none";
          }
        } 
        
        function cl() {
          e=window.event.srcElement;
          if (e.className!="c") {
            e=e.parentElement;
            if (e.className!="c") {
              return;
            }
          }
          e=e.parentElement;
          if (e.className=="e") {
            ch(e);
          }
          if (e.className=="k") {
            ch2(e);
          }
        }

        //mozilla
        function moz_f() {
          clean=document.getElementsByName('clean');
          for(i=0; i != clean.length;i++) {
            e = clean[i];
            childNodes=e.childNodes
            if (childNodes.length != 0) {
              if (e.className=="ci") {
                if (childNodes[0].childNodes[0].nodeValue.indexOf("\n") != 0)
                  moz_fix(e,"cb");
              }
              if (e.className=="di") {
                if (childNodes[0].childNodes[0].nodeValue.indexOf("\n") != 0)
                  moz_fix(e,"db");
              }
            }
          }
        }

        function moz_firstNonText(e) {
          for (var i=0;i != e.childNodes.length;i++) {
            if(e.childNodes[i].nodeName != "#text")
              return i;
          }
          //nothing found
          return -1;
        }

        function moz_fix(e,cl) {
          e.className=cl;
          e.style.display="block";
          j=e.parentNode.childNodes[1];
          j.className="c";
          k=j.childNodes[0];
          k.style.visibility="visible";
          k.href="#";
        }
      
        function moz_ch(e) {
          markParentIndex = moz_firstNonText(e);
          markParent = e.childNodes[markParentIndex];
          markIndex = moz_firstNonText(markParent);
          mark = markParent.childNodes[markIndex];
          if (mark.childNodes[0].nodeValue=="+") {
            mark.childNodes[0].nodeValue="-";
            for (var i=markParentIndex+1;i != e.childNodes.length;i++) {
              if(e.childNodes[i].nodeName != "#text")
                e.childNodes[i].style.display="block";
            }
          }
          else if (mark.childNodes[0].nodeValue=="-") {
            mark.childNodes[0].nodeValue="+";
            for (var i=markParentIndex+1;i != e.childNodes.length;i++) {
              if(e.childNodes[i].nodeName != "#text")
                e.childNodes[i].style.display="none";
            }
          }
        }
      
        function moz_ch2(e) {
          mark = e.childNodes[1].childNodes[0];
          contents=e.childNodes[2];
          if (mark.childNodes[0].nodeValue=="+") {
            mark.childNodes[0].nodeValue="-";
            if (contents.className=="db"||contents.className=="cb") {
              contents.style.display="block";
            }
            else {
              contents.style.display="inline";
            }
          }
          else if (mark.childNodes[0].nodeValue=="-") {
            mark.childNodes[0].nodeValue="+";
            contents.style.display="none";
          }
        }

        function moz_cl(evnt) {
          e=evnt.target.parentNode;  
          if (e.className != "c") {
            e=e.parentNode;
            if (e.className!="c") {
              return;
            }
          }
          e=e.parentNode;
          if (e.className=="e") {
            moz_ch(e);
          }
          if (e.className=="k") {
            moz_ch2(e);
          }
        }

        function ex(){}
        function h(){window.status=" ";}
        if(document.all) {
          document.onclick=cl;
        } else if(document.getElementById) {
          document.onclick=moz_cl;
        }
        //]]></xsl:text></script></xsl:template><xsl:template match="/"><html><head><style>
          body {
            font:x-small 'Verdana';
            margin-right:1.5em
          }

          .c {
            cursor:pointer
          }

          .b {
            color:red;
            font-family:'Courier New';
            font-weight:bold;
            text-decoration:none
          }

          .e {
            margin-left:1em;
            text-indent:-1em;
            margin-right:1em
          }

          .k {
            margin-left:1em;
            text-indent:-1em;
            margin-right:1em
          }

          .t {
            color:#990000
          }

          .xt {
            color:#990099
          }

          .ns {
            color:red
          }

          .dt{
            color:green
          }

          .m {
            color:blue
          }

          .tx {
            font-weight:bold
          }

          .db{
            text-indent:0px;
            margin-left:1em;
            margin-top:0px;
            margin-bottom:0px;
            padding-left:.3em;
            border-left:1px solid #CCCCCC;
            font:small Courier
          }

          .di {
            font:small Courier
          }

          .d {
            color:blue
          }

          .pi {
            color:blue
          }

          .cb{
            text-indent:0px;
            margin-left:1em;
            margin-top:0px;
            margin-bottom:0px;
            padding-left:.3em;
            font:small Courier;
            color:#888888
          }

          .ci {
            font:small Courier;
            color:#888888
          }

        </style><xsl:call-template name="js-tree"/></head><body class="st"><xsl:apply-templates/></body></html></xsl:template><xsl:template match="*"><xsl:variable name="element-name" select="name(.)"/><div class="e"><div class="c"><A href="#" onclick="return false" onfocus="h()" class="b">-</A><span class="m">&lt;</span><span><xsl:attribute name="class">t</xsl:attribute><xsl:value-of select="name()"/></span><xsl:variable name="pns" select="../namespace::*"/><xsl:for-each select="namespace::*[name() != 'xml']"><xsl:if test="not($pns[name()=name(current()) and string()=string(current())]) "><SPAN class="ns">
              xmlns<xsl:if test="name() != ''">:</xsl:if><xsl:value-of select="name()"/></SPAN><SPAN class="m">="</SPAN><xsl:variable name="value"><xsl:value-of select="."/></xsl:variable><xsl:if test="$value != ''"><B class="ns"><xsl:value-of select="$value"/></B></xsl:if><SPAN class="m">"</SPAN></xsl:if></xsl:for-each><xsl:apply-templates select="@*"/><span class="m">></span></div><div><xsl:apply-templates/><div><span class="b"><xsl:text disable-output-escaping="yes"> </xsl:text></span><span class="m">&lt;/</span><span><xsl:attribute name="class">t</xsl:attribute><xsl:value-of select="name()"/></span><span class="m">></span></div></div></div></xsl:template><xsl:template match="@*"><xsl:text disable-output-escaping="yes"> </xsl:text><span><xsl:attribute name="class">t</xsl:attribute><xsl:value-of select="name()"/></span><span class="m">="</span><xsl:variable name="value"><xsl:value-of select="."/></xsl:variable><xsl:if test="$value != ''"><b><xsl:value-of select="."/></b></xsl:if><span class="m">"</span></xsl:template><xsl:template match="text()"><div class="e"><span class="b"><xsl:text disable-output-escaping="yes"> </xsl:text></span><span class="tx"><xsl:value-of select="."/></span></div></xsl:template><xsl:template match="comment()"><div class="k"><span><a class="b" onclick="return false" onfocus="h()" style="visibility:hidden">-</a><span class="m">&lt;!--</span></span><xsl:variable name="spanId" select="concat('clean_', generate-id(.))"/><span id="{$spanId}" class="ci" name="clean"><pre><xsl:value-of select="."/></pre></span><span class="m">--></span><script>
        if(document.all)
      f(<xsl:value-of select="$spanId"/>);
        else if(document.getElementById)
      moz_f(document.getElementById('<xsl:value-of select="$spanId"/>'));
      </script></div></xsl:template><xsl:template match="processing-instruction()"><xsl:if test="name() != 'xml-stylesheet'"><div class="e"><span class="b"><xsl:text/></span><span class="m">&lt;?</span><span class="pi"><xsl:value-of select="name()"/><xsl:text/><xsl:value-of select="."/></span><span class="m">?></span></div></xsl:if></xsl:template></xsl:stylesheet>