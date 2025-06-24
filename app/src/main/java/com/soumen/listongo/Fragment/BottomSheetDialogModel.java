package com.soumen.listongo.Fragment;

public class BottomSheetDialogModel {
        String title;
        String description;
        String price;
        int imageResId;

    public BottomSheetDialogModel() {
    }

    public BottomSheetDialogModel(String title, String description, String price, int imageResId) {
            this.title = title;
            this.description = description;
            this.price = price;
            this.imageResId = imageResId;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
