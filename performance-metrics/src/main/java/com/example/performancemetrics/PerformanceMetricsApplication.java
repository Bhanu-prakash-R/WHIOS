package com.example.performancemetrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) 
@EnableDiscoveryClient
@EnableFeignClients
public class PerformanceMetricsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerformanceMetricsApplication.class, args);
	}

}
