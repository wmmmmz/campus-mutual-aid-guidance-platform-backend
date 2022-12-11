package com.wmz.campusplatform.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService{
    @Override
    public Map<String, Object> getPageData(Object dataList, Integer totalSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("dataList", dataList);
        result.put("totalSize", totalSize);
        return result;
    }
}
