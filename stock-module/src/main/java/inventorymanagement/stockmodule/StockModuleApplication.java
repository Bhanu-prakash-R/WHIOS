package inventorymanagement.stockmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


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
@EnableScheduling
public class StockModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockModuleApplication.class, args);
	}

}
