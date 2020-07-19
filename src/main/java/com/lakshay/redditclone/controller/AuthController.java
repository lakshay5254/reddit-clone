package com.lakshay.redditclone.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lakshay.redditclone.dto.AuthenticationResponse;
import com.lakshay.redditclone.dto.LoginRequest;
import com.lakshay.redditclone.dto.RegisterRequest;
import com.lakshay.redditclone.service.AuthService;

import lombok.AllArgsConstructor;

// for registering users 

@RestController  //@Controller is a common annotation that is used to mark a class as Spring MVC Controller while @RestController is a special controller used in RESTFul web services and the equivalent of @Controller + @ResponseBody
@RequestMapping("/api/auth") //for every request that contains /api/auth this class controller should be activated 
@AllArgsConstructor
public class AuthController {
	
	private final AuthService authService; //service method to send mail
	
	@PostMapping("/signup") //on signup request
	public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {// RegisterRequest= a DTO(Data transfer object) is a design pattern used to transfer data between software application subsystems. so it contins user entered data
	//@RequestBody : Annotation indicating a method parameter should be bound to the body of the HTTP request. @ResponseBody annotation can be put on a method and indicates that the return type should be written straight to the HTTP response body (and not placed in a Model, or interpreted as a view name)
		
		authService.signup(registerRequest); //passing user details to service
		return new ResponseEntity<>("User Registration successfull",HttpStatus.OK); //if registration successfull 
	}
	
	@GetMapping("accountVerification/{token}")
	public ResponseEntity<String> verifyAccount(@PathVariable String token){ // as we are passing data in a variable so to grab it we using path variable
		authService.verifyAccount(token);
		return new ResponseEntity<>("Account activated succefully",HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
		return authService.login(loginRequest);  //on login request use login method to authenticate user
	}

}
