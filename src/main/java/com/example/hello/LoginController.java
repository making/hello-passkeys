package com.example.hello;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping(path = "/login")
	String login() {
		return "login";
	}

	@GetMapping(path = "/logout")
	String logout() {
		return "logout";
	}

}
