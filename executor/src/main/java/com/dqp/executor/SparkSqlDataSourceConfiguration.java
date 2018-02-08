package com.dqp.executor;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  the type spark data source configuration
 */
@Configuration
public class SparkSqlDataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "sparksql.spring.datasource")
    public DataSourceProperties sparkSqlDataSourceProperties(){
        return new DataSourceProperties();
    }
}
