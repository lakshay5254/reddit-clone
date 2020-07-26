package com.lakshay.redditclone.mapper;


import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.lakshay.redditclone.dto.PostRequest;
import com.lakshay.redditclone.dto.PostResponse;
import com.lakshay.redditclone.model.*;
import com.lakshay.redditclone.repository.CommentRepository;
import com.lakshay.redditclone.repository.VoteRepository;
import com.lakshay.redditclone.service.AuthService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;



@Mapper(componentModel = "spring")
public abstract class PostMapper { // as new fileds need to be defined and we cant do it in interface

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;

    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())") //Post.createdDate = current time
    @Mapping(target = "description", source = "postRequest.description") //description = postRequest.description
    @Mapping(target = "user", source = "user")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "voteCount", constant = "0") //when saving a post default vote count is 0

        //creating Post object from postreq dto, subreddit and user entity
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId") //postres.id=post.postid
    @Mapping(target = "subredditName", source = "subreddit.name") //postres.subredditname= post.subreddit.name
    @Mapping(target = "userName", source = "user.username") // username= post.user.username
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")

    //Mapping entity to PostResponse dto
    public abstract PostResponse mapToDto(Post post);

    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    String getDuration(Post post) {
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }




}
