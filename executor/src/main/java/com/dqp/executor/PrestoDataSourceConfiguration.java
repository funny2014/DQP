package com.dqp.executor;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  the type presto data source configuration
 */
@Configuration
public class PrestoDataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "presto.spring.datasource")
    public DataSourceProperties prestoDataSourceProperties() {
        return new DataSourceProperties();
    }
}
