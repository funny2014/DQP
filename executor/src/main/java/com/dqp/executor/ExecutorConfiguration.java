package com.dqp.executor;


import com.dqp.executor.util.ExecutorProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * The type Executor configuration.
 */
@Configuration
public class ExecutorConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "executor")
    public ExecutorProperties executorProperties() {
        return new ExecutorProperties();
    }

}
