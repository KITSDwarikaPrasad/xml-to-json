package com.kfplc.converter;

//import com.kfplc.converter.config.ElasticsearchConfiguration;

import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.kfplc.converter")
//@EnableAutoConfiguration(exclude = ElasticsearchConfiguration.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

}
