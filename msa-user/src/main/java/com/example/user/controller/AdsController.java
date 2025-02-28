package com.example.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdsController {

    @GetMapping("/ads")
    public String showAdsPage() {
        return "ads"; // static/ads.html 파일을 반환
    }
}