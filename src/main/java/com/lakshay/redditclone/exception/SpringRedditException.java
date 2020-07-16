package com.lakshay.redditclone.exception;
@SuppressWarnings("serial")
public class SpringRedditException extends RuntimeException {
	public SpringRedditException(String exMessage) {
		super(exMessage);
	}// custom exception for passing our own exception message

}
