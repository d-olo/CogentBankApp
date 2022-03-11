package com.learning.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.learning.security.jwt.AuthEntryPointJwt;
import com.learning.security.jwt.AuthTokenFilter;
import com.learning.security.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity	// makes sure that security environment is enabled
@EnableGlobalMethodSecurity(prePostEnabled=true)	//
/**
 * Manages security settings.
 * @author Oliver Pagalanan
 * @since Mar 9, 2022
 */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;
	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;
	
	
	@Bean	// is used for object customization purposes as per requirement
	//@Scope("prototype")	// to get multiple objects/instances
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
	}
	
	// used to encrypt the passwords
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// to provide the AuthenticationManager object
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// core part of security -> we can restrict the access of end points through this configuration
		// we can set unauthorized access through this
		// we can provide direct go access for signup and signin (authorizing the res)
		// applying token validation for end points
		// CORS: Cross-origin requests - provides support for cross-platform/domain
		
		// for endpoint specification
		http.cors().and().csrf().disable()
		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
		.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and().authorizeRequests().antMatchers("/api/customer/register").permitAll()
		.antMatchers("/api/customer/authenticate").permitAll();	// support for register and login
		
		
		// for handling filters
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		
	}
	
	
}
