package com.example.communityservice.controller;

import com.example.communityservice.dto.CommentDto;
import com.example.communityservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Integer postId,
            @RequestHeader("X-Auth-User") String userEmail,
            @RequestHeader("Authorization") String accessToken,
            @RequestBody CommentDto commentDto) {
        commentDto.setUserEmail(userEmail);
        return ResponseEntity.ok(commentService.createComment(postId, commentDto, accessToken));
    }

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<Page<CommentDto>> getComments(
            @PathVariable Integer postId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getComments(postId, pageable));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer commentId,
            @RequestHeader("X-Auth-User") String userEmail) {
        commentService.deleteComment(commentId, userEmail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Integer commentId,
            @RequestHeader("X-Auth-User") String userEmail,
            @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(commentService.updateComment(commentId, userEmail, commentDto));
    }
}
