package com.dqp.executor;


import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Hive data source configuration.
 */
@Configuration
public class HiveDataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "hive.spring.datasource")
    public DataSourceProperties hiveDataSourceProperties() {
        return new DataSourceProperties();
    }
}
