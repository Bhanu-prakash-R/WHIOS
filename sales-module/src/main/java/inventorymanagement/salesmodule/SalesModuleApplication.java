package inventorymanagement.salesmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application configuration:
 * - @SpringBootApplication: Entry point for the Spring Boot app.
 * - @EnableDiscoveryClient: Enables service discovery.
 * - @EnableFeignClients: Allows Feign client communication.
 * - @EnableScheduling: Supports scheduled tasks.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SalesModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesModuleApplication.class, args);
	}

}
