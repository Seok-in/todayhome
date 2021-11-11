package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostProductQuestReq {
    private String questionCtgFlag;
    private int firstOptionIdx;
    private int secondOptionIdx;
    private int thirdOptionIdx;
    private String text;
    private String secretFlag;
}
