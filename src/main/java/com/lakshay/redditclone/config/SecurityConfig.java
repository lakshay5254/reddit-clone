package com.lakshay.redditclone.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;



// contains seciroty config of our whole application
@EnableWebSecurity //enables web security module, sprin security startup dependency
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final UserDetailsService userDetailsService; //its Dto that loads user data from different sources like from db
	
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	
	
	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable() //occur when session and cookies used to authenticate,to secure our app from cross site scripting
							.authorizeRequests() // allow all authrization requests that starts with /api/auth/
							.antMatchers("/api/auth/**")  //any other request that doest match must be authenticated or checked  
							.permitAll()
							.anyRequest()
							.authenticated();
	}
	
	// making authentication manager
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService) //as UDS is an interface so we create UserDetailsServiceImpl class
									.passwordEncoder(passwordEncoder());
	}
	
	@Bean  // so it can be used else where
	PasswordEncoder passwordEncoder() {  // as PE is an interface we need to create bean manually 
		return new BCryptPasswordEncoder(); // whenever we autowire it we get object of type Bcry...
	}
	
 
}
