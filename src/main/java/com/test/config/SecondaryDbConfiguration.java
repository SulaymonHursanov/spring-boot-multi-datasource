package com.test.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

import static java.util.Objects.nonNull;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.test.repositoryReadOnly",
        entityManagerFactoryRef = "secondaryEntityManager",
        transactionManagerRef = "secondaryTransactionManager"
)
@ConditionalOnProperty(value = "spring.multiDatasource", havingValue = "true")
public class SecondaryDbConfiguration {

    @Autowired
    private Environment env;

    @Bean
    @ConfigurationProperties(prefix="spring.second-datasource")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource secondaryDataSource(@Autowired HikariConfigs configs) {
        DataSource dataSource = DataSourceBuilder.create()
                .url(secondaryDataSourceProperties().getUrl())
                .username(secondaryDataSourceProperties().getUsername())
                .password(secondaryDataSourceProperties().getPassword())
                .driverClassName(secondaryDataSourceProperties().getDriverClassName())
                .build();

        if (dataSource instanceof HikariDataSource) {
            if (nonNull(configs.getMinimumIdle())) {
                ((HikariDataSource) dataSource).setMinimumIdle(configs.getMinimumIdle());
            }
            if (nonNull(configs.getMaximumPoolSize())) {
                ((HikariDataSource) dataSource).setMaximumPoolSize(configs.getMaximumPoolSize());
            }
            if (nonNull(configs.getConnectionTimeout())) {
                ((HikariDataSource) dataSource).setConnectionTimeout(configs.getConnectionTimeout());
            }
            if (nonNull(configs.getIdleTimeout())) {
                ((HikariDataSource) dataSource).setIdleTimeout(configs.getIdleTimeout());
            }
            ((HikariDataSource) dataSource).setPoolName("Secondary Hikari Pool");
        }
        return dataSource;
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean secondaryEntityManager(@Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(secondaryDataSource);
        em.setPackagesToScan("com.test.entities");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }



    @Bean
    public PlatformTransactionManager secondaryTransactionManager(@Qualifier("secondaryEntityManager")
                                                                      LocalContainerEntityManagerFactoryBean secondaryEntityManager) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(secondaryEntityManager.getObject());
        return transactionManager;
    }
}
