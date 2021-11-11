package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetActivities {
    private String imagePath;
    private String createdAt;
    private String activityName;
    private String flag;
}