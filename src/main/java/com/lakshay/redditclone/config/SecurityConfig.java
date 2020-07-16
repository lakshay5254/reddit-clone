package com.lakshay.redditclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


// contains seciroty config of our whole application
@EnableWebSecurity //enables web security module, sprin security startup dependency
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable() //occur when session and cookies used to authenticate,to secure our app from cross site scripting
							.authorizeRequests() // allow all authrization requests that starts with /api/auth/
							.antMatchers("/api/auth/**")  //any other request that doest match must be authenticated or checked  
							.permitAll()
							.anyRequest()
							.authenticated();
	}
	
	@Bean  // so it can be used else where
	PasswordEncoder PasswordEncoder() {  // as PE is an interface we need to create bean manually 
		return new BCryptPasswordEncoder(); // whenever we autowire it we get object of type Bcry...
	}
	
 
}
