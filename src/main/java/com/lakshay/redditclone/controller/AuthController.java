package com.lakshay.redditclone.controller;
//we should never write our business logic in our controller, it just recieves requests from the client and move them to service layer
import com.lakshay.redditclone.dto.RefreshTokenRequest;
import com.lakshay.redditclone.service.RefreshTokenService;
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


import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

// for registering users 

@RestController  //@Controller is a common annotation that is used to mark a class as Spring MVC Controller while @RestController is a special controller used in RESTFul web services and the equivalent of @Controller + @ResponseBody
@RequestMapping("/api/auth") //for every request that contains /api/auth this class controller should be activated 
@AllArgsConstructor
public class AuthController {
	
	private final AuthService authService; //service method to send mail
	private final RefreshTokenService refreshTokenService;
	
	@PostMapping("/signup") //on signup request
	public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {// RegisterRequest= a DTO(Data transfer object) is a design pattern used to transfer data between software application subsystems. so it contins user entered data
	//@RequestBody : Annotation indicating a method parameter should be bound to the body of the HTTP request. @ResponseBody annotation can be put on a method and indicates that the return type should be written straight to the HTTP response body (and not placed in a Model, or interpreted as a view name)
		
		authService.signup(registerRequest); //passing user details to service

		return new ResponseEntity<>("User Registration successful", OK); //if registration successfull

	}
	
	@GetMapping("accountVerification/{token}")
	public ResponseEntity<String> verifyAccount(@PathVariable String token){ // as we are passing data in a variable so to grab it we using path variable
		authService.verifyAccount(token);

        return new ResponseEntity<>("Account Activated Successfully", OK);

	}
	
	@PostMapping("/login")
	public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
		return authService.login(loginRequest);  //on login request use login method to authenticate user
	}

	@PostMapping("/refresh/token") //using refresh token to regenerate expired jwt token
	public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) { //valid= throw error if null or emtpy token
		return authService.refreshToken(refreshTokenRequest);
	}

	@PostMapping("/logout") //remove refresh token
	public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
		return ResponseEntity.status(OK).body("Refresh Token Deleted Successfully!!");
	}


}
