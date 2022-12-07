package com.wmz.campusplatform.details;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private List<Date> dateList;

    private BigInteger courseCnt;

    private BigInteger classCnt;

}
