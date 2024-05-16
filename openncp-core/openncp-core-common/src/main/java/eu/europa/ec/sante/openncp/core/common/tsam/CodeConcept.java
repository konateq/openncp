package eu.europa.ec.sante.openncp.core.common.tsam;

import eu.europa.ec.sante.openncp.core.common.ihe.tsam.util.CodedElement;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.r4.model.Coding;
import org.immutables.value.Value;
import org.w3c.dom.Element;

import java.util.Optional;

@Value.Immutable
public interface CodeConcept {
    String getCodeSystemOid();

    String getCodeSystemVersion();

    String getCodeSystemName();

    Optional<String> getValueSetOid();

    Optional<String> getValueSetVersion();

    String getCode();

    Optional<String> getDisplayName();

    static CodeConcept from(CodedElement codedElement) {
        Validate.notNull(codedElement);
        return ImmutableCodeConcept.builder()
                .code(codedElement.getCode())
                .codeSystemName(codedElement.getCodeSystem())
                .codeSystemVersion(codedElement.getVersion())
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
                .codeSystemName(iheElement.getAttribute("codeSystemName"))
                .codeSystemVersion(iheElement.getAttribute("codeSystemVersion"))
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
                .codeSystemName(coding.getSystem())
                .codeSystemOid(coding.getId())
                .codeSystemVersion(coding.getVersion())
                .build();
    }


    default boolean canCheckValueSet() {
        return getValueSetVersion().isPresent() && getValueSetOid().isPresent();
    }
}
