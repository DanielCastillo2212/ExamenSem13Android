package com.example.vj20231.ImageUpload;

import com.google.gson.annotations.SerializedName;

public class ImageUploadResponse {
    @SerializedName("url")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }
}
