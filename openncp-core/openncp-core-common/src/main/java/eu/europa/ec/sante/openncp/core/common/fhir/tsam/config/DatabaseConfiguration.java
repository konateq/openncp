package eu.europa.ec.sante.openncp.core.common.fhir.tsam.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "eu.europa.ec.sante.openncp.core.common.fhir.tsam.persistence")
public class DatabaseConfiguration {

    @Primary
    @Bean(destroyMethod="")
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return DataSourceBuilder.create().url(dataSourceProperties.getUrl()).password(dataSourceProperties.getPassword()).username(dataSourceProperties.getUsername()).driverClassName(dataSourceProperties.getDriverClassName()).build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("eu.europa.ec.sante.myhealtheu.tsam.persistence.model")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
