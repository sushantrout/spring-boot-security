package com.tech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tech.entity.RefreshToken;
import com.tech.entity.UserDetailsImpl;
import com.tech.entity.UserEntity;
import com.tech.exception.TokenRefreshException;
import com.tech.jwt.JWTUtils;
import com.tech.model.JwtResponse;
import com.tech.model.LoginRequest;
import com.tech.model.TokenRefreshRequest;
import com.tech.model.TokenRefreshResponse;
import com.tech.repository.UserRepository;
import com.tech.service.RefreshTokenService;

@RestController
@RequestMapping(value = "/api/user/")
public class UserController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JWTUtils jwtUtils;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	RefreshTokenService refreshTokenService;

	@PostMapping(value = "auth")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		return ResponseEntity.ok(new JwtResponse(token, 
												 userDetails.getId(), 
												 userDetails.getUsername()));
	}
	
	
	@PostMapping("/refreshtoken")
	  public ResponseEntity<?> refreshtoken(TokenRefreshRequest request) {
	    String requestRefreshToken = request.getRefreshToken();

	    return refreshTokenService.findByToken(requestRefreshToken)
	        .map(refreshTokenService::verifyExpiration)
	        .map(RefreshToken::getUser)
	        .map(user -> {
	          String token = jwtUtils.generateTokenFromUsername(user.getUsername());
	          return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
	        })
	        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
	            "Refresh token is not in database!"));
	  }
	
	@PostMapping(value = "sign-up")
	public UserEntity save(@RequestBody UserEntity userEntity) {
		userEntity.setId(null);
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
		return userRepository.save(userEntity);
	}
}
