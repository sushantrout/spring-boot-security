package com.tech.controller;

import javax.security.sasl.AuthenticationException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "api/get-current-user")
@RestController
public class TestController {
	
	@GetMapping
	public Object getTestMessage() throws AuthenticationException {
		throw new AuthenticationException("sorry no user found");
	}
}
