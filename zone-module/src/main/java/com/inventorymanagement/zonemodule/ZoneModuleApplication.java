package com.inventorymanagement.zonemodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ZoneModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZoneModuleApplication.class, args);
	}

}
