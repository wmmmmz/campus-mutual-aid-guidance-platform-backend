package com.wmz.campusplatform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService{
    @Value("${web.upload}")
    private String imgBaseUrl;

    @Override
    public String generateImgUrl(String baseUrl) {
        return baseUrl + "/" + UUID.randomUUID().toString().replace("-", "");
    }
}
