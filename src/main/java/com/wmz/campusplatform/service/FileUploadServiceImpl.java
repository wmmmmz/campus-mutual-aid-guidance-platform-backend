package com.wmz.campusplatform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService{

    @Value("${upload.filePath}")
    String basePath;

    @Override
    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public String saveTempFile(MultipartFile multipartFile) throws IOException {
        File file = multipartToFile(multipartFile, generateUUID());
        return file.getPath();
    }

    private File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        multipart.transferTo(convFile);
        return convFile;
    }
    public byte[] fileToByte(File file) throws IOException{
        byte[] bytes = null;
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(file);
            bytes = new byte[(int) file.length()];
            fis.read(bytes);
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }finally{
            fis.close();
        }
        return bytes;
    }

    @Override
    public String getBase64PrefixByFileSuffix(String suffixName) {
        String prefix = "";
        switch (suffixName){
            case ".doc":
                prefix = "data:application/msword;base64,";
                break;
            case ".docx":
                prefix = "data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,";
                break;
            case ".pdf":
                prefix = "data:application/pdf;base64,";
                break;
            case ".jpg":
                prefix = "data:image/jpeg;base64,";
                break;
            case ".png":
                prefix = "data:image/png;base64,";
                break;
            case ".gif":
                prefix = "data:image/gif;base64,";
                break;
            case ".svg":
                prefix = "data:image/svg+xml;base64,";
                break;
            case ".ico":
                prefix = "data:image/x-icon;base64,";
                break;
            case ".bmp":
                prefix = "data:image/bmp;base64,";
                break;
        }
        return prefix;
    }


}
