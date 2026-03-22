package com.automate.AutoJobApply.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResumeProfile {

    private List<String> skills;
    private Integer experience_years;
    private List<String> roles;
    private List<String> projects;
    private List<String> tools;

}
