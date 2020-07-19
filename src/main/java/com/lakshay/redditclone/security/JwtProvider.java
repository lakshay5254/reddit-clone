package com.lakshay.redditclone.security;

import com.lakshay.redditclone.exception.SpringRedditException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;



@Service
public class JwtProvider {
	
	 private KeyStore keyStore;
	    
	    

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
				.signWith(getPrivateKey()) //to sign token we are providing private key of the keystore, we are using symmetric encryption of jwt 
				.compact();
	}
	
	private PrivateKey getPrivateKey() { //method to pass keys  
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray()); //to read key(alias of keystore, password)
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore",e);
        }
    }

}
