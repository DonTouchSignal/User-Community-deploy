package com.example.communityservice.dto;

import com.example.communityservice.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Setter
public class PostDto {
    private Integer id;
    private String userEmail;
    private String userNickname;
    private String assetId;
    private String title;
    private String content;
    private String imageUrl;
    private int likeCount;
    private int viewCount;
    private LocalDateTime createdAt;

    // Entity -> DTO
    public PostDto(Post post) {
        this.id = post.getId();
        this.userEmail = post.getUserEmail();
        this.userNickname = post.getUserNickname();
        this.assetId = post.getAssetId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
        this.likeCount = post.getLikeCount();
        this.viewCount = post.getViewCount();
        this.createdAt = post.getCreatedAt();
    }

    // DTO -> Entity
    public Post toEntity() {
        return Post.builder()
                .userEmail(userEmail)
                .userNickname(userNickname)
                .assetId(assetId)
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .build();
    }
}
