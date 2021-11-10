package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateOrderReq {
    private int productIdx;
    private int firstOptionIdx;
    private int secondOptionIdx;
    private int thirdOptionIdx;
    private int productNum;
}
