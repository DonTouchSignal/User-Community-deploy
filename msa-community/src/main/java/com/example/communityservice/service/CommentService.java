package com.example.communityservice.service;

import com.example.communityservice.dto.CommentDto;
import com.example.communityservice.entity.Comment;
import com.example.communityservice.entity.Post;
import com.example.communityservice.repository.CommentRepository;
import com.example.communityservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final WebClient webClient;

    // 댓글 작성
    @Transactional
    public CommentDto createComment(Integer postId, CommentDto commentDto, String accessToken) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        
        // User 서비스에서 닉네임 조회
        String userNickname = webClient.get()
            .uri("/user/nickname")
            .header("X-Auth-User", commentDto.getUserEmail())
            .header("Authorization", accessToken)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        Comment comment = Comment.builder()
                .post(post)
                .userEmail(commentDto.getUserEmail())
                .userNickname(userNickname)
                .content(commentDto.getContent())
                .build();

        return new CommentDto(commentRepository.save(comment));
    }

    // 게시글의 댓글 목록 조회
    public Page<CommentDto> getComments(Integer postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);
        return comments.map(CommentDto::new);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Integer commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        
        if (!comment.getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }
        
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentDto updateComment(Integer commentId, String userEmail, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        
        if (!comment.getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }
        
        comment.update(commentDto.getContent());
        return new CommentDto(comment);
    }
}
