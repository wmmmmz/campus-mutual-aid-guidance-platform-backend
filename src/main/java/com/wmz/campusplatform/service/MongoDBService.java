package com.wmz.campusplatform.service;

import com.wmz.campusplatform.pojo.Carousel;
import com.wmz.campusplatform.pojo.Img;
import com.wmz.campusplatform.pojo.UploadFile;

import java.util.List;

public interface MongoDBService {
    public List<Img> getImgListByImgUrl(String imgUrl);

    public List<Carousel> getCarouselByThemeAndImgFile(String theme, byte[] imgFile);

    public List<UploadFile> getFileByFileName(String fileName);

    public String getBase64ByImg(Img img);
}
