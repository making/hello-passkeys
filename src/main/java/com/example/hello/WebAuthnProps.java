package com.example.hello;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webauthn")
public record WebAuthnProps(String rpName, String rpId, Set<String> allowedOrigins) {
}
