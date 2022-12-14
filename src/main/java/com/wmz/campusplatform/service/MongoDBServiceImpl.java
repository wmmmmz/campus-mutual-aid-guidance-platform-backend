package com.wmz.campusplatform.service;

import com.wmz.campusplatform.handler.MongoDBHelper;
import com.wmz.campusplatform.pojo.Carousel;
import com.wmz.campusplatform.pojo.ChatBoxFile;
import com.wmz.campusplatform.pojo.Img;
import com.wmz.campusplatform.pojo.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class MongoDBServiceImpl implements MongoDBService{

    @Autowired
    private MongoDBHelper mongoDBHelper;

    @Override
    public List<Img> getImgListByImgUrl(String imgUrl) {
        Criteria imgUrlCriteria = Criteria.where("imgUrl").is(imgUrl);
        Query query = new Query(imgUrlCriteria);
        return mongoDBHelper.find(query, Img.class);
    }

    @Override
    public List<Carousel> getCarouselByThemeAndImgFile(String theme, byte[] imgFile) {
        Criteria themeCriteria = Criteria.where("theme").is(theme);
        Criteria imgFileCriteria = Criteria.where("imgFile").is(imgFile);
        Query query = new Query();
        query.addCriteria(themeCriteria);
        query.addCriteria(imgFileCriteria);
        return mongoDBHelper.find(query, Carousel.class);
    }

    @Override
    public List<UploadFile> getFileByFileName(String fileName) {
        Criteria fileNameCriteria = Criteria.where("fileName").is(fileName);
        Query query = new Query(fileNameCriteria);
        return mongoDBHelper.find(query, UploadFile.class);
    }

    @Override
    public List<ChatBoxFile> getChatBoxImgByFileName(String fileName) {
        Criteria fileNameCriteria = Criteria.where("fileName").is(fileName);
        Query query = new Query(fileNameCriteria);
        return mongoDBHelper.find(query, ChatBoxFile.class);
    }

    @Override
    public String getBase64ByImg(Img img) {
        String imgPre = img.getImgPre();
        byte[] imgFile = img.getImgFile();
        return imgPre + "," + Base64.getEncoder().encodeToString(imgFile);
    }

    @Override
    public String getBase64ByChatBoxFile(ChatBoxFile chatBoxFile) {
        String imgPre = chatBoxFile.getFilePre();
        byte[] chatBoxImgFile = chatBoxFile.getFileByte();
        return imgPre + Base64.getEncoder().encodeToString(chatBoxImgFile);
    }
}
