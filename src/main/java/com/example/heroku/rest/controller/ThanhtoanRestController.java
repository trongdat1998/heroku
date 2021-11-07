package com.example.heroku.rest.controller;

import com.example.heroku.dto.PaymenDto;
import com.example.heroku.dto.Res;
import com.example.heroku.model.Config;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import java.util.*;

@RestController
public class ThanhtoanRestController {
    @PostMapping("/vnpay")
    public ResponseEntity<?> thanhtoan() throws IOException {
        PaymenDto paymenDto = new PaymenDto();
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = paymenDto.getVnp_OrderInfo(); //Thông tin mô tả nội dung thanh toán
        String orderType = "200000";//bảng Danh mục hàng hóa =>Thời trang
        String vnp_TxnRef = Config.getRandomNumber(8);//mã đơn hàng
        String vnp_IpAddr = Config.getIpAddress;
        String vnp_TmnCode = Config.vnp_TmnCode;//Mã website
        int amount = paymenDto.getAmount() * 100;

        Map vnp_Params = new HashMap();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        String bank_code = paymenDto.getBankcode();
        if (bank_code != null && !bank_code.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bank_code);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        paymenDto.setLanguage("vn");
        String locate = paymenDto.getLanguage();
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
        return ResponseEntity.ok(new Res(paymentUrl, "success", 200));
    }

    @GetMapping("vnpay/ipn")
    public ResponseEntity<?> VnPayIPN(HttpServletRequest req) throws UnsupportedEncodingException {
        Map fields = new HashMap();
        for (Enumeration params = req.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = (String) params.nextElement();
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
        String vnp_SecureHash = req.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        // Check checksum
        String signValue = Config.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            boolean checkOrderId = true; // vnp_TxnRef exists in your database
            boolean checkAmount = true; // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the amount of the code (vnp_TxnRef) in the Your database).
            boolean checkOrderStatus = true; // PaymnentStatus = 0 (pending)
            if (checkOrderId) {
                if (checkAmount) {
                    if (checkOrderStatus) {
                        if ("00".equals(req.getParameter("vnp_ResponseCode"))) {
                            System.out.print("đat1");
                          return  ResponseEntity.ok(new Res(null, "thành công", 200));
                        } else {
                            System.out.print("đat");
                        }
                        System.out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                    } else {
                        System.out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                    }
                } else {
                    System.out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                }
            } else {
                System.out.print("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
            }
        } else {
            System.out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
        }
        String a = (String) fields.get("vnp_SecureHash");
        return ResponseEntity.ok(new Res(null, "Thất bại", 97));
    }

    @GetMapping("/vnpay/return")
    public ResponseEntity<?> VnPayReturn(HttpServletRequest req) throws UnsupportedEncodingException {
        Map fields = new HashMap();
        for (Enumeration params = req.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = (String) params.nextElement();
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
        String vnp_SecureHash = req.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = Config.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(req.getParameter("vnp_ResponseCode"))) {
                System.out.print("GD Thanh cong");
                return ResponseEntity.ok(new Res(null, "GD Thanh cong", 97));
            } else {
                System.out.print("GD Khong thanh cong");
            }

        }
        return ResponseEntity.ok(new Res(null, "Chu ky khong hop le", 97));
    }

}
