package eu.europa.ec.sante.openncp.core.common.fhir.services;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);

    private final FhirValidator fhirValidator;

    @Value("${hapi.fhir.validation.enabled}")
    private boolean validationEnabled;

    public ValidationService(final FhirValidator fhirValidator) {
        this.fhirValidator = Validate.notNull(fhirValidator);
    }

    public ValidationResult validate(IBaseResource baseResource, RestOperationTypeEnum restOperationTypeEnum) {
        if (validationEnabled) {
            ValidationResult validationResult = fhirValidator.validateWithResult(baseResource);
            if (validationResult.isSuccessful()) {
                LOGGER.info("Successfully validated the received [{}] resource obtained with the [{}] operation", baseResource.getClass(), restOperationTypeEnum);
            } else {
                LOGGER.error("Validation error for the received [{}] obtained with the [{}] operation", baseResource.getClass(), restOperationTypeEnum);
                for (SingleValidationMessage validationMessage : validationResult.getMessages()) {
                    LOGGER.info(validationMessage.getMessage());
                }
            }
            return validationResult;
        }
        return null;
    }
}
