package apigateway.apigateway.Route;

import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class Route {
	
	@Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("purchase-module", r -> r.path("/api/purchases/**")
                        .uri("http://localhost:8080"))
                .route("stock-module", r -> r.path("/api/stocks/**")
                        .uri("http://localhost:8053"))
                .route("vendor-module", r -> r.path("/api/vendors/**")
                        .uri("http://localhost:8099"))
                .route("sales-module", r -> r.path("/api/sales/**")
                        .uri("http://localhost:8079"))
                .route("performance-metrics", r -> r.path("/api/metrics/**")
                        .uri("http://localhost:8073"))
                .route("user-module", r -> r.path("/api/auth/**")
                        .uri("http://localhost:8062"))
                .route("zone-module",r -> r.path("/api/zones/**")
                		.uri("http://localhost:8087"))
                        
                .build();
    }

}
