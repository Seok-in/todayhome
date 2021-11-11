package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostUserReq {
    private String userEmail;
    @NotBlank(message = "NULL PASSWORD")
    @Size(min = 7, max=18, message = "SIZE PASSWORD")
    private String userPw;
    @NotBlank(message = "NULL USERNAME")
    @Size(min = 2, max = 15, message = "SIZE USERNAME")
    private String userName;
}
