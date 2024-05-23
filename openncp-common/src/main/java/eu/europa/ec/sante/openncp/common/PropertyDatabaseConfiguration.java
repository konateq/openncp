package eu.europa.ec.sante.openncp.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

public class PropertyDatabaseConfiguration {

    @EnableJpaRepositories(
            basePackages = "eu.europa.ec.sante.openncp.common.property",
            entityManagerFactoryRef = "propertiesEntityManagerFactory",
            transactionManagerRef = "propertiesTransactionManager")
    @Configuration
    public static class PropertiesDatabaseConfiguration {
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.jndi.properties")
        public JndiPropertyHolder propertiesJndiPropertyHolder() {
            return new JndiPropertyHolder();
        }

        @Bean
        @Primary
        public DataSource propertiesDataSource() {
            final JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
            final JndiPropertyHolder jndiPropertyHolder = propertiesJndiPropertyHolder();
            final DataSource dataSource = dataSourceLookup.getDataSource(jndiPropertyHolder.getJndiName());
            return dataSource;
        }

        @Primary
        @Bean
        public LocalContainerEntityManagerFactoryBean propertiesEntityManagerFactory(EntityManagerFactoryBuilder builder) {
            return builder
                    .dataSource(propertiesDataSource())
                    .packages("eu.europa.ec.sante.openncp.common.property")
                    .build();
        }

        @Primary
        @Bean
        public PlatformTransactionManager propertiesTransactionManager(@Qualifier("propertiesEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }
    }
}
