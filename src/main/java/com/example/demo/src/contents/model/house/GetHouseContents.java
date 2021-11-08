package com.example.demo.src.contents.model.house;


import lombok.AllArgsConstructor;
import com.example.demo.src.contents.model.house.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetHouseContents {
    private GetTempHouseContents houseContents;
    private List<String> products;
}