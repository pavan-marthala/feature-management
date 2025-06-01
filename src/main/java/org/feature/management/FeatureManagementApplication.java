package org.feature.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FeatureManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeatureManagementApplication.class, args);
    }
}

