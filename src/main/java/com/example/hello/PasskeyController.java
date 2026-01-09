package com.example.hello;

import java.security.Principal;
import java.util.List;

import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PasskeyController {

	private final UserCredentialRepository credentialsRepository;

	private final PublicKeyCredentialUserEntityRepository publicKeyUserRepository;

	public PasskeyController(UserCredentialRepository credentialsRepository,
			PublicKeyCredentialUserEntityRepository publicKeyUserRepository) {
		this.credentialsRepository = credentialsRepository;
		this.publicKeyUserRepository = publicKeyUserRepository;
	}

	@GetMapping(path = "/passkeys")
	String register(Principal principal, Model model) {
		PublicKeyCredentialUserEntity userEntity = this.publicKeyUserRepository.findByUsername(principal.getName());
		if (userEntity == null) {
			model.addAttribute("passkeys", List.of());
		}
		else {
			List<CredentialRecord> passkeys = this.credentialsRepository.findByUserId(userEntity.getId());
			model.addAttribute("passkeys", passkeys);
		}
		return "passkeys";
	}

}
