package com.example.senomerc.model;

public class CategoryModel {
    String name;
    String img_url;
    String type;

    public CategoryModel() {
    }

    public CategoryModel(String name, String img_url, String type) {
        this.name = name;
        this.img_url = img_url;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setType(String type) {
        this.type = type;
    }
}
