package com.example.javaprojecttemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class JavaProjectTemplateApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(JavaProjectTemplateApplication.class, args);
		
		Health health = ctx.getBean(HealthEndpoint.class).health();
		
		log.info("Application Context health status is {}",health.getStatus());
	}

}
