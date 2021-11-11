package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetActivityParams {
    private String createdAt;
    private String flag;
    private String activityName;
    private int houseIdx;
    private int knowhowIdx;
    private int pictureIdx;
    private int productIdx;
}