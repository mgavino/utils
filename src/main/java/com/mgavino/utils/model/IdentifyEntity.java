package com.mgavino.utils.model;

import org.springframework.data.annotation.Id;

public abstract class IdentifyEntity {

    @Id
    private String id;

    public IdentifyEntity() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
