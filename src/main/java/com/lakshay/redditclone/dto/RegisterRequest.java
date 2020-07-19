package com.lakshay.redditclone.dto;

import lombok.AllArgsConstructor;
// it stores all data entered by user 
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	private String email;
	private String username;
	private String password;
}
