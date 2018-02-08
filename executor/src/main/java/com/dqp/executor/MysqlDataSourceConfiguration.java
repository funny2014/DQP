package com.dqp.executor;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  the type mysql data source configuration.
 */
@Configuration
public class MysqlDataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "mysql.spring.datasource")
    public DataSourceProperties MysqlDataSourceProperties(){
        return new DataSourceProperties();
    }
}
