package org.example.stockcalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@EnableScheduling
@EnableJpaAuditing
public class StockCalculatorApplication {

    public static void main(String[] args) {
        System.out.println("ENV: " + System.getenv());
        SpringApplication.run(StockCalculatorApplication.class, args);
    }

}
