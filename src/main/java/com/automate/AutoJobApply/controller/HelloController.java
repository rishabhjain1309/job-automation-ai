//package com.automate.AutoJobApply.controller;
//
//import com.automate.AutoJobApply.service.OllamaService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class HelloController {
//
//    private final OllamaService ollamaService;
//
//    public HelloController(OllamaService ollamaService) {
//        this.ollamaService = ollamaService;
//    }
//
//    @GetMapping("/ai")
//    public String askAI(@RequestParam String prompt) {
//        return ollamaService.callGroq(prompt);
//    }
//}