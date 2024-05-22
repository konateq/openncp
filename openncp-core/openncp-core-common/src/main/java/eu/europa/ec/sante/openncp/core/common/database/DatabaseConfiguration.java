package eu.europa.ec.sante.openncp.core.common.database;

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

public class DatabaseConfiguration {

    @EnableJpaRepositories(
            basePackages = "eu.europa.ec.sante.openncp.core.common.property",
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
                    .packages("eu.europa.ec.sante.openncp.core.common.property")
                    .build();
        }

        @Primary
        @Bean
        public PlatformTransactionManager propertiesTransactionManager(@Qualifier("propertiesEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }
    }

    @EnableJpaRepositories(
            basePackages = "eu.europa.ec.sante.openncp.core.common.tsam",
            entityManagerFactoryRef = "tsamEntityManagerFactory",
            transactionManagerRef = "tsamTransactionManager"
    )
    @Configuration
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

        @Bean
        public LocalContainerEntityManagerFactoryBean tsamEntityManagerFactory(EntityManagerFactoryBuilder builder) {
            return builder
                    .dataSource(tsamDataSource())
                    .packages("eu.europa.ec.sante.openncp.core.common.tsam")
                    .build();
        }

        @Primary
        public PlatformTransactionManager tsamTransactionManager(@Qualifier("tsamEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
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
