package com.lakshay.redditclone.repository;

import com.lakshay.redditclone.model.Post;
import com.lakshay.redditclone.model.User;
import com.lakshay.redditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
