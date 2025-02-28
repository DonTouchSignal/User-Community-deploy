package com.example.communityservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String userEmail;
    private String userNickname;
    
    @Column(nullable = false)
    private String assetId;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String content;
    
    private String imageUrl;
    
    private int likeCount;
    
    @Column(columnDefinition = "integer default 0")
    private int viewCount;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.likeCount = 0;
        this.viewCount = 0;
    }
    
    @Builder
    public Post(String userEmail, String userNickname, String assetId, String title, String content, String imageUrl) {
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.assetId = assetId;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = 0;
        this.viewCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void update(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}
