package com.example.demo.src.contents.model.picture;


import lombok.AllArgsConstructor;
import com.example.demo.src.contents.model.picture.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class PicContentFormat {
    private String imagePath;
    private String pictureText;
    private String pictureCategory;
    private List<String> keyword;
    private List<Products> productIndices = new ArrayList<Products>();
}