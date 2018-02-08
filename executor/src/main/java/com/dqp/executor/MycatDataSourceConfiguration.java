package com.dqp.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * The type Mycat data source configuration.
 */
@Configuration
public class MycatDataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "mycat.spring.datasource")
    @Primary
    public DataSource mycatDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "mycatJdbcTemplate")
    public JdbcTemplate mycatJdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }


}
