package com.example.springsecurityoauth2.oauth2.domain;

import lombok.Data;

@Data
public class UploadFile {
    private String uploadFileName;
    private String StoreFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        StoreFileName = storeFileName;
    }
}
