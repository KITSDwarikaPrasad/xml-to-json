package com.kfplc.converter.config;

import javax.annotation.Resource;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


@Configuration
@PropertySource(value = "classpath:elasticsearch.properties")
//@EnableElasticsearchRepositories(basePackages = "com.kfplc.converter.repository")
public class ElasticsearchConfiguration {
    @Resource
    private Environment environment;
    
    public ElasticsearchConfiguration() {
    	System.out.println("----------------------------> ElasticsearchConfiguration------");
    }
    @Bean
    public RestClient client() {
    	System.out.println("----------------------------> RestClient client()------");
    	System.out.println("----------------------------> environment.getProperty(\"elasticsearch.host\")------" + environment.getProperty("elasticsearch.host")+"------------------------------");
    	RestClient restClient = RestClient.builder(
 		       new HttpHost(environment.getProperty("elasticsearch.host"), Integer.parseInt(environment.getProperty("elasticsearch.port")), "http")
		       //new HttpHost("localhost", 9200, "http")
    		      // , new HttpHost("localhost", 9205, "http")
    		       ).build();
    	System.out.println("--------------restClient:"+restClient+"--------------------");
        return restClient;
    }

//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        return new ElasticsearchTemplate(client());
//    }


}
