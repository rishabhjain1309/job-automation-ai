package com.automate.AutoJobApply.service;

import com.automate.AutoJobApply.model.ResumeProfile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ResumeParserService {

    private final OllamaService ollamaService;

    public ResumeParserService(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    public ResumeProfile parseResume(String resumeText) {

        String prompt = """
            You are an expert resume parser.
            
            Extract structured JSON from the resume.
            
            STRICT RULES:
            - Return ONLY valid JSON
            - Do NOT include explanations
            - Do NOT include markdown
            - Extract clean individual items (no grouping)
            - Remove contact info (phone, email, location)
            - Only include real technical tools and skills
            
            FORMAT:
            {
              "skills": ["Java", "Spring Boot", "Kafka"],
              "experience_years": number,
              "roles": ["Backend Engineer"],
              "projects": ["Project Name"],
              "tools": ["Docker", "Kubernetes", "Git"]
            }
            
            IMPORTANT:
            - Skills must be separated individually
            - Tools must NOT include personal info
            - Projects should be actual project names if present
            
            Resume:
            """ + resumeText;

        String rawResponse = ollamaService.callGroq(prompt);

        String cleanJson = extractContent(rawResponse);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(cleanJson, ResumeProfile.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String extractContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            // Remove markdown if present
            content = content.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            return content;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response";
        }
    }
}