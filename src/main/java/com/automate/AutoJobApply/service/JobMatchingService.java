package com.automate.AutoJobApply.service;

import com.automate.AutoJobApply.model.ResumeProfile;
import com.automate.AutoJobApply.model.JobMatchResult;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobMatchingService {

    public JobMatchResult match(ResumeProfile resume, String jobDescription) {

        Set<String> resumeSkills = resume.getSkills()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<String> jdWords = Arrays.stream(jobDescription.toLowerCase().split("\\W+"))
                .collect(Collectors.toSet());

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : resumeSkills) {
            if (jobDescription.toLowerCase().contains(skill.toLowerCase())) {
                matched.add(skill);
            } else {
                missing.add(skill);
            }
        }

        int score = matched.size() * 20; // each important skill = 20%
        if (score > 100) score = 100;

        String decision = score >= 60 ? "APPLY" : "SKIP";



        JobMatchResult result = new JobMatchResult();
        result.setScore(score);
        result.setMatched_skills(matched);
        result.setMissing_skills(missing);
        result.setDecision(decision);

        return result;
    }
}