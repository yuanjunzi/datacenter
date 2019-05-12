package com.git.service.yuanjunzi.datacenter.domain.dto;

/**
 * Created by yuanjunzi on 2018/11/6.
 */
public class QueryConditionDTO {

    private String term;
    private String value;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
