package com.example.demo.src.contents.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.example.demo.src.contents.model.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetSocialInfo {
    private List<SocialInfoFormat> likeInfo;
    private List<SocialInfoFormat> ScrapInfo;
    private int commentsCnt;
}