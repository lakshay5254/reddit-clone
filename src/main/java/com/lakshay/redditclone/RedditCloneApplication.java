package com.lakshay.redditclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync  // so process independent can run faster, alternate we can use message queues like rabbitMQ or Active MQ as they provide reliability, heavy weight to our use case  
public class RedditCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedditCloneApplication.class, args);
	}
}
