package com.anoodle.webapi.bean;

public class WasteCategory {

    private String containdis;
    private long createTime;
    private String explaindis;
    private String id;
    private String name;
    private String tip;
    //0为可回收、1为有害、2为厨余(湿)、3为其他(干)
    private String type;

    public String getContaindis() {
        return containdis;
    }

    public void setContaindis(String containdis) {
        this.containdis = containdis;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getExplaindis() {
        return explaindis;
    }

    public void setExplaindis(String explaindis) {
        this.explaindis = explaindis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
