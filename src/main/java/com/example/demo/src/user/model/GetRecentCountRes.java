package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetRecentCountRes {
    private int allNum;
    private int productNum;
    private int pictureNum;
    private int houseNum;
    private int knowHowNum;
}
