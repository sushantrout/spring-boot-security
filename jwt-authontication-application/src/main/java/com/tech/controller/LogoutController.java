package com.tech.controller;

import java.util.Date;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping(value = "/api/logout")
public class LogoutController {

	@GetMapping
	public String logout() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal != null) {
			UserDetails ob = (UserDetails) principal;
			JwtBuilder setSubject = Jwts.builder().setSubject((ob.getUsername()));
			setSubject.setExpiration(new Date());
		}
		return "<h1>Logged out</h1>";
	}
}
