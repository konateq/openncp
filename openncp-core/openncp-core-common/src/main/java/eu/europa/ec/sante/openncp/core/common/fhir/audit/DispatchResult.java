package eu.europa.ec.sante.openncp.core.common.fhir.audit;

import eu.europa.ec.sante.openncp.common.immutables.Domain;
import org.apache.commons.lang3.Validate;

import java.util.Optional;

@Domain
public interface DispatchResult {
    DispatchStatus getDispatchStatus();

    Optional<DispatchError> getError();
    
    default boolean isSuccess() {
        return getDispatchStatus() == DispatchStatus.SENT;
    }

    static DispatchResult success() {
        return ImmutableDispatchResult.builder().dispatchStatus(DispatchStatus.SENT).build();
    }

    static DispatchResult failure(final Throwable cause) {
        Validate.notNull(cause, "throwable cause must not be null");

        final DispatchError dispatchError = DispatchError.of(cause);
        return ImmutableDispatchResult.builder().dispatchStatus(DispatchStatus.FAILED).error(dispatchError).build();
    }

    static DispatchResult failure(final String errorMessage, final Throwable cause) {
        Validate.notNull(errorMessage, "error message must not be null");
        Validate.notNull(cause, "throwable cause must not be null");

        final DispatchError dispatchError = DispatchError.of(errorMessage, cause);
        return ImmutableDispatchResult.builder().dispatchStatus(DispatchStatus.FAILED).error(dispatchError).build();
    }

    static DispatchResult failure(final String errorMessage) {
        Validate.notNull(errorMessage, "error message must not be null");

        final DispatchError dispatchError = DispatchError.of(errorMessage);
        return ImmutableDispatchResult.builder().dispatchStatus(DispatchStatus.FAILED).error(dispatchError).build();
    }

    enum DispatchStatus {
        IN_QUEUE,
        IN_PROGRESS,
        SENT,
        FAILED
    }

    @Domain
    interface DispatchError {
        Optional<String> getErrorMessage();

        Optional<Throwable> getThrowable();

        static DispatchError of(final String errorMessage, final Throwable throwable) {
            Validate.notNull(errorMessage, "error message must not be null");
            Validate.notNull(throwable, "throwable must not be null");

            return ImmutableDispatchError.builder().errorMessage(errorMessage).throwable(throwable).build();
        }

        static DispatchError of(final Throwable throwable) {
            Validate.notNull(throwable, "throwable must not be null");

            return ImmutableDispatchError.builder().throwable(throwable).build();
        }

        static DispatchError of(final String errorMessage) {
            Validate.notNull(errorMessage, "error message must not be null");

            return ImmutableDispatchError.builder().errorMessage(errorMessage).build();
        }
    }
}
