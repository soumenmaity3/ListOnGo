package com.soumen.listongo.ForAdmin.viewfragment;

import com.google.gson.annotations.SerializedName;

public class RequestAdminModel {
    private Long id;
    private String email;
    @SerializedName("adminReason")
    private String adminReason;

    public RequestAdminModel(Long id, String email, String adminReason) {
        this.id = id;
        this.email = email;
        this.adminReason = adminReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdminReason() {
        return adminReason;
    }

    public void setAdminReason(String adminReason) {
        this.adminReason = adminReason;
    }
}
