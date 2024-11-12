package com.springboot.JWTSecurity;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.springboot.JWTSecurity.service.UserSecurityService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * This class will override a method that will act as Filter
 * */
@Component
public class JwtFilter extends OncePerRequestFilter{
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserSecurityService userSecurityService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		 
		/*
		 * using request, i will take token from spring 
		 * using jwtUtil, i vl decode that token: username 
		 * using userSecurityService, i will fetch user details by username
		 * role..
		 * */
		final String authorizationHeader = request.getHeader("Authorization");
		
		 String username = null;
	     String jwt = null;
	        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	            jwt = authorizationHeader.substring(7);
	            username = jwtUtil.extractUsername(jwt);
	        }
	        
	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            UserDetails userDetails = this.userSecurityService.loadUserByUsername(username);
	            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
	                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
	                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                usernamePasswordAuthenticationToken
	                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	            }
	        }
	        filterChain.doFilter(request, response);
	}

}/*
 *  USER ------->  Jwt-FILTER   ------>  Controller (API)
 * 
 */