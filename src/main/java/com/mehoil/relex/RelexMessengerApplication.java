package com.mehoil.relex;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableRetry
@OpenAPIDefinition(info = @Info(title = "RM API", version = "v1"))
public class RelexMessengerApplication {
	public static void main(String[] args) {
		SpringApplication.run(RelexMessengerApplication.class, args);
	}

	@Bean(name = "customTaskExecutor")
	@Primary
	public ThreadPoolTaskExecutor customTaskExecutor() {
		return new ThreadPoolTaskExecutor();
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}
}