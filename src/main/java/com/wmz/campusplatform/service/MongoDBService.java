package com.wmz.campusplatform.service;

import com.wmz.campusplatform.pojo.Carousel;
import com.wmz.campusplatform.pojo.Img;

import java.util.List;

public interface MongoDBService {
    public List<Img> getImgListByImgUrl(String imgUrl);

    public List<Carousel> getCarouselByThemeAndImgFile(String theme, byte[] imgFile);
}
