package dev.kovaliv.test_task.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "dev.kovaliv.test_task.data")
public class JpaConfig {
}
