package com.soumen.listongo.Fragment.AddItem;

public class AddProductModel {
    private String title;
    private String description;
    private double price;
    private String category;
    private String nickName;

    public AddProductModel() {
    }

    public AddProductModel(String title, String description, double price, String category) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
