package com.automate.AutoJobApply.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class JobMatchResult {

    private int score;
    private List<String> matched_skills;
    private List<String> missing_skills;
    private String decision;
}
