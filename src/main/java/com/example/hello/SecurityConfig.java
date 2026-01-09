package com.example.hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.webauthn.management.JdbcPublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.JdbcUserCredentialRepository;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

	private final WebAuthnProps webAuthnProps;

	public SecurityConfig(WebAuthnProps webAuthnProps) {
		this.webAuthnProps = webAuthnProps;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/error", "/*.css", "/*.js")
				.permitAll()
				.anyRequest()
				.authenticated())
			.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true))
			.webAuthn(webauthn -> webauthn.rpName(webAuthnProps.rpName())
				.rpId(webAuthnProps.rpId())
				.allowedOrigins(webAuthnProps.allowedOrigins())
				.disableDefaultRegistrationPage(true))
			.build();
	}

	@Bean
	UserCredentialRepository userCredentialRepository(JdbcOperations jdbcOperations) {
		return new JdbcUserCredentialRepository(jdbcOperations);
	}

	@Bean
	PublicKeyCredentialUserEntityRepository publicKeyCredentialUserEntityRepository(JdbcOperations jdbcOperations) {
		return new JdbcPublicKeyCredentialUserEntityRepository(jdbcOperations);
	}

}
