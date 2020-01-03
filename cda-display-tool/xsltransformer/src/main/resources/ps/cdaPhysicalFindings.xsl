<?xml version="1.0"  ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n1="urn:hl7-org:v3"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:epsos="urn:epsos-org:ep:medication" version="1.0">
    <xsl:output method="html" indent="yes" version="4.01" doctype-system="http://www.w3.org/TR/html4/strict.dtd"
                doctype-public="-//W3C//DTD HTML 4.01//EN"/>

    <!-- variable to check that at least one physical findings section exist -->
    <xsl:variable name="physicalFindingsExist"
                  select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section/n1:code[@code='8716-3']"/>

    <!-- physical findings -->
    <xsl:template name="physicalFindings" match="/n1:ClinicalDocument/n1:component/n1:structuredBody">
        <xsl:choose>
            <!-- if we have at least one physical findings section -->
            <xsl:when test="($physicalFindingsExist)">
                <xsl:for-each select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section">
                    <xsl:call-template name="physicalFindingsSection"/>
                </xsl:for-each>
                <br/>
                <br/>
            </xsl:when>
            <!-- in the case the physical findings section is missing, nothing is displayed -->
        </xsl:choose>
    </xsl:template>

    <xsl:template name="physicalFindingsSection">
        <!-- Defining all needed variables -->
        <xsl:variable
                name="physicalFindingsSectionTitleCode"
                select="n1:code/@code"/>
        <xsl:variable
                name="systolicObservation"
                select="n1:entry/n1:organizer/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.13.1']/../n1:component/n1:observation/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.13.2']/../n1:code[@code='8480-6']/.."/>
        <xsl:variable
                name="diastolicObservation"
                select="n1:entry/n1:organizer/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.13.1']/../n1:component/n1:observation/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.13.2']/../n1:code[@code='8462-4']/.."/>
        <xsl:variable
                name="systolicDate"
                select="$systolicObservation/n1:effectiveTime/@value"/>
        <xsl:variable
                name="diastolicDate"
                select="$diastolicObservation/n1:effectiveTime/@value"/>
        <xsl:variable
                name="physicalFindingsOrganizerDate"
                select="n1:entry/n1:organizer/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.13.1']/../n1:effectiveTime"/>
        <xsl:variable name="physAct"
                      select="n1:entry/n1:observation"/>
        <!-- End definition of variables-->

        <xsl:choose>
            <!-- if sectionTitle is not missing for physical findings  (Exception physical findings section is missing)-->
            <xsl:when test=" ($physicalFindingsSectionTitleCode='8716-3')">
                <div class="wrap-collabsible">
                    <input id="collapsible-physical-findings-section-original" class="toggle" type="checkbox" checked="true" />
                    <label for="collapsible-physical-findings-section-original" class="lbl-toggle-title">
                        <!-- Section title -->
                        <xsl:call-template name="show-epSOSSections">
                            <xsl:with-param name="code" select="'8716-3'"/>
                        </xsl:call-template>
                    </label>
                    <div class="collapsible-content-title">
                        <div class="content-inner-title">
                            <xsl:choose>
                                <xsl:when test="(not($physicalFindingsOrganizerDate/@value = $systolicDate) or not($physicalFindingsOrganizerDate/@value = $diastolicDate))">
                                    <table>
                                        <tr>
                                            <td style="background-color:#ffffcc">
                                                <i class="fas fa-exclamation-triangle" style="color:#085a9f" aria-hidden="true"/>
                                                <!-- HOTFIX - Has to be replaced with a value from the epSOSDisplayLabel value set -->
                                                The date of the organizer should match the date of the observations.<br/>
                                            </td>
                                        </tr>
                                    </table>
                                </xsl:when>
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$shownarrative='true'">
                                    <div class="wrap-collabsible">
                                        <input id="collapsible-physical-findings-original" class="toggle" type="checkbox"/>
                                        <label for="collapsible-physical-findings-original" class="lbl-toggle">
                                            <xsl:value-of select="$originalNarrativeTableTitle"/>
                                        </label>
                                        <div class="collapsible-content">
                                            <div class="content-inner">
                                                <xsl:call-template name="show-narrative">
                                                    <xsl:with-param name="node" select="/n1:ClinicalDocument/n1:component/n1:structuredBody/n1:component/n1:section/n1:code[@code='8716-3']/../n1:text"/>
                                                </xsl:call-template>
                                            </div>
                                        </div>
                                    </div>
                                </xsl:when>
                            </xsl:choose>
                            <br/>
                            <!-- nullflavored act -->
                            <div class="wrap-collabsible">
                                <input id="collapsible-physical-findings-translated" class="toggle" type="checkbox" checked="true"/>
                                <label for="collapsible-physical-findings-translated" class="lbl-toggle">
                                    <xsl:value-of select="$translatedCodedTableTitle"/>
                                </label>
                                <div class="collapsible-content">
                                    <div class="content-inner">
                                        <xsl:choose>
                                            <xsl:when test="not($physAct/@nullFlavor)">
                                                <table class="translation_table">
                                                    <tbody>
                                                        <tr>
                                                            <th>
                                                                <!--  Date -->
                                                                <xsl:call-template name="show-epSOSDisplayLabels">
                                                                    <xsl:with-param name="code" select="'17'"/>
                                                                </xsl:call-template>
                                                            </th>
                                                            <th>
                                                                <!-- Systolic blood pressure -->
                                                                <xsl:call-template name="show-epSOSBloodPressure">
                                                                    <xsl:with-param name="code" select="'8480-6'"/>
                                                                </xsl:call-template>
                                                            </th>
                                                            <th>
                                                                <!-- Diastolic blood pressure -->
                                                                <xsl:call-template name="show-epSOSBloodPressure">
                                                                    <xsl:with-param name="code" select="'8462-4'"/>
                                                                </xsl:call-template>
                                                            </th>
                                                        </tr>
                                                        <xsl:for-each select="n1:entry">
                                                            <xsl:call-template name="physicalFindingsSectionEntry">
                                                                <xsl:with-param name="physicalFindingsOrganizerDate" select="$physicalFindingsOrganizerDate"/>
                                                            </xsl:call-template>
                                                        </xsl:for-each>
                                                    </tbody>
                                                </table>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:call-template name="show-epSOSNullFlavor">
                                                    <xsl:with-param name="code" select="$physAct/@nullFlavor"/>
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
    <xsl:template name="physicalFindingsSectionEntry">
        <xsl:param name="physicalFindingsOrganizerDate"/>
        <!-- Defining all needed variables -->
        <xsl:variable
                name="systolicValue"
                select="n1:organizer/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.13.1']/../n1:component/n1:observation/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.13.2']/../n1:code[@code='8480-6']/../n1:value"/>
        <xsl:variable
                name="diastolicValue"
                select="n1:organizer/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.13.1']/../n1:component/n1:observation/n1:templateId[@root='1.3.6.1.4.1.19376.1.5.3.1.4.13.2']/../n1:code[@code='8462-4']/../n1:value"/>

        <xsl:variable name="physAct" select="n1:observation"/>
        <!-- End definition of variables-->

        <!-- nullflavored act -->
        <xsl:choose>
            <xsl:when test="not($physAct/@nullFlavor)">
                <tr>
                    <td>
                        <xsl:call-template name="show-TS">
                            <xsl:with-param name="node" select="$physicalFindingsOrganizerDate"/>
                        </xsl:call-template>&#160;
                    </td>
                    <td>
                        <xsl:call-template name="show-PQ">
                            <xsl:with-param name="node" select="$systolicValue"/>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="show-PQ">
                            <xsl:with-param name="node" select="$diastolicValue"/>
                        </xsl:call-template>
                    </td>
                </tr>
            </xsl:when>
            <xsl:otherwise>
                <tr>
                    <td colspan="3">
                        <xsl:call-template name="show-epSOSNullFlavor">
                            <xsl:with-param name="code" select="$physAct/@nullFlavor"/>
                        </xsl:call-template>
                    </td>
                </tr>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
