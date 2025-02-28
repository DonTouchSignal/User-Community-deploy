package com.example.communityservice.repository;

import com.example.communityservice.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    Optional<PostLike> findByPostIdAndUserEmail(Integer postId, String userEmail);
    boolean existsByPostIdAndUserEmail(Integer postId, String userEmail);
    void deleteByPostIdAndUserEmail(Integer postId, String userEmail);
    void deleteByPostId(Integer postId);
} 