package com.example.demo.src.contents.model.house;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.example.demo.src.contents.model.house.*;
import com.example.demo.src.contents.model.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetAllHouseContents {
    private List<GetHouseIntro> intro;
    private List<GetHouseContents> contents;
    private List<GetSocialInfo> socialInfo;
    private List<GetComments> comments;
}