package com.lakshay.redditclone.service;

import com.lakshay.redditclone.dto.PostRequest;
import com.lakshay.redditclone.dto.PostResponse;
import com.lakshay.redditclone.exception.PostNotFoundException;
import com.lakshay.redditclone.exception.SubredditNotFoundException;
import com.lakshay.redditclone.mapper.PostMapper;
import com.lakshay.redditclone.model.Post;
import com.lakshay.redditclone.model.Subreddit;
import com.lakshay.redditclone.model.User;
import com.lakshay.redditclone.repository.PostRepository;
import com.lakshay.redditclone.repository.SubredditRepository;
import com.lakshay.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final SubredditRepository subredditRepository;
    private final AuthService authService;
    private final PostMapper postMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    public Post save(PostRequest postRequest) {
        // mapping from postRequest to post entity created in mapper
        //retreiving subreddit from repository
        Subreddit subreddit=subredditRepository.findByName(postRequest.getSubredditName()).orElseThrow(()->new SubredditNotFoundException(postRequest.getSubredditName()));
        User currentUser = authService.getCurrentUser();// getting user details

        return postMapper.map(postRequest,subreddit,currentUser);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));
        return postMapper.mapToDto(post);
    }
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        //now going to each post inside posts list using stream then mapping post object to postresponsedto then collecting it into a list
        return posts.stream().map(postMapper::mapToDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }


}
