package com.example.communityservice.repository;

import com.example.communityservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByPostId(Integer postId, Pageable pageable);
    void deleteByPostId(Integer postId);
    List<Comment> findByPostIdOrderByCreatedAtDesc(Integer postId);
}
