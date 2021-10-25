package com.example.heroku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymenDto {
    int amount = 400000;
    String vnp_OrderInfo ="mua hang";
    String bankcode = "NCB";
    String language="vn";
}
