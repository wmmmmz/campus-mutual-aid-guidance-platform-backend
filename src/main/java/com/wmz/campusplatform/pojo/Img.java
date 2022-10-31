package com.wmz.campusplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.File;

@Data
@Document
@AllArgsConstructor
public class Img{
//    private int id;

    @Id
    private int id;

    private String imgUrl;

    private byte[] imgFile;

}
