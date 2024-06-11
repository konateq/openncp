package eu.europa.ec.sante.openncp.core.common.tsam;

import eu.europa.ec.sante.openncp.common.immutables.Domain;
import eu.europa.ec.sante.openncp.core.common.ihe.tsam.util.CodedElement;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.Coding;
import org.immutables.value.Value;
import org.w3c.dom.Element;

import java.util.Optional;

@Domain
public interface CodeConcept {

    String getCode();

    String getCodeSystemVersion();

    Optional<String> getCodeSystemOid();

    Optional<String> getCodeSystemUrl();

    Optional<String> getCodeSystemName();

    Optional<String> getValueSetOid();

    Optional<String> getValueSetVersion();

    Optional<String> getDisplayName();

    static CodeConcept from(CodedElement codedElement) {
        Validate.notNull(codedElement);
        return ImmutableCodeConcept.builder()
                .code(codedElement.getCode())
                .codeSystemVersion(codedElement.getVersion())
                .codeSystemName(codedElement.getCodeSystem())
                .displayName(codedElement.getDisplayName())
                .valueSetOid(codedElement.getVsOid())
                .valueSetVersion(codedElement.getValueSetVersion())
                .build();
    }

    static CodeConcept from(Element iheElement, String valueSetOid, String valueSetVersion) {
        Validate.notNull(iheElement);
        Validate.notNull(valueSetOid);
        Validate.notNull(valueSetVersion);

        return ImmutableCodeConcept.builder()
                .code(iheElement.getAttribute("code"))
                .codeSystemVersion(iheElement.getAttribute("codeSystemVersion"))
                .codeSystemName(iheElement.getAttribute("codeSystemName"))
                .codeSystemOid(iheElement.getAttribute("codeSystem"))
                .displayName(iheElement.getAttribute("displayName"))
                .valueSetOid(valueSetOid)
                .valueSetVersion(valueSetVersion)
                .build();
    }

    static CodeConcept from(Coding coding) {
        Validate.notNull(coding);
        return ImmutableCodeConcept.builder()
                .code(coding.getCode())
                .codeSystemVersion(coding.getVersion())
                .codeSystemName(Optional.ofNullable(coding.getSystem()))
                .codeSystemUrl(Optional.ofNullable(coding.getSystem()))
                .displayName(Optional.ofNullable(coding.getDisplay()))
                .build();
    }


    default boolean canCheckValueSet() {
        return getValueSetVersion().isPresent() && getValueSetOid().isPresent();
    }
}
