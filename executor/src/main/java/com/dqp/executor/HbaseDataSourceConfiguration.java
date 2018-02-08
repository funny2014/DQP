package com.dqp.executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * The type Hbase data source configuration.
 */
@Configuration
public class HbaseDataSourceConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "hbase.spring.datasource")
    DataSourceProperties hbaseDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "hbase.spring.datasource")
    @Qualifier("hbaseDataSource")
    public DataSource hbaseDataSource() {
        final DataSourceProperties properties = this.hbaseDataSourceProperties();

        final DataSourceBuilder factory = DataSourceBuilder
                .create(properties.getClassLoader())
                .driverClassName(properties.getDriverClassName())
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword());
        if (properties.getType() != null) {
            factory.type(properties.getType());
        }
        return factory.build();
    }

    @Bean(name = "hbaseJdbcTemplate")
    public JdbcTemplate hbaseJdbcTemplate() {
        return new JdbcTemplate(this.hbaseDataSource());
    }
}
