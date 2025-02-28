package com.example.user.controller;

import com.example.user.service.SubsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// 광고 관련 REST API
@RestController
@RequestMapping("/api/ads")
public class AdsRestController {

    @Autowired
    private SubsService subsService;

    // 구독 여부에 따라 광고를 표시할지 여부
    // @param username 사용자의 이름
    @GetMapping("/show")
    public ResponseEntity<Boolean> shouldShowAds(@RequestHeader("X-Auth-User") String email) {
        boolean showAds = !subsService.isSubscribed(email);
        return ResponseEntity.ok(showAds);
    }

    // 사용자별 맞춤 광고 제공
    @GetMapping("/popup")
    public ResponseEntity<Map<String, String>> getPopupAd(@RequestHeader("X-Auth-User") String email) {
        Map<String, String> ad = new HashMap<>();

        if (subsService.isSubscribed(email)) {
            // 구독자는 광고를 보지 않음
            return ResponseEntity.ok(ad);
        } else {
            ad.put("title", "특가 할인 이벤트!");
            ad.put("content", "오늘 하루 50% 할인 쿠폰을 받아가세요!");
            ad.put("imageUrl", "https://example.com/ad-image.jpg");
            ad.put("link", "https://example.com/promotion");
        }

        return ResponseEntity.ok(ad);
    }
}
