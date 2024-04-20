package com.assessment.messaging.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:swagger-ui/index.html";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
