package com.example.demo.src.contents.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetComments {
    private String userName;
    private String cText;
    private int likeCnt;
    private String likedByUser;
    private String pastTime;
}