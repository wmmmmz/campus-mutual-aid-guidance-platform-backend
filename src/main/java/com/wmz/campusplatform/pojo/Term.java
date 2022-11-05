package com.wmz.campusplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "term")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String term;

    private Date startTime;

    private Date endTime;

}
