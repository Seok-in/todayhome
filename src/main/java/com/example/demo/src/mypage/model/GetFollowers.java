package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetFollowers {
    private String userName;
    private String bio;
    private String imagePath;
    private String followedByLogonUser;
    private String logonUser;
}