package com.example.heroku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymenDto {
    int amount;
    String vnp_OrderInfo;
    String bankcode;
    String language;
}
