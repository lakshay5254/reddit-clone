package com.lakshay.redditclone.service;

import com.lakshay.redditclone.dto.CommentsDto;
import com.lakshay.redditclone.exception.PostNotFoundException;
import com.lakshay.redditclone.mapper.CommentMapper;
import com.lakshay.redditclone.model.Comment;
import com.lakshay.redditclone.model.NotificationEmail;
import com.lakshay.redditclone.model.Post;
import com.lakshay.redditclone.model.User;
import com.lakshay.redditclone.repository.CommentRepository;
import com.lakshay.redditclone.repository.PostRepository;
import com.lakshay.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CommentService {
    private static final String POST_URL = "";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public void save(CommentsDto commentsDto){
        Post post = postRepository.findById(commentsDto.getPostId()).orElseThrow(()->new PostNotFoundException(commentsDto.getPostId().toString()));
        Comment comment= commentMapper.map(commentsDto,post,authService.getCurrentUser()); //mapping from dto to comment entity, passing current user
        commentRepository.save(comment);
        // sending notification email to author of post
        //building body of email suing mail content builder
        String message=mailContentBuilder.build(post.getUser().getUsername() + "posted a comment on your post." + POST_URL);
        sendCommentNotification(message, post.getUser());

    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }

    public List<CommentsDto> getllCommentsForPost(Long postId) {
        Post post=postRepository.findById(postId).orElseThrow(()->new PostNotFoundException(postId.toString()));//finding post
        return commentRepository.findByPost(post).stream().map(commentMapper::mapToDto).collect(toList()); //finding comments associated with post
    }

    public List<CommentsDto> getAllCommentsForUser(String userName) {
        User user= userRepository.findByUsername(userName).orElseThrow(()->new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user).stream().map(commentMapper::mapToDto).collect(toList());
    }
}
