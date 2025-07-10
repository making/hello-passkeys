package com.example.hello;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping(path = "/")
	public Object hello(Principal principal) {
		return "Hello %s!".formatted(principal.getName());
	}

}
