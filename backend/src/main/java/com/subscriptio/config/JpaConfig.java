package com.subscriptio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.subscriptio.infrastructure.persistence")
public class JpaConfig {
}
