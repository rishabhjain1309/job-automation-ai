package com.automate.AutoJobApply.controller;

import com.automate.AutoJobApply.model.ResumeProfile;
import com.automate.AutoJobApply.service.ResumeParserService;

import org.springframework.web.bind.annotation.*;

@RestController
public class ResumeController {

    private final ResumeParserService resumeParserService;

    public ResumeController(ResumeParserService resumeParserService) {
        this.resumeParserService = resumeParserService;
    }

    @PostMapping("/parse-resume")
    public ResumeProfile parseResume(@RequestBody String resumeText) {
        return resumeParserService.parseResume(resumeText);
    }
}