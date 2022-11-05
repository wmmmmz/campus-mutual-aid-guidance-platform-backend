package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.convert.TermDetailsConvert;
import com.wmz.campusplatform.details.TermDetails;
import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.repository.TermRepository;
import com.wmz.campusplatform.utils.StringUtils;
import io.swagger.models.auth.In;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/term")
@Log4j2
public class TermController {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private TermDetailsConvert termDetailsConvert;

    @PostMapping("/saveTerm")
    public ResultTool saveTerm(@RequestBody TermDetails termDetails){
        ResultTool resultTool = new ResultTool();
        if (termDetails.getDateList() == null){
            resultTool.setCode(ReturnMessage.NULL_TERM_TIME.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_TERM_TIME.getCodeMessage());
            return resultTool;
        }
        if (StringUtils.isEmpty(termDetails.getName())){
            resultTool.setCode(ReturnMessage.NULL_TERM_NAME.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_TERM_NAME.getCodeMessage());
            return resultTool;
        }

        if (termRepository.findByTerm(termDetails.getName()) == null){
            termRepository.save(termDetailsConvert.termDetailsConvert(termDetails));
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        }else {
            resultTool.setCode(ReturnMessage.EXISTED_TERM.getCodeNum());
            resultTool.setMessage(ReturnMessage.EXISTED_TERM.getCodeMessage());
        }
        return resultTool;
    }

    @GetMapping("/getTermDataList")
    public ResultTool getTermDataList(){
        ResultTool resultTool = new ResultTool();
        List<TermDetails> dataList = new ArrayList<>();
        List<Map<String, Object>> termList = termRepository.findTermList();
        for (Map<String, Object> termStatisticsDetails : termList) {
            Date startTime = null, endTime = null;
            TermDetails termDetails = new TermDetails();
            for(Map.Entry<String, Object> m : termStatisticsDetails.entrySet()){
                switch (m.getKey()){
                    case "term":
                        termDetails.setName((String) m.getValue());
                        break;
                    case "startTime":
                        startTime = (Date)m.getValue();
                        break;
                    case "endTime":
                        endTime = (Date)m.getValue();
                        break;
                    case "courseCnt":
                        termDetails.setCourseCnt((BigInteger) m.getValue());
                        break;
                    case "classCnt":
                        termDetails.setClassCnt((BigInteger) m.getValue());
                        break;
                }
            }
            List<Date> dateList = new ArrayList<>();
            dateList.add(startTime);
            dateList.add(endTime);
            termDetails.setDateList(dateList);
            dataList.add(termDetails);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(dataList);
        return resultTool;
    }
}
