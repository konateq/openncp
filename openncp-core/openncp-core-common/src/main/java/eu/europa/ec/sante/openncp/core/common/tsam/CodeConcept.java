package eu.europa.ec.sante.openncp.core.common.tsam;

import eu.europa.ec.sante.openncp.common.immutables.Domain;
import eu.europa.ec.sante.openncp.core.common.ihe.tsam.util.CodedElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.Coding;
import org.w3c.dom.Element;

import java.util.Optional;

@Domain
public interface CodeConcept {

    String getCode();

    Optional<String> getCodeSystemVersion();

    Optional<String> getCodeSystemOid();

    Optional<String> getCodeSystemUrl();

    Optional<String> getCodeSystemName();

    Optional<String> getValueSetOid();

    Optional<String> getValueSetVersion();

    Optional<String> getDisplayName();

    static CodeConcept from(final CodedElement codedElement) {
        Validate.notNull(codedElement);
        return ImmutableCodeConcept.builder()
                .code(codedElement.getCode())
                .codeSystemVersion(codedElement.getVersion())
                .codeSystemOid(codedElement.getOid())
                .codeSystemName(codedElement.getCodeSystem())
                .displayName(codedElement.getDisplayName())
                .valueSetOid(codedElement.getVsOid())
                .valueSetVersion(codedElement.getValueSetVersion())
                .build();
    }

    static CodeConcept from(final Element iheElement, final String valueSetOid, final String valueSetVersion) {
        Validate.notNull(iheElement);

        return ImmutableCodeConcept.builder()
                .code(iheElement.getAttribute("code"))
                .codeSystemVersion(Optional.of(iheElement.getAttribute("codeSystemVersion")).filter(StringUtils::isNotBlank))
                .codeSystemName(Optional.of(iheElement.getAttribute("codeSystemName")).filter(StringUtils::isNotBlank))
                .codeSystemOid(iheElement.getAttribute("codeSystem"))
                .displayName(Optional.of(iheElement.getAttribute("displayName")).filter(StringUtils::isNotBlank))
                .valueSetOid(Optional.ofNullable(valueSetOid))
                .valueSetVersion(Optional.ofNullable(valueSetVersion))
                .build();
    }

    static CodeConcept from(final Coding coding) {
        Validate.notNull(coding);
        return ImmutableCodeConcept.builder()
                .code(coding.getCode())
                .codeSystemVersion(Optional.ofNullable(coding.getVersion()))
                .codeSystemName(Optional.ofNullable(coding.getSystem()))
                .codeSystemUrl(Optional.ofNullable(coding.getSystem()))
                .displayName(Optional.ofNullable(coding.getDisplay()))
                .build();
    }


    default boolean canCheckValueSet() {
        return getValueSetVersion().isPresent() && getValueSetOid().isPresent();
    }
}
