package com.wmz.campusplatform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService{

    @Override
    public String generateImgUrl() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
