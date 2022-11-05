package com.wmz.campusplatform.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermDetails {
    private String name;

    private List<Date> dateList;

    private BigInteger courseCnt;

    private BigInteger classCnt;

}
