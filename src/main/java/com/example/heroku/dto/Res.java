package com.example.heroku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Res {
    private String data;
    private String message;
    private int status;

    public Res(String message, int status){
        this.message = message;
        this.status = status;
    }
}
