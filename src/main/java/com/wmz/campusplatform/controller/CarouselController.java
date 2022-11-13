package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.details.CarouselDetails;
import com.wmz.campusplatform.handler.MongoDBHelper;
import com.wmz.campusplatform.pojo.Carousel;
import com.wmz.campusplatform.pojo.Img;
import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.service.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carousel")
public class CarouselController {

    @Autowired
    private MongoDBHelper mongoDBHelper;

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("/saveCarousel")
    public ResultTool saveCarousel(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String ImgBase64 = (String)map.get("imgUrl");
        String theme = (String)map.get("theme");
        String[] split = ImgBase64.split(",");
        mongoDBHelper.save(new Carousel(mongoDBHelper.findAll(Carousel.class).size() + 1, theme,
                split[0], Base64.getDecoder().decode(split[1])));
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getAllCarousel")
    public ResultTool getAllCarousel(@RequestParam(required = false) String query){
        ResultTool resultTool = new ResultTool();
        List<Carousel> carouselList = mongoDBHelper.findAll(Carousel.class);
        List<CarouselDetails> carouselDetails = new ArrayList<>();
        for (Carousel carousel : carouselList) {
            List<String> imgUrlList = new ArrayList<>();
            String imgUrl = carousel.getImgPre() + "," + Base64.getEncoder().encodeToString(carousel.getImgFile());
            imgUrlList.add(imgUrl);
            carouselDetails.add(new CarouselDetails(carousel.getTheme(), imgUrl, imgUrlList));
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(carouselDetails);
        return resultTool;
    }

    @PostMapping("/deleteCarousel")
    public ResultTool deleteCarousel(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String theme = (String) map.get("theme");
        String imgBase64 = (String) map.get("imgBase64");
        String[] split = imgBase64.split(",");
        byte[] imgFile = Base64.getDecoder().decode(split[1]);
        List<Carousel> carouselByThemeAndImgFile = mongoDBService.getCarouselByThemeAndImgFile(theme, imgFile);
        for (Carousel carousel : carouselByThemeAndImgFile) {
            mongoTemplate.remove(carousel);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }
}
