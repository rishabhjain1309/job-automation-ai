package com.automate.AutoJobApply.model;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class EmailRequest {

    private String to;
    private String subject;
    private String body;

}
