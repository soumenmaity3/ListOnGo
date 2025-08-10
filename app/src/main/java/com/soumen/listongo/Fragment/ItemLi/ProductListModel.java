package com.soumen.listongo.Fragment.ItemLi;

public class ProductListModel {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private double price;
    private String category;
    private boolean adminApprove;
    private int quantity = 1;


    public ProductListModel(Long id, String title, String description, String imageUrl, double price, String category, boolean adminApprove) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
        this.adminApprove = adminApprove;
        this.quantity=1;
    }

    public Long getId() {
        return id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public boolean isAdminApprove() {
        return adminApprove;
    }

    public void setAdminApprove(boolean adminApprove) {
        this.adminApprove = adminApprove;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

