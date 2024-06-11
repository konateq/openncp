package eu.europa.ec.sante.openncp.common.security;

import eu.europa.ec.sante.openncp.common.immutables.Domain;

@Domain
public interface SslProperties {
    String getCertificateAlias();

    String getCertificatePassword();

    String getKeystorePath();

    String getKeystorePassword();

    String getTruststorePath();

    String getTruststorePassword();
}
