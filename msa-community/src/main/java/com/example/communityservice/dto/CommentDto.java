package com.example.communityservice.dto;

import com.example.communityservice.entity.Comment;
import com.example.communityservice.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Setter
public class CommentDto {
    private Integer id;
    private Integer postId;
    private String userEmail;
    private String userNickname;
    private String content;
    private LocalDateTime createdAt;

    // Entity -> DTO
    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.userEmail = comment.getUserEmail();
        this.userNickname = comment.getUserNickname();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }

    // DTO -> Entity
    public Comment toEntity(Post post) {
        return Comment.builder()
                .post(post)
                .userEmail(userEmail)
                .userNickname(userNickname)
                .content(content)
                .build();
    }
}
