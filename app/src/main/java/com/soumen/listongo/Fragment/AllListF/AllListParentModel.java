package com.soumen.listongo.Fragment.AllListF;

public class AllListParentModel {
    private int id;
    private String title;
    private double price;
    private String dateAndTime;
    private int quantity;
    private String listName;

    public AllListParentModel(int id, String title, double price, String dateAndTime, int quantity, String listName) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.dateAndTime = dateAndTime;
        this.quantity = quantity;
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
