package com.automate.AutoJobApply.controller;

import com.automate.AutoJobApply.model.*;
import com.automate.AutoJobApply.service.*;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class JobController {

    private final ResumeParserService resumeParserService;
    private final JobMatchingService jobMatchingService;
    private final JobService jobService;
    private final ExcelService excelService;

    public JobController(ResumeParserService resumeParserService,
                         JobMatchingService jobMatchingService,
                         JobService jobService,
                         ExcelService excelService) {
        this.resumeParserService = resumeParserService;
        this.jobMatchingService = jobMatchingService;
        this.jobService = jobService;
        this.excelService = excelService;
    }

    @PostMapping("/run-job-search")
    public String run(@RequestBody String resumeText) {

        ResumeProfile profile = resumeParserService.parseResume(resumeText);

        List<Job> jobs = jobService.getJobs();

        List<Job> filteredJobs = new ArrayList<>();
        List<JobMatchResult> filteredResults = new ArrayList<>();

        // ✅ MATCH + FILTER
        for (Job job : jobs) {

            JobMatchResult result = jobMatchingService.match(profile, job.getDescription());

            if (result.getScore() >= 60) { // only good jobs
                filteredJobs.add(job);
                filteredResults.add(result);
            }
        }

        // ✅ SORT (highest score first)
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < filteredResults.size(); i++) {
            indexes.add(i);
        }

        indexes.sort((a, b) ->
                filteredResults.get(b).getScore() - filteredResults.get(a).getScore()
        );

        List<Job> sortedJobs = new ArrayList<>();
        List<JobMatchResult> sortedResults = new ArrayList<>();

        for (int i : indexes) {
            sortedJobs.add(filteredJobs.get(i));
            sortedResults.add(filteredResults.get(i));
        }

        // ✅ GENERATE EXCEL WITH SORTED DATA
        excelService.generateExcel(sortedJobs, sortedResults);

        return "Excel generated successfully!";
    }


    @PostMapping("/match-job")
    public JobMatchResult matchJob(@RequestBody Map<String, String> request) {

        String resumeText = request.get("resume");
        String jobDescription = request.get("jobDescription");

        ResumeProfile profile = resumeParserService.parseResume(resumeText);

        return jobMatchingService.match(profile, jobDescription);
    }


}