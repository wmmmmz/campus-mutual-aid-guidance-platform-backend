package com.wmz.campusplatform.pojo;

import javax.persistence.Id;

public class MongoSequence {
    @Id
    private String id;
    private int seq;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}