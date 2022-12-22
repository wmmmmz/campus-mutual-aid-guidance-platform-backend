package com.wmz.campusplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Document
@AllArgsConstructor
public class ChatBoxFile {
    @Id
    private int id;

    private String fileName;

    private String filePre;

    private byte[] fileByte;

}
