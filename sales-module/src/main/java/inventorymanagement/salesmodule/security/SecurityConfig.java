package inventorymanagement.salesmodule.security;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

//import inventorymanagement.zonemodule.security.RequestInterceptor;

import java.util.Arrays;
 
@Configuration
@EnableWebSecurity
public class SecurityConfig {
 
    @Autowired
    private JwtFilter jwtFilter;
 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
 
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable and configure CORS
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/sales/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
 
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3001")); // Allow requests from React app
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS to /api/ endpoints
        return source;
    }
    @Bean
   	RequestInterceptor requestInterceptor() {
   		return requestTemplate -> {
   			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
   			if(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
   				HttpServletRequest request = servletRequestAttributes.getRequest();
   				String authHeader = request.getHeader("AUTHORIZATION");
   				System.out.print(">>>>>>>>>>>>" + authHeader);
   				if(authHeader != null && !authHeader.isEmpty()) {
   					requestTemplate.header("AUTHORIZATION", authHeader);
   				}
   			}
   		};
   	}
}