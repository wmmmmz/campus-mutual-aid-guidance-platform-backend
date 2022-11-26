package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileUploadController {

    @Value("${upload.filePath}")
    private String filePath;

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping(value = "/nginx/uploadByAction")
    public ResultTool uploadByNginx(@RequestParam MultipartFile file) throws IOException {
        ResultTool resultTool = new ResultTool();
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        Map<String, String> map = new HashMap<>();
        String path = fileUploadService.saveTempFile(file);
        map.put("path", path);
        map.put("suffixName", suffixName);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(map);
        return resultTool;
    }
}
