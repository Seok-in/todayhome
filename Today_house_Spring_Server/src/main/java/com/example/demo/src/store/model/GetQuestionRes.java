package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetQuestionRes {
    private String questionCtgFlag;
    private String userName;
    private String createdAt;
    private String text;
    private String status;
    private String answerText;
    private String companyName;
    private String answerCreatedAt;
    private String firstOptionName;
    private String secondOptionName;
    private String thirdOptionName;
    private String secretFlag;
}
