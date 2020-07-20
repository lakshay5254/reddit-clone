package com.lakshay.redditclone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshay.redditclone.dto.SubredditDto;
import com.lakshay.redditclone.model.Subreddit;
import com.lakshay.redditclone.repository.SubredditRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {
	//create and save subreddit information
	private final SubredditRepository subredditRepository ;
	
	@Transactional
	public SubredditDto save(SubredditDto subredditDto) {
		//mapping information from dto to subreddit entity
		Subreddit save=subredditRepository.save(mapSubredditDto(subredditDto));
		subredditDto.setId(save.getId()); // setting id in dto
		return subredditDto;
	}

	private Subreddit mapSubredditDto(SubredditDto subredditDto) {
		//using builder pattern to construct subreddit entity
		// we can also use smapping libraries like mapstruct inplace of builder
		return Subreddit.builder().name(subredditDto.getName())
				.description(subredditDto.getDescription())
				.build();
	}
	
	
    @Transactional(readOnly = true) //in relational db to ensure consistency 
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(this::mapToDto) //mapping subreddit entity to dto
                .collect(Collectors.toList());
    }

    private SubredditDto mapToDto(Subreddit subreddit) {
		return SubredditDto.builder().name(subreddit.getName())
				.id(subreddit.getId())
				.numberOfPosts(subreddit.getPosts().size())
				.build();
				
	}

	


}
