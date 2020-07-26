package com.lakshay.redditclone.mapper;
import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
//java library helps in mapping data between two objects easily
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lakshay.redditclone.dto.SubredditDto;
import com.lakshay.redditclone.model.Post;
import com.lakshay.redditclone.model.Subreddit;

@Mapper(componentModel="spring")
public interface SubredditMapper {
	
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))") //telling mapstruct to use mapPosts method for mapping posts field  
    SubredditDto mapSubredditToDto(Subreddit subreddit); //takes subreddit entity object return subreddit dto object
    //mapstruct automatically identifies similar name fields and map them 
    // for not similar like here in entity we have list<> posts and in dto we have number of posts to map them we use meth
    
    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();  // returning size of list
    }
    
    //for inverse mapping or mapping dto to entity
    @InheritInverseConfiguration
    @Mapping(target="posts",ignore=true) //ignoring posts field as we will set it when we will be creating posts
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")

    Subreddit mapDtoToSubreddit(SubredditDto subredditDto);
} // now we can replace builder methods with mapper methods 

	
