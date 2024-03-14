package eu.europa.ec.sante.openncp.application.client;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
@EnableConfigurationProperties
@Profile("local")
public class JndiConfiguration {
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }

            @Override
            protected void postProcessContext(Context context) {
                context.getNamingResources().addResource(createJNDIResource("jdbc/ConfMgr"));
                context.getNamingResources().addResource(createJNDIResource("jdbc/TSAM"));
            }
        };
    }

    private ContextResource createJNDIResource(String jndiName) {
        final ContextResource resource = new ContextResource();
        resource.setName(jndiName);
        resource.setType(DataSource.class.getName());
       return resource;
    }

//    @Bean(destroyMethod = "")
//    public DataSource jndiConfMgrDataSource() throws IllegalArgumentException, NamingException {
//        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
//        bean.setJndiName("java:comp/env/jdbc/ConfMgr");
//        bean.setProxyInterface(DataSource.class);
//        bean.setLookupOnStartup(false);
//        bean.afterPropertiesSet();
//        return (DataSource) bean.getObject();
//    }

    @Bean(destroyMethod="")
    @ConfigurationProperties(prefix="spring.datasource.default")
    public JndiObjectFactoryBean confMgrDataSource() {
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
