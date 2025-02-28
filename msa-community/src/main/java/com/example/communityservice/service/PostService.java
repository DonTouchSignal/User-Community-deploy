package com.example.communityservice.service;

import com.example.communityservice.dto.PostDto;
import com.example.communityservice.entity.Post;
import com.example.communityservice.entity.PostLike;
import com.example.communityservice.repository.PostLikeRepository;
import com.example.communityservice.repository.PostRepository;
import com.example.communityservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final PostViewService postViewService;
    private final WebClient webClient;

    // 게시글 생성
    @Transactional
    public PostDto createPost(String assetId, String userEmail, PostDto postDto, String accessToken) {
        // User 서비스에서 닉네임 조회
        String userNickname = webClient.get()
            .uri("/user/nickname")
            .header("X-Auth-User", userEmail)
            .header("Authorization", accessToken)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        Post post = Post.builder()
                .userEmail(userEmail)
                .userNickname(userNickname)
                .assetId(assetId)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .imageUrl(postDto.getImageUrl())
                .build();
        
        return new PostDto(postRepository.save(post));
    }

    // 게시글 목록 조회
    public Page<PostDto> getPosts(String assetId, Pageable pageable) {
        Page<Post> posts = postRepository.findByAssetIdOrderByCreatedAtDesc(assetId, pageable);
        
        return posts.map(post -> {
            PostDto postDto = new PostDto(post);
            // Redis에서 실제 조회수 가져오기
            postDto.setViewCount(postViewService.getViewCount(post.getId()));
            return postDto;
        });
    }

    // 게시글 상세 조회
    public PostDto getPost(Integer postId, String assetId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        
        // 요청된 assetId와 게시글의 assetId가 일치하는지 확인
        if (!assetId.equals(post.getAssetId())) {
            throw new IllegalArgumentException("해당 자산의 게시글이 아닙니다.");
        }
        
        // Redis 조회수 처리
        postViewService.initializeViewCount(postId, post.getViewCount());
        int currentViewCount = postViewService.increaseAndGetViewCount(postId);
        
        PostDto postDto = new PostDto(post);
        postDto.setViewCount(currentViewCount);
        
        return postDto; 
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Integer postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        
        if (!post.getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
        }
        
        // 1. 게시글의 댓글 삭제
        commentRepository.deleteByPostId(postId);
        
        // 2. 게시글의 좋아요 삭제
        postLikeRepository.deleteByPostId(postId);
        
        // 3. Redis의 조회수 데이터 삭제
        postViewService.deleteViewCount(postId);
        
        // 4. 게시글 삭제
        postRepository.delete(post);
    }

    @Transactional
    public void likePost(Integer postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        boolean exists = postLikeRepository.existsByPostIdAndUserEmail(postId, userEmail);
        if (exists) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시글입니다.");
        }

        PostLike postLike = PostLike.builder()
                .postId(postId)
                .userEmail(userEmail)
                .build();

        postLikeRepository.save(postLike);
        post.increaseLikeCount();
    }

    @Transactional
    public void unlikePost(Integer postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        PostLike postLike = postLikeRepository.findByPostIdAndUserEmail(postId, userEmail)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 게시글입니다."));

        postLikeRepository.delete(postLike);
        post.decreaseLikeCount();
    }

    public boolean hasLiked(Integer postId, String userEmail) {
        return postLikeRepository.existsByPostIdAndUserEmail(postId, userEmail);
    }

    @Transactional
    public PostDto updatePost(Integer postId, String userEmail, PostDto postDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        
        if (!post.getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다.");
        }
        
        post.update(postDto.getTitle(), postDto.getContent(), postDto.getImageUrl());
        
        return new PostDto(post);
    }

    // 특정 사용자가 작성한 게시글 목록
    public Page<PostDto> getPostsByUserEmail(String userEmail, Pageable pageable) {
        return postRepository.findByUserEmailOrderByCreatedAtDesc(userEmail, pageable)
                .map(post -> {
                    PostDto dto = new PostDto(post);
                    dto.setViewCount(postViewService.getViewCount(post.getId()));
                    return dto;
                });
    }

    // 특정 사용자가 좋아요 누른 게시글 목록
    public Page<PostDto> getLikedPosts(String userEmail, Pageable pageable) {
        return postRepository.findLikedPostsByUserEmail(userEmail, pageable)
                .map(post -> {
                    PostDto dto = new PostDto(post);
                    dto.setViewCount(postViewService.getViewCount(post.getId()));
                    return dto;
                });
    }
}
