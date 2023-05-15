package com.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("spring.datasource.hikari")
public class HikariConfigs {
    private Integer maximumPoolSize;
    private Integer minimumIdle;
    private Long connectionTimeout;
    private Long idleTimeout;
}
