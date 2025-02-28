package com.example.user.repository;

import com.example.user.entities.SubsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubsRepository extends JpaRepository<SubsEntity, Long> {
    SubsEntity findByEmail(String email);
}
