package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"Service", "Repo", "Task", "Scheduler"})
public class AppConfig {
}
