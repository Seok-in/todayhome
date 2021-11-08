package com.example.demo.src.contents.model.knowhow;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetKnowhowIntro {
    private String coverImage;
    private String title;
    private String createdAt;
    private String userName;
    private String userIntro;
    private String knowhowCategory;
    private String followedByUser;
}