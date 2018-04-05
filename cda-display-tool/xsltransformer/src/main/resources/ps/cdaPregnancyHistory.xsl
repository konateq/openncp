<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n1="urn:hl7-org:v3"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:epsos="urn:epsos-org:ep:medication">
    <xsl:output method="html" indent="yes" version="4.01" doctype-system="http://www.w3.org/TR/html4/strict.dtd"
                doctype-public="-//W3C//DTD HTML 4.01//EN"/>


    <!-- variable to check that at least one pregnancy history section exist -->
    <xsl:variable name="pregnancyHistoryExist"
                  select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section/n1:code[@code='10162-6']"/>

    <!--social histories -->
    <xsl:template name="pregnancyHistory" match="/n1:ClinicalDocument/n1:component/n1:structuredBody">

        <xsl:choose>
            <!-- if we have at least one pregnancy history section -->
            <xsl:when test="($pregnancyHistoryExist)">
                <xsl:for-each select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section">
                    <xsl:call-template name="pregnancyHistorySection"/>
                </xsl:for-each>
                <br/>
            </xsl:when>
            <!-- in the case the social history section is missing, nothing is displayed -->
        </xsl:choose>
    </xsl:template>


    <xsl:template name="pregnancyHistorySection">

        <!-- Defining all needed variables -->
        <xsl:variable
                name="pregnancyHistorySectionTitleCode"
                select="n1:code/@code"/>
        <xsl:variable
                name="pregnancyHistorySectionTitle"
                select="n1:code[@code='10162-6']/@displayName"/>
        <xsl:variable name="pregnancyHistoryObservation"
                      select="n1:entry/n1:observation"/>
        <!-- End definition of variables-->

        <xsl:choose>
            <!-- if sectionTitle is not missing for pregnancy history (Exception pregnancy history section is missing)-->
            <xsl:when test="($pregnancyHistorySectionTitleCode='10162-6')">
                <span class="sectionTitle">
                    <xsl:value-of select="$pregnancyHistorySectionTitle"/>
                </span>
                <br/>
                <xsl:choose>
                    <xsl:when test="$shownarrative='true'">
                        <a href="javascript: showhide('pregHistTr'); self.focus(); void(0);">Show/Hide</a>
                        <div id="pregHistTr" style="display:block">
                            <xsl:apply-templates
                                    select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section/n1:code[@code='10162-6']/../n1:text/*"/>
                            <br/>
                        </div>
                    </xsl:when>
                </xsl:choose>
                <xsl:choose>
                    <xsl:when test="not($pregnancyHistoryObservation/@nullFlavor)">
                        <table>
                            <tbody>
                                <tr>
                                    <th>
                                        <xsl:choose>
                                            <xsl:when test="$pregnancyHistoryObservation/n1:code/@displayName">
                                                <!-- TODO this has to become a value in the epsosDisplayLabel value set -->
                                                <xsl:value-of select="$pregnancyHistoryObservation/n1:code/@displayName"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <!-- uncoded element Problem -->
                                                <xsl:if test="$pregnancyHistoryObservation/n1:value/n1:originalText/n1:reference/@value">
                                                    <xsl:call-template name="show-uncodedElement">
                                                        <xsl:with-param name="code"
                                                                        select="$pregnancyHistoryObservation/n1:value/n1:originalText/n1:reference/@value"/>
                                                    </xsl:call-template>
                                                </xsl:if>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </th>
                                <xsl:for-each select="$pregnancyHistoryObservation">
                                    <xsl:call-template name="pregnancyHistorySectionEntry"/>
                                </xsl:for-each>
                                </tr>
                            </tbody>
                        </table>
                    </xsl:when>
                    <xsl:otherwise>
                        <tr>
                            <td>
                                <xsl:call-template name="show-nullFlavor">
                                    <xsl:with-param name="code" select="$pregnancyHistoryObservation/@nullFlavor"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <!-- Pregnancy History Section Entry-->
    <xsl:template name="pregnancyHistorySectionEntry">

        <!-- Defining all needed variables -->
        <xsl:variable
                name="pregnancyTypeDate"
                select="n1:code/@displayName"/>
        <xsl:variable
                name="pregnancyExpectedDate"
                select="n1:value[@xsi:type='TS' or substring-after(@xsi:type, ':')='TS']"/>
        <xsl:variable
                name="pregnancyExpectedDateNode"
                select="n1:value"/>
        <xsl:variable name="pregHistAct"
                      select="n1:observation"/>
        <!-- End definition of variables-->


        <!-- nullflavored act -->
        <xsl:choose>
            <xsl:when test="not($pregHistAct/@nullFlavor)">
                    <td>
                        <xsl:choose>
                            <xsl:when test="not ($pregnancyExpectedDateNode/@nullFlavor)">
                                <xsl:call-template name="show-time">
                                    <xsl:with-param name="datetime" select="$pregnancyExpectedDate"/>
                                </xsl:call-template>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:call-template name="show-nullFlavor">
                                    <xsl:with-param name="code" select="$pregnancyExpectedDateNode/@nullFlavor"/>
                                </xsl:call-template>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
            </xsl:when>
            <xsl:otherwise>
                <tr>
                    <td>
                        <xsl:call-template name="show-nullFlavor">
                            <xsl:with-param name="code" select="$pregHistAct/@nullFlavor"/>
                        </xsl:call-template>
                    </td>
                </tr>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>