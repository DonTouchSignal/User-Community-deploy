package com.example.user.controller;

import com.example.user.service.SubsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
    @Autowired
    private SubsService subsService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestHeader("X-Auth-User") String email) {
        try {
            subsService.subscribe(email);
            return ResponseEntity.ok("구독 처리가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("구독 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestHeader("X-Auth-User") String email) {
        try {
            subsService.unsubscribe(email);
            return ResponseEntity.ok("구독 취소가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("구독 취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkSubscriptionStatus(@RequestHeader("X-Auth-User") String email) {
        try {
            boolean isSubscribed = subsService.isSubscribed(email);
            return ResponseEntity.ok(isSubscribed);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
} 