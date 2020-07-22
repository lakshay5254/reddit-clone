
package com.lakshay.redditclone.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshay.redditclone.dto.SubredditDto;
import com.lakshay.redditclone.exception.SpringRedditException;
import com.lakshay.redditclone.mapper.SubredditMapper;
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
	private final SubredditMapper subredditMapper;
	
	
	@Transactional
	public SubredditDto save(SubredditDto subredditDto) {
		//mapping information from dto to subreddit entity
		Subreddit save=subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
		subredditDto.setId(save.getId()); // setting id in dto
		return subredditDto;
	}

//	private Subreddit mapSubredditDto(SubredditDto subredditDto) {
//		//using builder pattern to construct subreddit entity
//		// we can also use smapping libraries like mapstruct inplace of builder
//		return Subreddit.builder().name(subredditDto.getName())
//				.description(subredditDto.getDescription())
//				.build();
//	}
	
	
    @Transactional(readOnly = true) //in relational db to ensure consistency 
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(subredditMapper::mapSubredditToDto) //mapping subreddit entity to dto
                .collect(toList());
    }

	public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with ID - " + id));
        return subredditMapper.mapSubredditToDto(subreddit);
		
	}
    
    

//    private SubredditDto mapToDto(Subreddit subreddit) {
//		return SubredditDto.builder().name(subreddit.getName())
//				.id(subreddit.getId())
//				.numberOfPosts(subreddit.getPosts().size())
//				.build();
//				
//	}

	


}
