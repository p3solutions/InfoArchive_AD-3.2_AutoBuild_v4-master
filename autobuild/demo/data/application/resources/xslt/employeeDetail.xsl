<?xml version="1.0" encoding="UTF-8" ?> 
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--  XHTML output with XML syntax -->
    <xsl:output method="xml" encoding="utf-8" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>
    <xsl:param name="data-base-uri"/>
    <xsl:template match="/">
        <!--  always emit at least an empty HTML element -->
        <html>
            <!--  and look at the rest of the file -->
            <xsl:apply-templates/>
        </html>
    </xsl:template>
    <xsl:template match="results">
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
					}</style>
            <title>History</title>
            <link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
			<script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"/>
            
            
        </head>
        <body>
            <div id="CollapsiblePanel0" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Employee Detail</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td>SSN</td>
                        	<td><xsl:value-of select="employee_summary/@ssNumber"/></td>
                        	<td>Employee Number</td>
                        	<td><xsl:value-of select="payhist_header/PAY_HISTHDR-ROW/EMPNO"/></td>
                        	<td>Name</td>
                        	<td><xsl:value-of select="employee_summary/@lastName"/>,&#160;<xsl:value-of select="employee_summary/@firstName"/>&#160;<xsl:value-of select="employee_summary/@middleName"/></td>
                        	<td>Position</td>                        	                        	
                        	<td><xsl:value-of select="payhist_header/PAY_HISTHDR-ROW/POS_GROUP"/></td>
                        	<td>Location</td>
                        	<td><xsl:value-of select="payhist_header//PAY_HISTHDR-ROW/LOCATION_CODE"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel1" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Personal Information</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td>Associate #</td>
                        	<td></td> <!-- TODO: Associate # is missing from data feed -->
                        	<td>Birth Date</td>
                        	<td>
                            	<xsl:call-template name="FormatDate">
                                	<xsl:with-param name="DateTime" select="employee_summary/@birthDate"/>
                            	</xsl:call-template>
                            </td>
                        	<td>Ethnic Background</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/ETHNIC_BACKGROUND"/></td>
                        </tr>
                        <tr>
                        	<td>Marital Status</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/MARITAL_STATUS"/></td>                        	                        	
                        	<td>Gender</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/GENDER"/></td>
                        	<td>Rehireable</td>
                        	<td></td> <!-- TODO: Rehireable is missing from data feed -->
                        </tr>
                        <tr>
                        	<td>Home Address</td> <!-- expand the following to include all lines of the address -->
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/LINE1"/>,&#160;<xsl:value-of select="employee_header/EMPDETAILHDR-ROW/CITY"/>,&#160;<xsl:value-of select="employee_header/EMPDETAILHDR-ROW/STATE"/>&#160;<xsl:value-of select="employee_header/EMPDETAILHDR-ROW/ZIP"/></td>
                        	<td>Home Phone</td>
                        	<td></td> <!-- TODO: Home Phone is missing from data feed -->                      
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel2" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Employment Information</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <!-- TODO: field is missing here that shows "Inactive Associate" on screen shot -->
                        <tr>
                        	<td>Hire Date</td>
                        	<td>
                            	<xsl:call-template name="FormatDate">
                                	<xsl:with-param name="DateTime" select="employee_header/EMPDETAILHDR-ROW/HIRE_DATE"/>
                            	</xsl:call-template>
                            </td>
                        	<td>Associate Type</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/ASSOCIATE_TYPE"/></td>
                        	<td>Current Status</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/CURRENT_STATUS"/></td>
                        </tr>
                        <tr>
                        	<td>Rehire Date</td>
                        	<td>
                            	<xsl:call-template name="FormatDate">
                                	<xsl:with-param name="DateTime" select="employee_header/EMPDETAILHDR-ROW/REHIRE_DATE"/>
                            	</xsl:call-template>
                            </td>                        	                        	
                        	<td>Associate Type Date</td>
                        	<td>
                        		<xsl:call-template name="FormatDate">
                                	<xsl:with-param name="DateTime" select="employee_header/EMPDETAILHDR-ROW/ASSOCIATE_TYPE_DATE"/>
                            	</xsl:call-template>
                            </td>
                        	<td>United Way Code</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/UNITED_WAY_CODE"/></td>
                        </tr>
                        <tr>
                        	<td>Termination Date</td>
                        	<td>
                            	<xsl:call-template name="FormatDate">
                                	<xsl:with-param name="DateTime" select="employee_header/EMPDETAILHDR-ROW/TERMINATION_DATE"/>
                            	</xsl:call-template>
                            </td>
                        	<td>Tax Entity</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/TAX_ENTITY"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel3" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Primary Position Information</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td>Title</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/TITLE"/></td>
                        	<td>Location</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/LOCATION"/></td>
                        	<td>Position</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/POSITION"/></td>
                        </tr>
                        <tr>
                        	<td>Pay Grade</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/PAY_GRADE"/></td>                        	                        	
                        	<td>Pay Rate</td>
                        	<td><xsl:value-of select="mostrecent_payhist/PAY_HISTDTL-ROW/RATE"/></td>
                        	<td>Increase Date</td>
                        	<td></td> <!-- TODO: Increase Date is missing from data feed, or needs to be determined somehow -->
                        </tr>
                        <tr>
                        	<td>Last Check Date</td>
                        	<td>
                            	<xsl:call-template name="FormatDate">
                                	<xsl:with-param name="DateTime" select="employee_header/EMPDETAILHDR-ROW/LASTCHECKDATE"/>
                            	</xsl:call-template>
                            </td>
                        	<td>Position Tenure</td>
                        	<td>
                        		<xsl:call-template name="FormatDate">
                                	<xsl:with-param name="DateTime" select="employee_header/EMPDETAILHDR-ROW/POSITION_TENURE"/>
                            	</xsl:call-template>
                        	</td>
                        	<td>Location Tenure</td>
                        	<td>
                            	<xsl:call-template name="FormatDate">
                                	<xsl:with-param name="DateTime" select="employee_header/EMPDETAILHDR-ROW/LOCATION_TENURE"/>
                            	</xsl:call-template>
                            </td>
                        </tr>
                        <tr>
                        	<td>Avg Weekly Hours</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/AVG_WEEKLY_HOURS"/></td>
                        	<td>Avg Monthly Wage</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/AVG_MONTHLY_WAGE"/></td>
                        	<td>Union Local</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/UNION_LOCAL"/></td>
                        </tr>
                        <tr>
                        	<td>Payroll Class</td>                        	                        	
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/PAYROLL_CLASS"/></td>
                        	<td>Contract/Sub</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/CONTRACT_SUB"/></td>
                        	<td>Normal Hours</td>
                        	<td><xsl:value-of select="employee_header/EMPDETAILHDR-ROW/NORMAL_HOURS"/></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel4" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Additional Position Information</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td>Title</td>
                        	<td>Location</td>
                        	<td>Position</td>
                        	<td>Rate</td>
                        	<td>Normal Hours</td>
                        	<td>Last Check Date</td>
                        </tr>
                        <tr>
                        	<xsl:variable name="temp">
                        		<xsl:for-each select="service_history/EMPDETAILADDPOSINFO-ROW">
                                		<td><xsl:value-of select="TITLE"/></td>
                                		<td><xsl:value-of select="LOCATION"/></td>
                                		<td><xsl:value-of select="POSITION"/></td>
                                		<td><xsl:value-of select="RATE"/></td>
                                		<td><xsl:value-of select="NORMAL_HOURS"/></td>
                                		<td><xsl:value-of select="LAST_CHECK_DATE"/></td>
                        		</xsl:for-each>
                        	</xsl:variable>
                        	<xsl:choose>
								<xsl:when test="$temp=''">
                        			<td>No records found</td>
								</xsl:when>
								<xsl:otherwise>
									<xsl:copy-of select="$temp"/>
								</xsl:otherwise>
							</xsl:choose>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel5" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Service History</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td>Months of Service</td>
                        	<td></td> <!-- TODO: Months of Service is missing from the data feed or needs to be calculated somehow -->
                        	<td>Seniority Date</td>
                        	<td></td> <!-- TODO: Seniority Date is missing from the data feed or needs to be calculated somehow -->
                        </tr>
                        <tr>
                        	<td>Date</td>
                        	<td>Status</td>
                        	<td>Break Code</td>
                        	<td>Description</td>                        	                        	
                        	<td>Exp RetDt</td>
                        	<td>Comments</td>
                        </tr>
                        <tr>
                        	<xsl:variable name="temp">
                        		<xsl:for-each select="service_history/EMPDETAIL_SERVICEHIST-ROW">
                               		<td>
                           				<xsl:call-template name="FormatDate">
                               				<xsl:with-param name="DateTime" select="employee_header/EMPDETAILHDR-ROW/PERS_STATUS_DATE"/>
                           				</xsl:call-template>
                           			</td>
                               		<td><xsl:value-of select="STATUS"/></td>
                               		<td><xsl:value-of select="BREAK_CODE"/></td>
                               		<td><xsl:value-of select="DETAIL_DESCRIPTION"/></td>
                               		<td><xsl:value-of select="EXP_RETDT"/></td>
                               		<td></td> <!-- TODO: Comments is missing from the data feed -->
                        		</xsl:for-each>
                        	</xsl:variable>
                        	<xsl:choose>
								<xsl:when test="$temp=''">
                        			<td>No records found</td>
								</xsl:when>
								<xsl:otherwise>
									<xsl:copy-of select="$temp"/>
								</xsl:otherwise>
							</xsl:choose>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel6" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Employment History</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td>System Date</td>
                        	<td>Effective Date</td>
                        	<td>Employment Type</td>
                        	<td>Employ Type Date</td>
                        	<td>Hire Date</td>
                        	<td>Termination Date</td>
                        	<td>Rehire Date</td>
                        	<td>Pers Status</td>                        	                        	
                        	<td>Pers Status Date</td>
                        	<td>Pers Status Reason</td>
                        </tr>
                        <tr>
                        	<xsl:variable name="temp">
                        		<xsl:for-each select="employment_history/EMPDETAILEMPHIST-ROW">
                               		<td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="SYS_DATE"/></xsl:call-template></td>
                               		<td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="EFF_DATE"/></xsl:call-template></td>
                           		 	<td></td> <!-- TBD -->
                               		<td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="EMPLOY_TYPE_DATE"/></xsl:call-template></td>
                               		<td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="HIRE_DATE"/></xsl:call-template></td>
                               		<td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="TERMINATION_DATE"/></xsl:call-template></td>
                               		<td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="REHIRE_DATE"/></xsl:call-template></td>
                               		<td><xsl:value-of select="PERS_STATUS"/></td>
                               		<td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="PERS_STATUS_DATE"/></xsl:call-template></td>
                               		<td><xsl:value-of select="PERS_STATUS_RSN"/></td>
                        		</xsl:for-each>
                        	</xsl:variable>
                        	<xsl:choose>
								<xsl:when test="$temp=''">
                        			<td>No records found</td>
								</xsl:when>
								<xsl:otherwise>
									<xsl:copy-of select="$temp"/>
								</xsl:otherwise>
							</xsl:choose>
                        </tr>
                    </table>
                </div>
            </div>                      	
            <div id="CollapsiblePanel7" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Federal Taxes</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td></td>
                        	<td>Marital</td>
                        	<td>Status</td>
                        	<td>Exemptions</td>
                        	<td>Exempt Amount</td>
                        	<td>Extra</td>
                        	<td>QTD Wages</td>
                        	<td>QTD Tax</td>                        	                        	
                        	<td>YTD Wages</td>
                        	<td>YTD Tax</td>
                        </tr>
                        <tr>
                        	<xsl:variable name="temp">
                        		<xsl:for-each select="federal_taxes/EMPDETAIL_FEDTAXES-ROW[(TAX_ID='FEDERAL' or TAX_ID='MEDICARE' or TAX_ID='OASDI') and
                        	                                                       (TAX_CODE='FEDERAL' or TAX_CODE='EMPE')]">
                               		<td><xsl:value-of select="TAX_ID"/></td>
                               		<td><xsl:value-of select="MARITAL_STATUS"/></td>
                               		<td><xsl:value-of select="TAX_STATUS"/></td>
                               		<td><xsl:value-of select="EXEMPTIONS"/></td>
                               		<td><xsl:value-of select="EXEMPT_AMOUNT"/></td>
                               		<td><xsl:value-of select="EXTRA_AMOUNT"/></td>
                               		<td><xsl:value-of select="QTD_TOTAL_WAGES"/></td> <!-- TODO: should this be QTD_TAXABLE_WAGES? -->
                               		<td><xsl:value-of select="QTD_TAX_AMOUNT"/></td>
                               		<td><xsl:value-of select="YTD_TOTAL_WAGES"/></td>  <!-- TODO: should this be YTD_TAXABLE_WAGES? -->
                               		<td><xsl:value-of select="YTD_TAX_AMOUNT"/></td>
                        		</xsl:for-each>
                        	</xsl:variable>
                        	<xsl:choose>
								<xsl:when test="$temp=''">
                        			<td>No records found</td>
								</xsl:when>
								<xsl:otherwise>
									<xsl:copy-of select="$temp"/>
								</xsl:otherwise>
							</xsl:choose>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel8" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">State Taxes</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td>State</td>
                        	<td>Marital</td>
                        	<td>Status</td>
                        	<td>Exemptions</td>
                        	<td>Extra</td>
                        	<td>QTD Wages</td>
                        	<td>QTD Tax</td>                        	                        	
                        	<td>YTD Wages</td>
                        	<td>YTD Tax</td>
                        </tr>
                        <tr>
                        	<xsl:variable name="temp">
                        		<xsl:for-each select="federal_taxes/EMPDETAIL_FEDTAXES-ROW[TAX_ID='STATE']">
                               		<td><xsl:value-of select="TAX_CODE"/></td>
                               		<td><xsl:value-of select="MARITAL_STATUS"/></td>
                               		<td><xsl:value-of select="TAX_STATUS"/></td>
                               		<td><xsl:value-of select="EXEMPTIONS"/></td>
                               		<td><xsl:value-of select="EXTRA_AMOUNT"/></td>
                               		<td><xsl:value-of select="QTD_TOTAL_WAGES"/></td> <!-- TODO: should this be QTD_TAXABLE_WAGES? -->
                               		<td><xsl:value-of select="QTD_TAX_AMOUNT"/></td>
                              		<td><xsl:value-of select="YTD_TOTAL_WAGES"/></td>  <!-- TODO: should this be YTD_TAXABLE_WAGES? -->
                               		<td><xsl:value-of select="YTD_TAX_AMOUNT"/></td>
                        		</xsl:for-each>
                        	</xsl:variable>
                        	<xsl:choose>
								<xsl:when test="$temp=''">
                        			<td>No records found</td>
								</xsl:when>
								<xsl:otherwise>
									<xsl:copy-of select="$temp"/>
								</xsl:otherwise>
							</xsl:choose>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="CollapsiblePanel9" class="CollapsiblePanel">
                <div class="CollapsiblePanelTab" tabindex="0">Local Taxes</div>
                <div class="CollapsiblePanelContent">
                    <table width="100%" border="1">
                        <tr>
                        	<td>Local</td>
                        	<td>Marital</td>
                        	<td>Status</td>
                        	<td>Exemptions</td>
                        	<td>Extra</td>
                        	<td>QTD Wages</td>
                        	<td>QTD Tax</td>                        	                        	
                        	<td>YTD Wages</td>
                        	<td>YTD Tax</td>
                        </tr>
                        <tr>
                        	<xsl:variable name="temp">
                        		<xsl:for-each select="federal_taxes/EMPDETAIL_FEDTAXES-ROW[TAX_ID='LOCAL']">
                               		<td><xsl:value-of select="TAX_CODE"/></td>
                               		<td><xsl:value-of select="MARITAL_STATUS"/></td>
                               		<td><xsl:value-of select="TAX_STATUS"/></td>
                               		<td><xsl:value-of select="EXEMPTIONS"/></td>
                               		<td><xsl:value-of select="EXTRA_AMOUNT"/></td>
                               		<td><xsl:value-of select="QTD_TOTAL_WAGES"/></td> <!-- TODO: should this be QTD_TAXABLE_WAGES? -->
                               		<td><xsl:value-of select="QTD_TAX_AMOUNT"/></td>
                               		<td><xsl:value-of select="YTD_TOTAL_WAGES"/></td>  <!-- TODO: should this be YTD_TAXABLE_WAGES? -->
                               		<td><xsl:value-of select="YTD_TAX_AMOUNT"/></td>
                        		</xsl:for-each>
                        	</xsl:variable>
                        	<xsl:choose>
								<xsl:when test="$temp=''">
                        			<td>No records found</td>
								</xsl:when>
								<xsl:otherwise>
									<xsl:copy-of select="$temp"/>
								</xsl:otherwise>
							</xsl:choose>
                        </tr>
                    </table>
                </div>
            </div>
            <script type="text/javascript">
var CollapsiblePanel0 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel0");
var CollapsiblePanel1 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel1");
var CollapsiblePanel2 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel2");
var CollapsiblePanel3 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel3");
var CollapsiblePanel4 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel4");
var CollapsiblePanel5 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel5");
var CollapsiblePanel6 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel6");
var CollapsiblePanel7 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel7");
var CollapsiblePanel8 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel8");
var CollapsiblePanel9 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel9");
</script>
        </body>
    </xsl:template>

    <xsl:template name="FormatDate">
        <xsl:param name="DateTime" />
        <!-- convert from format 2006-01-14T08:55:22.00000 -->
        <!-- To format 01/14/2006 08:55:22 -->
        <xsl:variable name="year">
            <xsl:value-of select="substring($DateTime,1,4)" />
        </xsl:variable>
        <xsl:variable name="mo-temp">
            <xsl:value-of select="substring-after($DateTime,'-')" />
        </xsl:variable>
        <xsl:variable name="mo">
            <xsl:value-of select="substring-before($mo-temp,'-')" />
        </xsl:variable>
        <xsl:variable name="day-temp">
            <xsl:value-of select="substring-after($mo-temp,'-')" />
        </xsl:variable>
        <xsl:variable name="day">
            <xsl:value-of select="substring($day-temp,1,2)" />
        </xsl:variable>
        <xsl:variable name="time">
            <xsl:value-of select="substring-after($mo-temp,'T')" />
        </xsl:variable>
        <xsl:variable name="hh">
            <xsl:value-of select="substring($time,1,2)" />
        </xsl:variable>
        <xsl:variable name="mm">
            <xsl:value-of select="substring($time,4,2)" />
        </xsl:variable>
        <xsl:variable name="ss">
            <xsl:value-of select="substring($time,7,2)" />
        </xsl:variable>
        <xsl:value-of select="$mo"/>
        <xsl:if test="$mo != ''">
            <xsl:value-of select="'/'"/>
	        <xsl:value-of select="$day"/>
	        <xsl:value-of select="'/'"/>
	        <xsl:value-of select="$year"/>
        </xsl:if>
    </xsl:template>    
</xsl:stylesheet>
