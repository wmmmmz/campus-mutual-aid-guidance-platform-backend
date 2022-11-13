package com.wmz.campusplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Document
@AllArgsConstructor
public class Carousel {
    @Id
    private int id;

    private String theme;

    private String imgPre;

    private byte[] imgFile;
}
