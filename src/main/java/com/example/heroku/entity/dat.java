package com.example.heroku.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dat")
public class dat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String que;
}

