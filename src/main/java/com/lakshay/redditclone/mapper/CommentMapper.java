package com.lakshay.redditclone.mapper;

import com.lakshay.redditclone.dto.CommentsDto;
import com.lakshay.redditclone.model.Comment;
import com.lakshay.redditclone.model.Post;
import com.lakshay.redditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true) //id will be auto generated so ignore
    @Mapping(target = "text", source = "commentsDto.text") //comment.text=commentDto.text
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())") // we are mentioning java.time.instant cus when mapstruct will create implementation class it will not include import sttements
    @Mapping(target = "post", source = "post") //
    @Mapping(target = "user", source = "user")
    Comment map(CommentsDto commentsDto, Post post, User user);

    @Mapping(target = "postId", expression = "java(comment.getPost().getPostId())") // postId= comment.Post.postId
    @Mapping(target = "userName", expression = "java(comment.getUser().getUsername())")
    CommentsDto mapToDto(Comment comment);
}
