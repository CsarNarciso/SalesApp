package com.cesar.Authentication;

import com.cesar.Authentication.util.AuthUserUtils;
import com.cesar.Authentication.util.AuthorityUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AuthenticationApplication {
	
	private final AuthUserUtils authUserUtils;
	private final AuthorityUtils authorityUtils;

	public AuthenticationApplication(AuthUserUtils authUserUtils, AuthorityUtils authorityUtils) {
		this.authUserUtils = authUserUtils;
		this.authorityUtils = authorityUtils;
	}

    public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}

	@Bean
	public CommandLineRunner init() {
		return args -> {

			// Pre-load database data
			authorityUtils.initDefaultPermissionsAndRoles();
			authUserUtils.initDefaultAdmins();

			//Optional, preload test user (for dev testing)
			authUserUtils.initTestUser();
		};
	}
}