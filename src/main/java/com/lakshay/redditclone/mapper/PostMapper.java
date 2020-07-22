package com.lakshay.redditclone.mapper;

import com.lakshay.redditclone.dto.PostRequest;
import com.lakshay.redditclone.dto.PostResponse;
import com.lakshay.redditclone.model.Post;
import com.lakshay.redditclone.model.Subreddit;
import com.lakshay.redditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())") //Post.createdDate = current time
    @Mapping(target = "description", source = "postRequest.description") //description = postRequest.description
    //creating Post object from postreq dto, subreddit and user entity
    Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId") //postres.id=post.postid
    @Mapping(target = "subredditName", source = "subreddit.name") //postres.subredditname= post.subreddit.name
    @Mapping(target = "userName", source = "user.username") // username= post.user.username
    //Mapping entity to PostResponse dto
    PostResponse mapToDto(Post post);

}
