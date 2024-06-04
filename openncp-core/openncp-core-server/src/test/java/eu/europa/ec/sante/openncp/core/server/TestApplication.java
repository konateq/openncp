package eu.europa.ec.sante.openncp.core.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"eu.europa.ec.sante.openncp.core"})
public class TestApplication {

    public static void main(final String... args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.properties")
    public DataSourcePropertyHolder propertiesDataSourcePropertyHolder() {
        return new DataSourcePropertyHolder();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.tsam")
    public DataSourcePropertyHolder tsamDataSourcePropertyHolder() {
        return new DataSourcePropertyHolder();
    }

    public static class DataSourcePropertyHolder {
        private String url;
        private String driverClassName;
        private String username;
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
