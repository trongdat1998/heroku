package com.example.heroku.entity;

import lombok.Data;

import javax.persistence.*;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String phone;

    private String email;

}
