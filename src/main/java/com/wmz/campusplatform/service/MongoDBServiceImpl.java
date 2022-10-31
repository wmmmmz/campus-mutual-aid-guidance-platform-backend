package com.wmz.campusplatform.service;

import com.wmz.campusplatform.handler.MongoDBHelper;
import com.wmz.campusplatform.pojo.Img;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

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
}
