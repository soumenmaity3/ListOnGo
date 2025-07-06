package com.soumen.listongo.ForCart;

import java.time.LocalDateTime;

public class ForAllListModel {
    private Long id;
    private String title;
    private double price;
    private int quantity;
    private String dateAndTime;
    private  String listName;

    public ForAllListModel() {
    }


    public ForAllListModel(Long id, String title, double price, int quantity, String dateAndTime, String listName) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.dateAndTime = dateAndTime;
        this.listName = listName;
    }

    public ForAllListModel(Long id, String title, double price, String dateAndTime, int quantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.dateAndTime = dateAndTime;
        this.quantity = quantity;
    }

    public String getList_name() {
        return listName;
    }

    public void setList_name(String listName) {
        this.listName = listName;
    }

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
