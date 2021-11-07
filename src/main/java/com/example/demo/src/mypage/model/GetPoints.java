package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPoints {
    private int usablePoints;
    private List<Point> point;
}