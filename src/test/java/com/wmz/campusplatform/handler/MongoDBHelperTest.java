package com.wmz.campusplatform.handler;

import com.mongodb.BasicDBObject;
import com.wmz.campusplatform.pojo.Img;
import com.wmz.campusplatform.utils.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MongoDBHelperTest{

    @Autowired
    private MongoDBHelper mongoDBHelper;

    @Test
    public void mangoDBTest() throws IOException {
        Img img = new Img(1, "default", FileUtils.fileToByte(new File("/Users/mengzhe/Pictures/photo.jpg")));
        mongoDBHelper.save(img);
    }

    @Test
    public void testFind() {
        Criteria imgUrl = Criteria.where("imgUrl").is("admin_admin");
        Query query = new Query(imgUrl);
        List<Img> img = mongoDBHelper.find(query, Img.class);
        for (Img img1 : img) {
            System.out.println("image" +img1.getImgUrl());
//            Base64.getEncoder().encodeToString(img1.getImgFile()));
        }
//        List<User> list = mongoDBHelper.find(query,User.class);
//        List<User> list = mongoDBHelper.findAll(User.class);
//        for (User user : list) {
//            System.out.println("id=" + user.getId() + ",name=" + user.getName());
//        }
    }
}
