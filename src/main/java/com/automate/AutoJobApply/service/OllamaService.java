package com.automate.AutoJobApply.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    @Value("${groq.api.key}")
    private String apiKey;

    public String callGroq(String prompt) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "llama-3.1-8b-instant");

            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            requestMap.put("messages", new Object[]{message});

            String requestBody = mapper.writeValueAsString(requestMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling AI";
        }
    }
    public String generateEmail(String jobTitle, String company) {

        String prompt = """
    Write a short professional cold email for job application.

    Candidate: Java Backend Developer (2 years experience)
    Skills: Java, Spring Boot, Kafka, Microservices

    Job: %s at %s

    Keep it short and impactful.
    """.formatted(jobTitle, company);

        return callGroq(prompt);
    }

}