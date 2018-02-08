package com.dqp.core;

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

@Slf4j
@EnableFeignClients
@EnableCircuitBreaker
@SpringBootApplication( exclude ={
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class
})
public class CoreApplication {

	public static void main(String[] args) {
		log.info("start CoreApplication...");
		SpringApplication.run(CoreApplication.class, args);
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
