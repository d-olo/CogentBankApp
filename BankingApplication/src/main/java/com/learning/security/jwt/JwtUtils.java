package com.learning.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.learning.security.service.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(JwtUtils.class);
	
	// retrieves a specific field from properties file
	@Value("${com.learning.jwtSecret}")
	private String jwtSecret;
	
	@Value("${com.learning.jwtExpirationMs}")
	private long jwtExpirationMs;
	
	// to generate the token
	public String generateToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
	
		// it is using builder design pattern
		return Jwts.builder().setSubject(userPrincipal.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime()+jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact(); // compact() encodes the data
	}
	
	// validation of token
	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (ExpiredJwtException e) {
			LOGGER.error("JWT token expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			LOGGER.error("JWT token is unsupported: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			LOGGER.error("Invalid JWT token: {}", e.getMessage());
		} catch (SignatureException e) {
			LOGGER.error("Invalid JWT token signature: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			LOGGER.error("Invalid argument: {}", e.getMessage());
		}
		
		return false;
	}
	
	// get name from the token -> 
	public String getUsernameFromJwtToken(String authToken) {
		return Jwts.parser()	// compact -> java object
				.setSigningKey(jwtSecret)	// -> secret key -> encoding is done
				.parseClaimsJws(authToken)	// provided actual token
				.getBody()	// extracting the body content
				.getSubject();	// extracting the subject
	}
	
}
