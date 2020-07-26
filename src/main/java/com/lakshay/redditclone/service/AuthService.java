package com.lakshay.redditclone.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.lakshay.redditclone.dto.RefreshTokenRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshay.redditclone.dto.AuthenticationResponse;
import com.lakshay.redditclone.dto.LoginRequest;
import com.lakshay.redditclone.dto.RegisterRequest;
import com.lakshay.redditclone.exception.SpringRedditException;
import com.lakshay.redditclone.model.NotificationEmail;
import com.lakshay.redditclone.model.User;
import com.lakshay.redditclone.model.VerificationToken;
import com.lakshay.redditclone.repository.UserRepository;
import com.lakshay.redditclone.repository.VerificationTokenRepository;
import com.lakshay.redditclone.security.JwtProvider;

import lombok.AllArgsConstructor;

// logic to register users or creating user objects saving to db, sending activation emails 

@Service
@AllArgsConstructor  // this will autowire all  final fields using constructor
@Transactional
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
	private final MailService mailService;
	private final AuthenticationManager authenticationManager; //interface
	private final JwtProvider jwtProvider;

	private final RefreshTokenService refreshTokenService;
	
	@Transactional(readOnly = true) // to follow rules of db transactions

	public void signup(RegisterRequest registerRequest) {

		User user = new User();

		user.setUsername(registerRequest.getUsername()); //mapping user entity or saving data in db with data entered by user that is stored inside registerRequest
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); //PE present in authserive
		user.setCreated(Instant.now()); //instant.now used to get current time
		user.setEnabled(false); //disable user till authentication
		userRepository.save(user);

		//email verification
		String token = generateVerificationToken(user);
		// now to send these token to user email for verification we need to send html template with it to create it we can use thymleaf add dependecny
		//then create template
		mailService.sendMail(new NotificationEmail("Please Activate your account", user.getEmail(), "Thank you for signing up to Spring Reddit, " +
				"please click on the below url to activate your account : " +
				"http://localhost:8080/api/auth/accountVerification/" + token));  //(subject, recipient, body)
	}


	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString(); //128bit
		VerificationToken verificationToken = new VerificationToken(); //saving in entity
		verificationToken.setToken(token);
		verificationToken.setUser(user);

		verificationTokenRepository.save(verificationToken); //saving token in db
		return token;
	}


	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token); // checking token exist or not in repository
		fetchUserAndEnable(verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token")));
	}

	private void fetchUserAndEnable(VerificationToken verificationToken) {
		String username = verificationToken.getUser().getUsername();
		User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("UserName : " + username + " not found"));
		user.setEnabled(true); //save in model
		userRepository.save(user); //save or update record in db
	}


	public AuthenticationResponse login(LoginRequest loginRequest) {  //logic to authenticate user
		// uses authentication manager to perform login so autowire it,,as it is interface so we need to specify which bean to create as there are multiple implementations of it, create inside security config
		// when we autowire AM spring finds that bean and inject in our class
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())); //pass username pass in login request object as a constructor arguments
		//to create jwt tokens add dependecy jjwt-api,jwwt-impl, jjwt-jackson and create key handling class 
		// storing authentication object inside security context
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		//to check if user is logged in or not such check for authentication object inside security context
		String token = jwtProvider.generateToken(authenticate);
		// now we can send this token back to user, to send it we will use DTO authentication response class

		return AuthenticationResponse.builder()
				.authenticationToken(token)
				.refreshToken(refreshTokenService.generateRefreshToken().getToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis())) //getting expiration time from jwtprovider then calculating instant from timestamp
				.username(loginRequest.getUsername())
				.build(); // when login we get one  refresh token, jwt token, expiration date

		
	}

	public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
		//refresh token esrvice class responsible for create delete update refresh tokens
		refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken()); //if not validate then runtime exception
		String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername()); //as when jwt expires no user info presnt in security context  to generate token we use another method  rathen then generateToken()
		return AuthenticationResponse.builder()
				.authenticationToken(token)
				.refreshToken(refreshTokenRequest.getRefreshToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
				.username(refreshTokenRequest.getUsername())
				.build();
	}


	

	@Transactional(readOnly = true)
	public User getCurrentUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
				getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
	}

}

