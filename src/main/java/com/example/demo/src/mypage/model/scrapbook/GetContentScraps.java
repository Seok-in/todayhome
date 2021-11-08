package com.example.demo.src.mypage.model.scrapbook;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
/**
노하우, 집들이 스크랩북
 */
@Getter
@Setter
@AllArgsConstructor
public class GetContentScraps {
    private String imagePath;
    private String title;
    private String userName;
}