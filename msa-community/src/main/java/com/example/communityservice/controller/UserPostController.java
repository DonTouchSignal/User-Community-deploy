package com.example.communityservice.controller;

import com.example.communityservice.dto.PostDto;
import com.example.communityservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserPostController {
    private final PostService postService;

    // 내가 작성한 게시글 목록
    @GetMapping("/me/posts")
    public ResponseEntity<Page<PostDto>> getMyPosts(
            @RequestHeader("X-Auth-User") String userEmail,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPostsByUserEmail(userEmail, pageable));
    }

    // 내가 좋아요 누른 게시글 목록
    @GetMapping("/me/liked-posts")
    public ResponseEntity<Page<PostDto>> getMyLikedPosts(
            @RequestHeader("X-Auth-User") String userEmail,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getLikedPosts(userEmail, pageable));
    }
} 