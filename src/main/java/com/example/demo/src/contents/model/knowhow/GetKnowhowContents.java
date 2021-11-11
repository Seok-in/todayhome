package com.example.demo.src.contents.model.knowhow;


import lombok.AllArgsConstructor;
import com.example.demo.src.contents.model.house.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetKnowhowContents {
    private GetTempKnowhowContents knowhowContents;
    private List<String> products;
}