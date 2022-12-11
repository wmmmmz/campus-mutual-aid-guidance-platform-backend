package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.convert.TermDetailsConvert;
import com.wmz.campusplatform.details.TermDetails;
import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.pojo.Term;
import com.wmz.campusplatform.pojo.TreeSelectData;
import com.wmz.campusplatform.repository.TermRepository;
import com.wmz.campusplatform.service.PageService;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.*;

@RestController
@RequestMapping("/term")
@Log4j2
public class TermController {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private TermDetailsConvert termDetailsConvert;

    @Autowired
    private PageService pageService;

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

    @PostMapping("/updateTerm")
    public ResultTool updateTerm(@RequestBody TermDetails termDetails){
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
        Term term = termRepository.findByTerm(termDetails.getName());
        term.setStartTime(termDetails.getDateList().get(0));
        term.setEndTime(termDetails.getDateList().get(1));
        termRepository.save(term);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getTermDataList")
    public ResultTool getTermDataList(@RequestParam(required = false) String query
                                    , @RequestParam(required = false) Integer pageSize
                                    , @RequestParam(required = false) Integer pageIndex){
        ResultTool resultTool = new ResultTool();
        List<TermDetails> dataList = new ArrayList<>();
        List<Map<String, Object>> termList = null;
        Integer offSet = pageSize * (pageIndex - 1);
        Integer termTotalSize = termRepository.findTermTotalSize(query);
        termList = termRepository.findTermList(query, pageSize, offSet);
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
        Map<String, Object> pageData = pageService.getPageData(dataList, termTotalSize);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(pageData);
        return resultTool;
    }

    @PostMapping("/deleteTerm")
    public ResultTool deleteTerm(@RequestBody Term term){
        ResultTool resultTool = new ResultTool();
        termRepository.deleteByTerm(term.getTerm());
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getTermToday")
    public ResultTool getTermToday(){
        ResultTool resultTool = new ResultTool();
        Term termByDate = termRepository.findTermByDate(new Date());
        if (termByDate != null){
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
            resultTool.setData(termByDate.getTerm());
        }else {
            resultTool.setCode(ReturnMessage.TERM_UNEXISTED.getCodeNum());
            resultTool.setMessage(ReturnMessage.TERM_UNEXISTED.getCodeMessage());
        }
        return resultTool;
    }

    @GetMapping("/getAllTerm")
    public ResultTool getAllTerm(){
        ResultTool resultTool = new ResultTool();
        List<Term> termList = termRepository.findAll();
        List<TreeSelectData> termNameList = new ArrayList<>();
        for (Term term : termList) {
            termNameList.add(new TreeSelectData(term.getTerm(), term.getTerm()));
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(termNameList);
        return resultTool;
    }
}
