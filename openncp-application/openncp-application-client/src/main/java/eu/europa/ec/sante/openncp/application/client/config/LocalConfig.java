package eu.europa.ec.sante.openncp.application.client.config;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties
@Profile("local")
public class LocalConfig {

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
                context.getNamingResources().addResource(createJNDIResource("jdbc/ConfMgr", "ehealth_properties"));
                context.getNamingResources().addResource(createJNDIResource("jdbc/TSAM", "ehealth_ltrdb"));
            }
        };
        return tomcat;
    }

    //TODO this needs to be configuration driven
    private ContextResource createJNDIResource(final String jndiName, String databaseName) {
        final ContextResource resource = new ContextResource();
        resource.setName(jndiName);
        resource.setType(DataSource.class.getName());
        resource.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        resource.setProperty("url", String.format("jdbc:mysql://localhost:3307/%s?allowPublicKeyRetrieval=true&useSSL=false", databaseName));
        resource.setProperty("username", "root");
        resource.setProperty("password", "Password1");
        return resource;
    }
}
