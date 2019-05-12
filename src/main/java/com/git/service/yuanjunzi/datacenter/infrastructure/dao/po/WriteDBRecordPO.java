package com.git.service.yuanjunzi.datacenter.infrastructure.dao.po;

/**
 * Created by yuanjunzi on 2017/11/18.
 */
public class WriteDBRecordPO {
    private long id;
    private long ssoid;
    private String ssoname;
    private String type;
    private String columna;
    private String columnb;
    private String columnc;
    private String columnd;
    private String columne;
    private long time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSsoid() {
        return ssoid;
    }

    public void setSsoid(long ssoid) {
        this.ssoid = ssoid;
    }

    public String getSsoname() {
        return ssoname;
    }

    public void setSsoname(String ssoname) {
        this.ssoname = ssoname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumna() {
        return columna;
    }

    public void setColumna(String columna) {
        this.columna = columna;
    }

    public String getColumnb() {
        return columnb;
    }

    public void setColumnb(String columnb) {
        this.columnb = columnb;
    }

    public String getColumnc() {
        return columnc;
    }

    public void setColumnc(String columnc) {
        this.columnc = columnc;
    }

    public String getColumnd() {
        return columnd;
    }

    public void setColumnd(String columnd) {
        this.columnd = columnd;
    }

    public String getColumne() {
        return columne;
    }

    public void setColumne(String columne) {
        this.columne = columne;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
