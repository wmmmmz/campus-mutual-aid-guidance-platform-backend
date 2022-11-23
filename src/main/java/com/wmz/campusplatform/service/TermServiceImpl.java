package com.wmz.campusplatform.service;

import com.wmz.campusplatform.pojo.Term;
import com.wmz.campusplatform.repository.TermRepository;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
public class TermServiceImpl implements TermService{

    @Autowired
    private TermRepository termRepository;

    @Override
    public String getTermVerified(String termName) {
        if (!StringUtils.isEmpty(termName)) return termName;
        Term termByDate = termRepository.findTermByDate(new Date());
        if (termByDate == null) {
            log.error("学期不存在");
            return null;
        } else {
            return termByDate.getTerm();
        }
    }
}
