package com.example.senomerc.model;

import java.io.Serializable;

public class ProductsModel implements Serializable {

    String description;
    String name;
    String rating;
    int price;
    String img_url;
    String type;

    public ProductsModel() {

    }

    public ProductsModel(String description, String name, String rating, int price, String img_url, String type) {
        this.description = description;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.img_url = img_url;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public int getPrice() {
        return price;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getType() {return type;}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setType(String type) { this.type = type;}

}
