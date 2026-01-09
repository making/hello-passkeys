package com.example.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HelloController {

	private final Logger log = LoggerFactory.getLogger(HelloController.class);

	@GetMapping(path = "/")
	public Object hello(Authentication authentication) {
		log.info("Authentication={}", authentication);
		return "index";
	}

}
