package com.example.demo.src.contents.model.picture;


import lombok.AllArgsConstructor;
import com.example.demo.src.contents.model.picture.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostPicReq {
    private List<PicContentFormat> PicContent;
    private String houseSize;
    private String houseType;
    private String houseStyle;
}