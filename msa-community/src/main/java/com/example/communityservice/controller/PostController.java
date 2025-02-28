package com.example.communityservice.controller;

import com.example.communityservice.dto.PostDto;
import com.example.communityservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets/{assetId}/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @PathVariable String assetId,
            @RequestHeader("X-Auth-User") String userEmail,
            @RequestHeader("Authorization") String accessToken,
            @RequestBody PostDto postDto) {
        return ResponseEntity.ok(postService.createPost(assetId, userEmail, postDto, accessToken));
    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<Page<PostDto>> getPosts(
            @PathVariable String assetId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(assetId, pageable));
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(
            @PathVariable String assetId,
            @PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getPost(postId, assetId));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer postId,
            @RequestHeader("X-Auth-User") String userEmail) {
        postService.deletePost(postId, userEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Integer postId,
            @RequestHeader("X-Auth-User") String userEmail) {
        postService.likePost(postId, userEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Integer postId,
            @RequestHeader("X-Auth-User") String userEmail) {
        postService.unlikePost(postId, userEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/like")
    public ResponseEntity<Boolean> hasLiked(
            @PathVariable Integer postId,
            @RequestHeader("X-Auth-User") String userEmail) {
        return ResponseEntity.ok(postService.hasLiked(postId, userEmail));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Integer postId,
            @RequestHeader("X-Auth-User") String userEmail,
            @RequestBody PostDto postDto) {
        return ResponseEntity.ok(postService.updatePost(postId, userEmail, postDto));
    }
}
