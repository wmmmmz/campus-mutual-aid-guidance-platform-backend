package com.wmz.campusplatform.service;

import java.util.Map;

public interface PageService {
    Map<String, Object> getPageData(Object dataList, Integer totalSize);
}
