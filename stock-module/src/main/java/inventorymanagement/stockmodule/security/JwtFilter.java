package inventorymanagement.stockmodule.security;

import inventorymanagement.stockmodule.security.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
 
import java.io.IOException;
import java.util.List;
 
@Component
public class JwtFilter extends OncePerRequestFilter {
 
    @Autowired
    private JWTService jwtUtil;
 
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
 
        String authHeader = request.getHeader("Authorization");
 
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
 
        String token = authHeader.substring(7);
        System.out.println("token filter:");
        if (!jwtUtil.validateToken(token)) {
            chain.doFilter(request, response);
            return;
        }
 
        String username = jwtUtil.extractEmail(token);
        String roles = jwtUtil.extractRoles(token);
 
        List<GrantedAuthority> authorities = List.of();
 
        UserDetails userDetails = new User(username, "N/A", authorities);
 
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
 
        SecurityContextHolder.getContext().setAuthentication(authToken);
 
        chain.doFilter(request, response);
    }
}