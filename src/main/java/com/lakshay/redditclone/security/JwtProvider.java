package com.lakshay.redditclone.security;

import com.lakshay.redditclone.exception.SpringRedditException;


import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.crypto.Data;

import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;


import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;


@Service
public class JwtProvider {
	
	 private KeyStore keyStore;
	 //for refresh token setting expiration time , set this in properties
	@Value("${jwt.expiration.time}")
	private Long jwtExpirationInMillis;
	    

	    @PostConstruct
	    public void init() {  //initializing the key
	        try {
	            keyStore = KeyStore.getInstance("JKS");  //getting jks instance of keystore
	            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks"); //getting input stream from keystore file
	            keyStore.load(resourceAsStream, "secret".toCharArray()); //provde input stream with password of keystore, here pass = "secret" 
	        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {  
	            throw new SpringRedditException("Exception occurred while loading keystore",e);
	        }

	    }
	
	
	public String generateToken(Authentication authentication) {
		User principle=(User)authentication.getPrincipal(); //cast to userdetails.User
		return Jwts.builder() //using Jwts class to create jwt token in string 
				.setSubject(principle.getUsername()) //setting username as subject
				.setIssuedAt(from(Instant.now()))
				.signWith(getPrivateKey()) //to sign token we are providing private key of the keystore, we are using asymmetric encryption of jwt private public key
				.setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis))) //converting long to an instant object then use date.from to convert it into date object
				.compact();
	}

	// jwt regenerate method with subject or with username
	public String generateTokenWithUserName(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(from(Instant.now()))
				.signWith(getPrivateKey())
				.setExpiration(java.sql.Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
				.compact();
	}

	private PrivateKey getPrivateKey() { //method to pass keys  
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray()); //to read key(alias of keystore, password)
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore",e);
        }
    }
	
	//validating jwt token
	public boolean validateToken(String jwt) {
		// we used private key from keystore to sign in now we will use public key to validate it 
		parser().setSigningKey(getPublickey()).parseClaimsJws(jwt);
        return true;
	}
    


	private PublicKey getPublickey() {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occured while " +
                    "retrieving public key from keystore", e);
        }
    }
	
	public String getUsernameFromJwt(String token) {
        Claims claims = parser()  //claims is the object used to store token body
                .setSigningKey(getPublickey())
                .parseClaimsJws(token)
                .getBody();

		return claims.getSubject();  //we set subject as username
    }

    public Long getJwtExpirationInMillis(){
	    	return jwtExpirationInMillis;
	}

}
