package com.soumen.listongo.Fragment.AllListF;

import com.google.gson.annotations.SerializedName;
import com.soumen.listongo.ForCart.ForAllListModel;

import java.util.List;

public class AllListFragmentModel {
    private String dateTime;
    private List<ForAllListModel> items;
    @SerializedName("listName")
    private String listName;

    public AllListFragmentModel(String dateTime, List<ForAllListModel> items, String listName) {
        this.dateTime = dateTime;
        this.items = items;
        this.listName = listName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setList_name(String listName) {
        this.listName = listName;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<ForAllListModel> getItems() {
        return items;
    }

    public void setItems(List<ForAllListModel> items) {
        this.items = items;
    }
}
