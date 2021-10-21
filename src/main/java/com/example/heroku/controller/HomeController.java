package com.example.heroku.controller;

import com.example.heroku.dao.AccountDao;
import com.example.heroku.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    AccountDao ac;
    @RequestMapping("/home")
    public String home(Model model){
//        List<Account> a = ac.findAll();
//        model.addAttribute("mesage",a.get(0).getUsername());
        return "home";
    }
}
