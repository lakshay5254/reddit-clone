package com.lakshay.redditclone.model;

//generate posts
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;



@Data  // creates getters and setters
@Entity // jpa for declaring entity
@Builder // generates builders for class uses builder pattern to create objects intitialize them 
@AllArgsConstructor  //all-args constructor requires one argument for every field in the class
@NoArgsConstructor  // will generate a constructor with no parameters,,  RequiredArgsConstructor generates a constructor with 1 parameter for each field that are  non-initialized final fields get a parameter, as well as any fields that are marked as @NonNull
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long postId;
    @NotBlank(message = "Post Name cannot be empty or Null")
    private String postName;
    @Nullable  //elements can be null,,  @NotNull
    private String url;
    @Nullable
    @Lob  // used to store large chunks of data in db
    private String description;
    private Integer voteCount = 0;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;
    private Instant createdDate;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Subreddit subreddit;
	
}
