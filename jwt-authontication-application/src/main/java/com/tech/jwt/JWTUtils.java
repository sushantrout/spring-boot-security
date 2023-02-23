package com.tech.jwt;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.tech.entity.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTUtils {
	
	public static Map<String, Boolean> SESSION_MAP = new LinkedHashMap<>();
	
	@Value("${tech.app.jwtSecret}")
	private String jwtSecret;

	@Value("${tech.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Boolean logout = JWTUtils.SESSION_MAP.get(getUserNameFromJwtToken(authToken));
			if(logout != null && logout) {
				return false;
			}
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("token string is empty: {}", e.getMessage());
		}

		return false;
	}
	
	public String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}
}
