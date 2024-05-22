package eu.europa.ec.sante.openncp.core.common.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties
public class DatabaseConfiguration {

    @EnableJpaRepositories(basePackages = "eu.europa.ec.sante.openncp.core.common.property")
    @EntityScan("eu.europa.ec.sante.openncp.core.common.property")
    public static class PropertiesDatabaseConfiguration {
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.jndi.properties")
        public JndiPropertyHolder propertiesJndiPropertyHolder(@Value("spring.datasource.jndi.properties.jndiName") String jndiName) {
            return new JndiPropertyHolder();
        }

        @Bean
        @Primary
        public DataSource propertiesDataSource() {
            final JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
            final DataSource dataSource = dataSourceLookup.getDataSource(propertiesJndiPropertyHolder("test").getJndiName());
            return dataSource;
        }
    }

    @EnableJpaRepositories(basePackages = "eu.europa.ec.sante.openncp.core.common.tsam")
    @EntityScan("eu.europa.ec.sante.openncp.core.common.tsam")
    public static class TsamDatabaseConfiguration {
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.jndi.tsam")
        public JndiPropertyHolder tsamJndiPropertyHolder() {
            return new JndiPropertyHolder();
        }

        @Bean
        public DataSource tsamDataSource() {
            final JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
            final DataSource dataSource = dataSourceLookup.getDataSource(tsamJndiPropertyHolder().getJndiName());
            return dataSource;
        }
    }

    public static class JndiPropertyHolder {
        private String jndiName;

        public String getJndiName() {
            return jndiName;
        }

        public void setJndiName(String jndiName) {
            this.jndiName = jndiName;
        }
    }

}
