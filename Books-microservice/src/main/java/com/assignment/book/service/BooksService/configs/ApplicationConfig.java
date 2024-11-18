package com.assignment.book.service.BooksService.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
