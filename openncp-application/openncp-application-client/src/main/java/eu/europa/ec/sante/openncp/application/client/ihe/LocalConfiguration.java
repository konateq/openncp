package eu.europa.ec.sante.openncp.application.client.ihe;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
@EnableConfigurationProperties
@Profile("local")
public class LocalConfiguration {

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        final TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected TomcatWebServer getTomcatWebServer(final Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }

            @Override
            protected void postProcessContext(final Context context) {
                context.getNamingResources().addResource(createJNDIResource("jdbc/ConfMgr"));
                context.getNamingResources().addResource(createJNDIResource("jdbc/TSAM"));

                final SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                final SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        //        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    private Connector redirectConnector() {
        final Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }

    private ContextResource createJNDIResource(final String jndiName) {
        final ContextResource resource = new ContextResource();
        resource.setName(jndiName);
        resource.setType(DataSource.class.getName());
        resource.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        resource.setProperty("url", "jdbc:mysql://localhost:3307/ehealth_properties?allowPublicKeyRetrieval=true&useSSL=false");
        resource.setProperty("username", "root");
        resource.setProperty("password", "Password1");
        return resource;
    }

    @Bean(destroyMethod = "")
    @ConfigurationProperties(prefix = "spring.datasource.default")
    public JndiObjectFactoryBean confMgrDataSource() {
        final JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        return jndiObjectFactoryBean;
    }

    @Bean(destroyMethod = "")
    @ConfigurationProperties(prefix = "spring.datasource.tsam")
    public JndiObjectFactoryBean tsamDataSource() {
        return new JndiObjectFactoryBean();
    }

    //    @Bean(destroyMethod = "")
    //    public DataSource jndiDataTsamSource() throws IllegalArgumentException, NamingException {
    //        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
    //        bean.setJndiName("java:comp/env/jdbc/TSAM");
    //        bean.setProxyInterface(DataSource.class);
    //        bean.setLookupOnStartup(false);
    //        bean.afterPropertiesSet();
    //        return (DataSource) bean.getObject();
    //    }
}
