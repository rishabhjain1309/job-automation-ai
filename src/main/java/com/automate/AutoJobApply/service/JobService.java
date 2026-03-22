package com.automate.AutoJobApply.service;

import com.automate.AutoJobApply.model.Job;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class JobService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public List<Job> getJobs() {

        List<Job> jobs = new ArrayList<>();

        jobs.addAll(getRemotiveJobs());
        jobs.addAll(getRemoteOkJobs());

        return jobs;
    }

    // ================= REMOTIVE =================
    private List<Job> getRemotiveJobs() {

        List<Job> jobs = new ArrayList<>();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://remotive.com/api/remote-jobs?search=java"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            JsonNode jobList = root.path("jobs");

            for (JsonNode node : jobList) {

                Job job = new Job();
                job.setTitle(node.path("title").asText());
                job.setCompany(node.path("company_name").asText());
                job.setDescription(node.path("description").asText());
                job.setUrl(node.path("url").asText());

                if (isRelevant(job)) {
                    jobs.add(job);
                }

                if (jobs.size() >= 10) break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobs;
    }

    // ================= REMOTE OK =================
    private List<Job> getRemoteOkJobs() {

        List<Job> jobs = new ArrayList<>();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://remoteok.com/api"))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());

            for (JsonNode node : root) {

                Job job = new Job();

                job.setTitle(node.path("position").asText());
                job.setCompany(node.path("company").asText());
                job.setDescription(node.path("description").asText());
                job.setUrl(node.path("url").asText());

                // skip empty entries
                if (job.getTitle() == null || job.getTitle().isEmpty()) continue;

                if (isRelevant(job)) {
                    jobs.add(job);
                }

                if (jobs.size() >= 10) break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobs;
    }

    // ================= FILTER =================
    private boolean isRelevant(Job job) {

        String text = (job.getTitle() + " " + job.getDescription()).toLowerCase();

        // must have backend/java
        boolean mustHave = text.contains("java")
                || text.contains("spring")
                || text.contains("software")
                || text.contains("developer")
                || text.contains("backend");

        // reject unwanted roles
        boolean reject = text.contains("react")
                || text.contains("frontend")
                || text.contains("shopify")
                || text.contains("rails")
                || text.contains("support")
                || text.contains("manager")
                || text.contains("product");

        return mustHave && !reject;
    }
}