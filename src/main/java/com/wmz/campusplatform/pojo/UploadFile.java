package com.wmz.campusplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.File;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class UploadFile {
    @Id
    private int id;

    private String filePre;

    private String fileName;

    private byte[] file;
}
