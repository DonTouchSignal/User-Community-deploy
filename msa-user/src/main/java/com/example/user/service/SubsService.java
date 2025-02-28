package com.example.user.service;

import com.example.user.entities.SubsEntity;
import com.example.user.repository.SubsRepository;
import com.example.user.repository.UserRepository;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class SubsService {
    @Autowired
    private SubsRepository subsRepository;

    public boolean isSubscribed(String email) {
        SubsEntity subsEntity = subsRepository.findByEmail(email);
        if (subsEntity == null) {
            return false;
        }
        // 구독 상태 확인
        return subsEntity.isSubscriptionStatus();
    }

    @Transactional
    public void subscribe(String email) {
        SubsEntity subsEntity = subsRepository.findByEmail(email);
        if (subsEntity == null) {
            subsEntity = new SubsEntity();
            subsEntity.setEmail(email);
        }
        subsEntity.setSubscriptionStatus(true);
        subsRepository.save(subsEntity);
    }

    @Transactional
    public void unsubscribe(String email) {
        SubsEntity subsEntity = subsRepository.findByEmail(email);
        if (subsEntity != null) {
            subsEntity.setSubscriptionStatus(false);
            subsRepository.save(subsEntity);
        } else {
            throw new IllegalArgumentException("구독 정보를 찾을 수 없습니다.");
        }
    }
}
