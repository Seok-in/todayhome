package com.example.demo.src.contents.model.knowhow;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.example.demo.src.contents.model.house.*;
import com.example.demo.src.contents.model.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetAllKnowhowContents {
    private List<GetKnowhowIntro> intro;
    private List<GetKnowhowContents> contents;
    private List<GetSocialInfo> socialInfo;
    private List<GetComments> comments;
}