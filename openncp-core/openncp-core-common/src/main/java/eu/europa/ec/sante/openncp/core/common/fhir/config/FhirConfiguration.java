package eu.europa.ec.sante.openncp.core.common.fhir.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.interceptor.api.IInterceptorService;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.openapi.OpenApiInterceptor;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.IPagingProvider;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.validation.FhirValidator;
import eu.europa.ec.sante.openncp.core.common.fhir.context.EuFhirContextFactory;
import eu.europa.ec.sante.openncp.core.common.fhir.interceptors.EuCorsInterceptor;
import eu.europa.ec.sante.openncp.core.common.fhir.interceptors.JwtSamlInterceptor;
import eu.europa.ec.sante.openncp.core.common.fhir.interceptors.SecuredOpenApiInterceptor;
import eu.europa.ec.sante.openncp.core.common.fhir.interceptors.UnsecuredOpenApiInterceptor;
import eu.europa.ec.sante.openncp.core.common.fhir.security.TokenProvider;
import eu.europa.ec.sante.openncp.core.common.ihe.assertionvalidator.saml.SAML2Validator;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@Configuration
@EnableConfigurationProperties(FhirProperties.class)
public class FhirConfiguration {

    final Logger LOG = LoggerFactory.getLogger(FhirConfiguration.class);

    private final FhirProperties properties;

    public FhirConfiguration(final FhirProperties properties) {
        this.properties = Validate.notNull(properties);
    }

    @Bean
    @Primary
    public FhirContext fhirContext() {
        return EuFhirContextFactory.createFhirContext();
    }
    @Bean
    @ConditionalOnExpression("${hapi.fhir.openapi.enabled:false} == true && ${hapi.fhir.openapi.secured:false} == true")
    public OpenApiInterceptor getSecuredOpenApiInterceptor() {
        return new SecuredOpenApiInterceptor();
    }

    @Bean
    @DependsOn("fhirContext")
    public FhirValidator fhirValidator(final FhirContext fhirContext) {
        final NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(fhirContext);
        try {
            LOG.info("Loading eulab_0.1.0-ballot.tgz package");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/eulab_0.1.0-ballot.tgz");
            LOG.info("Loading ips.r4_1.1.0.tgz package");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/ips.r4_1.1.0.tgz");
            LOG.info("Loading myhealtheu_lab_0.0.1.tgz package");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/myhealtheu_lab_0.0.1.tgz");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final ValidationSupportChain validationSupportChain = new ValidationSupportChain(new DefaultProfileValidationSupport(fhirContext),
                                                                                         new CommonCodeSystemsTerminologyService(fhirContext),
                                                                                         new InMemoryTerminologyServerValidationSupport(fhirContext),
                                                                                         npmPackageSupport);

        final FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupportChain);

        final FhirValidator validator = fhirContext.newValidator();
        validator.setValidateAgainstStandardSchema(true);
        validator.registerValidatorModule(instanceValidator);
        return validator;
    }

    @Bean
    public OpenApiInterceptor getUnSecuredOpenApiInterceptor() {
        return new UnsecuredOpenApiInterceptor();
    }


    @Bean
    public JwtSamlInterceptor getJwtSamlInterceptor(TokenProvider tokenProvider, SAML2Validator saml2Validator){
        return new JwtSamlInterceptor(tokenProvider, saml2Validator);
    }

    @Configuration
    static class FhirClientConfiguration {

        @Value("${fhirserver.url:}")
        private String externalFhirServerUrl;

        @Bean
        public IGenericClient getClient(final FhirContext context) {
            return context.newRestfulGenericClient(externalFhirServerUrl);
        }
    }

    //This configuration sets up the HAPI RestfulServer through Spring boot.
    @Configuration
    @EnableConfigurationProperties(FhirProperties.class)
    @ConditionalOnProperty(value = "fhir.server.inclusion", havingValue = "true")
    @ConfigurationProperties("hapi.fhir.rest")
    @SuppressWarnings("serial")
    static class FhirRestfulServerConfiguration extends RestfulServer {

        private final FhirProperties properties;
        private final FhirContext fhirContext;
        private final List<IResourceProvider> resourceProviders;

        private final List<IServerInterceptor> interceptors;
        private final OpenApiInterceptor openApiInterceptor;
        private final IPagingProvider pagingProvider;
        private final EuCorsInterceptor euCorsInterceptor;

        public FhirRestfulServerConfiguration(final FhirProperties properties, final FhirContext fhirContext,
                                              final ObjectProvider<List<IResourceProvider>> resourceProviders,
                                              final ObjectProvider<List<IServerInterceptor>> interceptors,
                                              final ObjectProvider<IPagingProvider> pagingProvider,
                                              final OpenApiInterceptor openApiInterceptor,
                                              final EuCorsInterceptor euCorsInterceptor) {
            this.properties = properties;
            this.fhirContext = fhirContext;
            this.resourceProviders = resourceProviders.getIfAvailable();
            this.pagingProvider = pagingProvider.getIfAvailable();
            this.interceptors = interceptors.getIfAvailable();
            this.openApiInterceptor = openApiInterceptor;
            this.euCorsInterceptor = euCorsInterceptor;
        }

        @Bean
        public ServletRegistrationBean<RestfulServer> fhirServerRegistrationBean() {
            final ServletRegistrationBean<RestfulServer> registration = new ServletRegistrationBean<>(this,
                                                                                                      this.properties.getServer().getServletPath());
            registration.setLoadOnStartup(1);
            return registration;
        }

        @Override
        protected void initialize() throws ServletException {
            super.initialize();

            setFhirContext(this.fhirContext);
            setResourceProviders(this.resourceProviders);
            setPagingProvider(this.pagingProvider);
            setServerAddressStrategy(new HardcodedServerAddressStrategy(this.properties.getServer().getBasePath()));

            final IInterceptorService interceptorService = getInterceptorService();
            interceptors.forEach(interceptorService::registerInterceptor);
            interceptorService.registerInterceptor(this.openApiInterceptor);
            interceptorService.registerInterceptor(this.euCorsInterceptor);
        }
    }
}
