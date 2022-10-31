package com.wmz.campusplatform.handler;

import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * mongo db助手
 */
@Component
public class MongoDBHelper {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存
     *
     * @param t
     * @param <T>
     * @return
     */
    public <T> T save(T t) {
        return mongoTemplate.save(t);
    }

    /**
     * 保存
     *
     * @param t
     * @param collectionName
     * @param <T>
     * @return
     */
    public <T> T save(T t, String collectionName) {
        return mongoTemplate.save(t, collectionName);
    }

    /**
     * 查询
     *
     * @param query
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> find(Query query, Class<T> tClass) {
        return mongoTemplate.find(query, tClass);
    }

    /**
     * 查询所有
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> findAll(Class<T> tClass) {
        return mongoTemplate.findAll(tClass);
    }



}

