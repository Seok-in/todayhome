package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Point {
    private String pointName;
    private String pointText;
    private int point;
    private String expiredAt;
    private String createdAt;
}