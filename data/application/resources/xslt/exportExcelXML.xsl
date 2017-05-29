<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" encoding="UTF-8"/>

    <xsl:strip-space elements="*"/>

    <xsl:template match="/*">
        <ss:Workbook xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
            <ss:Worksheet ss:Name="Sheet1">
                <ss:Table>
                    <!-- For each lineitem of result -->
                    <xsl:for-each select="*">
                        <!-- Insert additional row for header -->
                        <xsl:if test="position() = 1">
                            <ss:Row>
                                <xsl:for-each select="attribute::*">
                                    <xsl:if test="name() != 'type' and name() != 'uri'">
                                        <ss:Cell>
                                            <ss:Data ss:Type="String">
                                                <xsl:value-of select="name()"/>
                                            </ss:Data>
                                        </ss:Cell>
                                    </xsl:if>
                                </xsl:for-each>
                            </ss:Row>
                        </xsl:if>
                        <ss:Row>
                            <xsl:for-each select="attribute::*">
                                <xsl:if test="name() != 'type' and name() != 'uri'">
                                    <ss:Cell>
                                        <ss:Data ss:Type="String">
                                            <xsl:value-of select="."/>
                                        </ss:Data>
                                    </ss:Cell>
                                </xsl:if>
                            </xsl:for-each>
                        </ss:Row>
                    </xsl:for-each>

                </ss:Table>
            </ss:Worksheet>
        </ss:Workbook>
    </xsl:template>

</xsl:stylesheet>
