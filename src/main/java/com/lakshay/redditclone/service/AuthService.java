package com.lakshay.redditclone.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshay.redditclone.dto.RegisterRequest;
import com.lakshay.redditclone.exception.SpringRedditException;
import com.lakshay.redditclone.model.NotificationEmail;
import com.lakshay.redditclone.model.User;
import com.lakshay.redditclone.model.VerificationToken;
import com.lakshay.redditclone.repository.UserRepository;
import com.lakshay.redditclone.repository.VerificationTokenRepository;

import lombok.AllArgsConstructor;

// logic to register users or creating user objects saving to db, sending activation emails 

@Service
@AllArgsConstructor  // this will autowire all  final fields using constructor
public class AuthService {
	
	/*
	 * @Autowired 
	 * private PasswordEncoder passwordEncoder;
	 * @Autowired// autowiring user repository for saving user in db private
	 * private UserRepository userRepository;
	 */ 
	//this is field injection but we should use construction injection always 
	
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final MailService mailService; // 
	
	
	@Transactional  // to follow rules of db transactions
	public void signup(RegisterRequest registerRequest) {
		User user =new  User();
		user.setUsername(registerRequest.getUsername()); //mapping user entity or saving data in db with data entered by user that is stored inside registerRequest
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); //PE present in authserive 
		user.setCreated(Instant.now()); //instant.now used to get current time 
		user.setEnabled(false); //disable user till authentication
		userRepository.save(user); 
		
		//email verification
		String token=generateVerificationToken(user);
		// now to send these token to user email for verification we need to send html template with it to create it we can use thymleaf add dependecny
		//then create template
		mailService.sendMail(new NotificationEmail("Please Activate your account", user.getEmail(),"Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));  //(subject, recipient, body)
	}
 

	private String generateVerificationToken(User user) {
		String token=UUID.randomUUID().toString(); //128bit 
		VerificationToken verificationToken=new VerificationToken(); //saving in entity
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		
		verificationTokenRepository.save(verificationToken); //saving token in db
		return token;
	}


	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken=verificationTokenRepository.findByToken(token); // checking token exist or not in repository
		verificationToken.orElseThrow(()->new SpringRedditException("Invalid Token")); // if token is not valid
		fetchUserAndEnable(verificationToken.get());
	}

	@Transactional  // whenever we communicate with db we use transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		String username=verificationToken.getUser().getUsername();
		User user=userRepository.findByUsername(username).orElseThrow(()->new SpringRedditException("UserName : "+username+" not found"));
		user.setEnabled(true); //save in model
		userRepository.save(user); //save or update record in db
	}

}
