package com.cesar.Authentication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.cesar.Authentication.security.filter.JwtTokenValidator;
import com.cesar.Authentication.service.UserDetailServiceImpl;
import com.cesar.Authentication.util.JwtUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtUtils jwtUtils;
	private final HandlerExceptionResolver handlerExceptionResolver;

	public SecurityConfig(JwtUtils jwtUtils, HandlerExceptionResolver handlerExceptionResolver) {
		this.jwtUtils = jwtUtils;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(c -> c.disable()).httpBasic(Customizer.withDefaults())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(request -> {

					// For auth operations
					request.anyRequest().permitAll();

				}).addFilterBefore(new JwtTokenValidator(jwtUtils, handlerExceptionResolver), BasicAuthenticationFilter.class).build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
		return authConfiguration.getAuthenticationManager();
	}

	@Bean
	AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailsServiceImpl) {

		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsServiceImpl);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}