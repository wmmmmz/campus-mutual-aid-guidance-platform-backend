package com.wmz.campusplatform.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileUploadService {
    public String generateUUID();

    public String saveTempFile(MultipartFile file) throws IOException;

    public byte[] fileToByte(File file) throws IOException;

    public String getBase64PrefixByFileSuffix(String suffixName);
}
