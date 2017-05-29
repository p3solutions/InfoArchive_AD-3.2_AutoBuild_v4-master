<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <!-- always emit at least an empty HTML element -->
        <html>
            <xsl:apply-templates />
        </html>
    </xsl:template>

    <!-- Generate the HTML head element content -->
    <xsl:template name="makeHead">
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
                }
                th {
                    text-align : left;
                }
            </style>
            <title>
                <!-- Title template must be provided by including stylesheet -->
                <xsl:call-template name="Title" />
            </title>
            <script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript">
                // For some (unknown) reason, there needs to be content in this element or the
                // header won't be rendered correctly by the Spry Javascript
            </script>
            <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js">
                // For some (unknown) reason, there needs to be content in this element or the
                // header won't be rendered correctly by the Spry Javascript
            </script>
            <script>
                $(document).ready(function(){

                    $("#mainFilter").change(function() {
                        var toDisplay = $("#mainFilter option:selected").text();
                        var panelID = "CollapsiblePanel" + $("#mainFilter option:selected").attr("value");
                        if (toDisplay == "All") {
                            showAll();
                        } else {
                            hideAllExcept(panelID);
                        }
                    });

                    buildOrderedList();

                });

                function hideAllExcept (show) {
                    hideAll();

                    $("#CollapsiblePanelHeader").show();
                    $("#" + show).show();
                    <!-- show sub-panels -->
                    $("#" + show).find('.CollapsiblePanel').show();
                }

                function hideAll () {
                    $(".CollapsiblePanel").each(function() {
                        $(this).hide();
                    });
                }

                function showAll () {
                    $(".CollapsiblePanel").each(function() {
                        $(this).show();
                    });
                }

                function buildOrderedList () {
                    var docs = new Array();
                    var index = 0;
                    $(".reqDoc").each(function() {
                        if ($.inArray($(this).text(), docs) == -1) {
                          docs[index] = $(this).text();
                         index++;
                       }
                    });

                    docs.sort();
                }

            </script>
            <link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
        </head>
    </xsl:template>

    <!-- output a collapsible panel - required params: name of panel (for id; no spaces), header text, and content -->
    <xsl:template name="MakeCollapsiblePanel">
        <xsl:param name="panelName" />
        <xsl:param name="panelHeaderText" />
        <xsl:param name="panelContent" />


        <xsl:variable name="panelID" select="concat('CollapsiblePanel', $panelName)" />
        <xsl:variable name="panelVar" select="concat('collapsiblePanel', $panelName)" />
        <div id="{$panelID}" class="CollapsiblePanel">
            <div class="CollapsiblePanelTab" tabindex="0">
                <xsl:copy-of select="$panelHeaderText" />
            </div>
            <div class="CollapsiblePanelContent">
                <xsl:copy-of select="$panelContent" />
            </div>
        </div>
        <script type="text/javascript">
            var <xsl:value-of select="$panelVar" /> = new Spry.Widget.CollapsiblePanel("<xsl:value-of select="$panelID" />");
        </script>
    </xsl:template>

</xsl:stylesheet>
