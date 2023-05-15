package com.test.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static java.util.Objects.nonNull;

@Configuration
@ConditionalOnProperty(value = "spring.multiDatasource", havingValue = "false", matchIfMissing = true)
public class DataSourceConfiguration {

    @Primary
    @Bean
    public DataSource primaryDataSource(@Autowired DataSourceProperties dataSourceProperties, @Autowired HikariConfigs configs) {
        DataSource dataSource = DataSourceBuilder.create()
                .url(dataSourceProperties.getUrl())
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .driverClassName(dataSourceProperties.getDriverClassName())
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
            ((HikariDataSource) dataSource).setPoolName("Main Hikari Pool");
        }
        return dataSource;
    }

//    @Bean
//    @ConditionalOnProperty(name = "spring.liquibase.enabled", havingValue = "true")
//    @LiquibaseDataSource
//    public DataSource liquibaseDataSource(@Autowired LiquibaseProperties liquibaseProperties) {
//        DataSource dataSource = DataSourceBuilder.create()
//                .url(liquibaseProperties.getUrl())
//                .username(liquibaseProperties.getUser())
//                .password(liquibaseProperties.getPassword())
//                .driverClassName(liquibaseProperties.getDriverClassName())
//                .build();
//        if (dataSource instanceof HikariDataSource) {
//            ((HikariDataSource) dataSource).setMaximumPoolSize(2);
//            ((HikariDataSource) dataSource).setPoolName("Liquibase Hikari Pool");
//            ((HikariDataSource) dataSource).setMinimumIdle(0);
//        }
//        return dataSource;
//    }
}
