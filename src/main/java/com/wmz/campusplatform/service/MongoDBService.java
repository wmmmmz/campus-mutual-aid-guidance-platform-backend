package com.wmz.campusplatform.service;

import com.wmz.campusplatform.pojo.Carousel;
import com.wmz.campusplatform.pojo.ChatBoxImg;
import com.wmz.campusplatform.pojo.Img;
import com.wmz.campusplatform.pojo.UploadFile;

import java.util.List;

public interface MongoDBService {
    List<Img> getImgListByImgUrl(String imgUrl);

    List<Carousel> getCarouselByThemeAndImgFile(String theme, byte[] imgFile);

    List<UploadFile> getFileByFileName(String fileName);

    List<ChatBoxImg> getChatBoxImgByImgName(String imgName);

    String getBase64ByImg(Img img);

    String getBase64ByChatBoxImg(ChatBoxImg chatBoxImg);
}
