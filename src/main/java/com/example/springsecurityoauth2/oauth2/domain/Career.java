package com.example.springsecurityoauth2.oauth2.domain;

public enum Career {
    A("A조"),
    B("B조"),
    C("C조"),
    D("D조"),
    E("초심"),
    EE("왕초심");

    private String stringValue;

    Career(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}