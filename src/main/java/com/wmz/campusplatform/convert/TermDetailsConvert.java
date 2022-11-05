package com.wmz.campusplatform.convert;

import com.wmz.campusplatform.details.TermDetails;
import com.wmz.campusplatform.pojo.Term;
import org.springframework.stereotype.Component;

@Component
public class TermDetailsConvert {
    public Term termDetailsConvert(TermDetails termDetails){
        Term term = new Term();
        term.setTerm(termDetails.getName());
        term.setStartTime(termDetails.getDateList().get(0));
        term.setEndTime(termDetails.getDateList().get(1));
        return term;
    }
}
