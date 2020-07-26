package com.lakshay.redditclone.service;


import com.lakshay.redditclone.dto.VoteDto;
import com.lakshay.redditclone.exception.PostNotFoundException;
import com.lakshay.redditclone.exception.SpringRedditException;
import com.lakshay.redditclone.model.Post;
import com.lakshay.redditclone.model.Vote;
import com.lakshay.redditclone.repository.PostRepository;
import com.lakshay.redditclone.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.lakshay.redditclone.model.VoteType.UPVOTE;


@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        //as find by method returns optional we are using orelsethrow to throw exception
        Post post = postRepository.findById(voteDto.getPostId()) //retrieving post
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + voteDto.getPostId()));
        //finding recent vote submitted by user
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        //checking user can vote once
        if (voteByPostAndUser.isPresent() &&
                voteByPostAndUser.get().getVoteType()
                        .equals(voteDto.getVoteType())) { //if existing vote = requested vote throw exception
            throw new SpringRedditException("You have already "
                    + voteDto.getVoteType() + "'d for this post");
        }
        //if new vote then subtract or add to vote count
        if (UPVOTE.equals(voteDto.getVoteType())) { //votetype.UPVOTE=votedto.voteType
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }
        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }
//for small mapping logic better to create such small functions
    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()  //used to construct the object of vote model
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
