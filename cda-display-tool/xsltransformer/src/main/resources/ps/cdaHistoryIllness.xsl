<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n1="urn:hl7-org:v3"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:epsos="urn:epsos-org:ep:medication" version="1.0">
    <xsl:output method="html" indent="yes" version="4.01" doctype-system="http://www.w3.org/TR/html4/strict.dtd"
                doctype-public="-//W3C//DTD HTML 4.01//EN"/>

    <!-- variable to check that at least one History Illness section exist -->
    <xsl:variable name="historyIllnessExist"
                  select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section/n1:code[@code='11348-0']"/>

    <!-- History Illness -->
    <xsl:template name="historyIllness" match="/n1:ClinicalDocument/n1:component/n1:structuredBody">
        <xsl:choose>
            <!-- if we have at least one history illness section -->
            <xsl:when test="($historyIllnessExist)">
                <xsl:for-each select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section">
                    <xsl:call-template name="historyIllnessSection"/>
                </xsl:for-each>
                <br/>
                <br/>
            </xsl:when>
            <!-- in the case the history illness section is missing, nothing is displayed -->
        </xsl:choose>
    </xsl:template>

    <xsl:template name="historyIllnessSection">
        <!-- Defining all needed variables -->
        <xsl:variable
                name="historyIllnessSectionTitleCode"
                select="n1:code/@code"/>
        <xsl:variable name="nullEntry" select="n1:entry"/>
        <xsl:variable name="historyAct" select="n1:entry/n1:entry"/>
        <xsl:variable name="obsValueCode"
                      select="n1:entry/n1:act/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.5.2']/../n1:entryRelationship[@typeCode='SUBJ']/n1:observation/n1:templateId[@root='1.3.6.1.4.1.12559.11.10.1.3.1.3.7']/../n1:value/@code"/>
        <!-- End definition of variables-->
        <xsl:choose>
            <!-- if sectionTitle is not missing for history illness  (Exception history illness section is missing)-->
            <xsl:when test=" ($historyIllnessSectionTitleCode='11348-0')">
                <div class="wrap-collabsible">
                    <input id="collapsible-history-illness-section-original" class="toggle" type="checkbox" checked="true" />
                    <label for="collapsible-history-illness-section-original" class="lbl-toggle-title">
                        <!-- Section title -->
                        <xsl:call-template name="show-eHDSISection">
                            <xsl:with-param name="code" select="'11348-0'"/>
                        </xsl:call-template>
                    </label>
                    <div class="collapsible-content-title">
                        <div class="content-inner-title">
                            <xsl:choose>
                                <xsl:when test="$shownarrative='true'">
                                    <div class="wrap-collabsible">
                                        <input id="collapsible-history-illness-original" class="toggle" type="checkbox"/>
                                        <label for="collapsible-history-illness-original" class="lbl-toggle">
                                            <xsl:value-of select="$originalNarrativeTableTitle"/>
                                        </label>
                                        <div class="collapsible-content">
                                            <div class="content-inner">
                                                <xsl:call-template name="show-narrative">
                                                    <xsl:with-param name="node" select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section/n1:code[@code='11348-0']/../n1:text"/>
                                                </xsl:call-template>
                                            </div>
                                        </div>
                                    </div>
                                </xsl:when>
                            </xsl:choose>
                            <br/>
                            <!-- nullflavored act -->
                            <div class="wrap-collabsible">
                                <input id="collapsible-history-illness-translated" class="toggle" type="checkbox" checked="true"/>
                                <label for="collapsible-history-illness-translated" class="lbl-toggle">
                                    <xsl:value-of select="$translatedCodedTableTitle"/>
                                </label>
                                <div class="collapsible-content">
                                    <div class="content-inner">
                                        <xsl:choose>
                                            <xsl:when test="not($historyAct/@nullFlavor)">
                                                <table class="translation_table">
                                                    <tbody>
                                                        <xsl:if test="not ($obsValueCode='no-known-problems' or $obsValueCode='no-problem-info')">
                                                            <tr>
                                                                <th>
                                                                    <!-- xsl:text>Closed Inactive Problem</xsl:text-->
                                                                    <xsl:call-template name="show-eHDSIDisplayLabel">
                                                                        <xsl:with-param name="code" select="'11'"/>
                                                                    </xsl:call-template>
                                                                </th>
                                                                <th>
                                                                    <!-- xsl:text>Onset Date</xsl:text-->
                                                                    <xsl:call-template name="show-eHDSIDisplayLabel">
                                                                        <xsl:with-param name="code" select="'45'"/>
                                                                    </xsl:call-template>
                                                                </th>
                                                                <th>
                                                                    <!-- xsl:text>End Date</xsl:text-->
                                                                    <xsl:call-template name="show-eHDSIDisplayLabel">
                                                                        <xsl:with-param name="code" select="'26'"/>
                                                                    </xsl:call-template>
                                                                </th>
                                                            </tr>
                                                        </xsl:if>
                                                        <xsl:for-each select="n1:entry">
                                                            <xsl:call-template name="historyIllnessSectionEntry">
                                                            </xsl:call-template>
                                                        </xsl:for-each>
                                                    </tbody>
                                                </table>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:call-template name="show-eHDSINullFlavor">
                                                    <xsl:with-param name="code" select="$historyAct/@nullFlavor"/>
                                                </xsl:call-template>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <!-- FOR EACH ENTRY -->
    <xsl:template name="historyIllnessSectionEntry">
        <!-- Defing all needed variables -->
        <xsl:variable name="problemCondition"
                      select="n1:act/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.5.2']/../n1:entryRelationship[@typeCode='SUBJ']/n1:observation/n1:templateId[@root='1.3.6.1.4.1.12559.11.10.1.3.1.3.7']/../n1:value"/>
        <xsl:variable name="historyIllnessOnSetDate"
                      select="n1:act/n1:templateId[@root= '1.3.6.1.4.1.19376.1.5.3.1.4.5.2']/../n1:effectiveTime/n1:low"/>
        <xsl:variable name="historyIllnessEndDate"
                      select="n1:act/n1:templateId[@root= '1.3.6.1.4.1.19376.1.5.3.1.4.5.2']/../n1:effectiveTime/n1:high"/>
        <!-- End definition of variables-->

        <!-- nullflavored act -->
        <xsl:choose>
            <xsl:when test="not(n1:act/@nullFlavor)">
                <xsl:choose>
                    <xsl:when test="($problemCondition='no-known-problems' or $problemCondition='no-problem-info')">
                        <xsl:call-template name="show-eHDSIAbsentOrUnknownProblem">
                            <xsl:with-param name="code" select="$problemCondition/@code"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <tr>
                            <td>
                                <!-- Closed/Inactive problem -->
                                <xsl:call-template name="show-eHDSIIllnessandDisorder">
                                    <xsl:with-param name="node" select="$problemCondition"/>
                                </xsl:call-template>
                            </td>
                            <td>
                                <!-- Onset Date -->
                                <xsl:call-template name="show-TS">
                                    <xsl:with-param name="node" select="$historyIllnessOnSetDate"/>
                                </xsl:call-template>&#160;
                            </td>
                            <td>
                                <!-- End Date -->
                                <xsl:call-template name="show-TS">
                                    <xsl:with-param name="node" select="$historyIllnessEndDate"/>
                                </xsl:call-template>&#160;
                            </td>
                        </tr>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <tr>
                    <td colspan="3">
                        <xsl:call-template name="show-eHDSINullFlavor">
                            <xsl:with-param name="code" select="n1:act/@nullFlavor"/>
                        </xsl:call-template>
                    </td>
                </tr>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>