package eu.europa.ec.sante.openncp.common.util;

import eu.europa.ec.sante.openncp.common.immutables.Domain;

/**
 * Container abstraction to hold relevant data regarding certificates.
 * Used to circumvent all the {@link eu.europa.ec.sante.openncp.common.configuration.util.Constants} accesses in places
 * that have no {@link eu.europa.ec.sante.openncp.common.configuration.ConfigurationManager} created by spring.
 */
@Domain
public interface CertificatesDataHolder {
    CertificateData getServiceProviderData();

    CertificateData getServiceConsumerData();

    @Domain
    interface CertificateData {
        String getPath();

        String getPassword();

        String getAlias();
    }
}
