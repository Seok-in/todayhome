package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostProductQuestReq {
    private String questionCtgFlag;
    private int firstOptionIdx;
    private int secondOptionIdx;
    private int thirdOptionIdx;
    @NotBlank(message = "QUESTION BLANK")
    @Size(min=5, max=200, message="QUESTION SIZE")
    private String text;
    private String secretFlag;
}
