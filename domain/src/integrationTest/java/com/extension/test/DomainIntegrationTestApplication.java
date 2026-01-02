package com.extension.test;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.extension.test")
@EnableJpaRepositories(basePackages = "com.extension.test")
@EntityScan(basePackages = "com.extension.test")
public class DomainIntegrationTestApplication {
}