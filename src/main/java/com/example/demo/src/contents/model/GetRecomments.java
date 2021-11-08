package com.example.demo.src.contents.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRecomments {
    private String rcText;
    private int likeCnt;
    private String likedByUser;
    private String pastTime;
}