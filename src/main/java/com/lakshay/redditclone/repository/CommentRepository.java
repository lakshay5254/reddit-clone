package com.lakshay.redditclone.repository;

import com.lakshay.redditclone.model.Comment;
import com.lakshay.redditclone.model.Post;
import com.lakshay.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
