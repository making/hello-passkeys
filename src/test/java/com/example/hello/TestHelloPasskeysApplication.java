package com.example.hello;

import org.springframework.boot.SpringApplication;

public class TestHelloPasskeysApplication {

	public static void main(String[] args) {
		SpringApplication.from(HelloPasskeysApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
