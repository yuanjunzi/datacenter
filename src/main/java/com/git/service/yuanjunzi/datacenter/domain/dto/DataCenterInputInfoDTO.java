package com.git.service.yuanjunzi.datacenter.domain.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanjunzi on 2019/4/24.
 * 数据查询领域入参
 */
public class DataCenterInputInfoDTO {

    private Map<String, Object> must;

    private Map<String, Object> should;

    private Map<String, Object> mustNot;

    private String bucket;

    private String metrics;

    private String type;

    private boolean needHistogram = true;

    public DataCenterInputInfoDTO() {
        must = new HashMap<>();
        should = new HashMap<>();
        mustNot = new HashMap<>();
    }

    public void setMust(Map<String, Object> must) {
        this.must = must;
    }

    public void setShould(Map<String, Object> should) {
        this.should = should;
    }

    public void setMustNot(Map<String, Object> mustNot) {
        this.mustNot = mustNot;
    }

    public Map<String, Object> getMust() {
        return must;
    }

    public void addMust(String key, Object value) {
        if (this.must == null) {
            must = new HashMap<>();
        }
        this.must.put(key, value);
    }

    public void addMust(Map<String, Object> must) {
        if (this.must == null) {
            this.must = new HashMap<>();
        }
        this.must.putAll(must);
    }

    public Map<String, Object> getShould() {
        return should;
    }

    public void addShould(String key, Object value) {
        if (this.should == null) {
            this.should = new HashMap<>();
        }
        this.should.put(key, value);
    }

    public void addShould(Map<String, Object> should) {
        if (this.should == null) {
            this.should = new HashMap<>();
        }
        this.should.putAll(should);
    }

    public Map<String, Object> getMustNot() {
        return mustNot;
    }

    public void addMustNot(String key, Object value) {
        if (this.mustNot == null) {
            this.mustNot = new HashMap<>();
        }
        this.mustNot.put(key, value);
    }

    public void addMustNot(Map<String, Object> mustNot) {
        if (this.mustNot == null) {
            this.mustNot = new HashMap<>();
        }
        this.mustNot.putAll(mustNot);
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNeedHistogram() {
        return needHistogram;
    }

    public void setNeedHistogram(boolean needHistogram) {
        this.needHistogram = needHistogram;
    }

    public DataCenterInputInfoDTO copy() {
        DataCenterInputInfoDTO dto = new DataCenterInputInfoDTO();
        dto.addMust(this.must);
        dto.addShould(this.should);
        dto.addMustNot(this.mustNot);
        dto.setBucket(this.bucket);
        dto.setMetrics(this.metrics);
        dto.setType(this.type);
        dto.setNeedHistogram(this.needHistogram);
        return dto;
    }

    @Override
    public String toString() {
        return "DataCenterInputInfoDTO{" +
                "must=" + must +
                ", should=" + should +
                ", mustNot=" + mustNot +
                ", bucket='" + bucket + '\'' +
                ", metrics='" + metrics + '\'' +
                ", type='" + type + '\'' +
                ", needHistogram=" + needHistogram +
                '}';
    }
}
