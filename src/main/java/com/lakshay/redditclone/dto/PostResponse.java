package com.lakshay.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String postName;
    private String url;
    private String description;
    private String userName;
    private String subredditName;
    private Integer voteCount; //no of votes for a post
    private Integer commentCount;
    private String duration; //post duration from creation finding using time ago library(github marlonlom)


}
