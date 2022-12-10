package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class SolarFestival extends LitePalSupport {
    int id;
    String name;
    Date date;
    // TODO: 2022/12/7 这里可以存储节日、节气简介

    public SolarFestival(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
