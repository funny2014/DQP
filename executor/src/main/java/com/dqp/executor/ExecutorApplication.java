package com.dqp.executor;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@EnableFeignClients
@EnableCircuitBreaker
@ComponentScan(basePackages = "com.dqp.executor")
//禁止springboot自动注入多个数据源；
@SpringBootApplication( exclude ={
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class
})
public class ExecutorApplication {

	public static void main(String[] args) {
		log.info("start ExecutorApplication...");
		SpringApplication.run(ExecutorApplication.class, args);
	}

	@Bean
	Retryer feignRetryer() {
		return new Retryer() {

			@Override
			public void continueOrPropagate(RetryableException e) {
				throw e;
			}

			@Override
			public Retryer clone() {
				return this;
			}
		};
	}
}
