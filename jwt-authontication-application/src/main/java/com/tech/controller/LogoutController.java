package com.tech.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tech.jwt.JWTUtils;

@RestController
@RequestMapping(value = "/api/logout")
public class LogoutController {
	
	@Autowired
	private JWTUtils jwtUtils;
	
	@GetMapping
	public String logout(HttpServletRequest request) {
		String token = jwtUtils.parseJwt(request);
		String userNameFromJwtToken = jwtUtils.getUserNameFromJwtToken(token);
		JWTUtils.SESSION_MAP.put(userNameFromJwtToken, true);
		return "Logout sucessful";
	}
}
