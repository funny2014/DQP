package com.dqp.analysor;


import com.dqp.analysor.service.AnalysorInitialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * The type Analysor application.
 */
@Slf4j
@SpringBootApplication
public class AnalysorApplication {

    public static void main(String[] args) throws IOException {
        log.info("start AnalysorApplication...");
        SpringApplication.run(AnalysorApplication.class, args);
        AnalysorInitialService analysorInitialService = new AnalysorInitialService();
    }
}
