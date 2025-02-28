package com.example.communityservice.repository;

import com.example.communityservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findByAssetIdOrderByCreatedAtDesc(String assetId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.viewCount = :viewCount WHERE p.id = :postId")
    void updateViewCount(@Param("postId") Integer postId, @Param("viewCount") int viewCount);

    // 특정 사용자가 작성한 게시글 찾기
    Page<Post> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);

    // 특정 사용자가 좋아요 누른 게시글 찾기
    @Query("SELECT p FROM Post p " +
           "JOIN PostLike pl ON p.id = pl.postId " +
           "WHERE pl.userEmail = :userEmail " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findLikedPostsByUserEmail(@Param("userEmail") String userEmail, Pageable pageable);
}
