package com.abc.editorserver.module.JSONModule;

public class ExcelConfig {

    private String excel;
    private String sheet;
    private String redis_table;
    private String redis_key;
    private String refs;


    public String getExcel() {
        return excel;
    }

    public void setExcel(String excel) {
        this.excel = excel;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    public String getRedis_table() {
        return redis_table;
    }

    public void setRedis_table(String redis_table) {
        this.redis_table = redis_table;
    }

    public String getRedis_key() {
        return redis_key;
    }

    public void setRedis_key(String redis_key) {
        this.redis_key = redis_key;
    }

    public String getRefs() { return refs; }

    public void setRefs(String refs) { this.refs = refs; }
}
