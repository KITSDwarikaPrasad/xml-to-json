package com.kfplc.converter;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//import com.kfplc.converter.config.ElasticsearchConfiguration;

import org.springframework.boot.SpringApplication;

@SpringBootApplication
@ComponentScan(basePackages = "com.kfplc.converter")
//@EnableAutoConfiguration(exclude = {ElasticsearchConfiguration.class})
//@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

}
