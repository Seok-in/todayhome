package com.example.demo.src.oAuthLogin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 @Getter
 @Setter
 @AllArgsConstructor
 @NoArgsConstructor
 public class OAuthToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
        private int refresh_token_expires_in;
 }

