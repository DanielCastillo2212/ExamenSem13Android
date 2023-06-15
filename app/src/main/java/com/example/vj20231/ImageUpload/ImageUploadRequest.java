package com.example.vj20231.ImageUpload;

public class ImageUploadRequest {
    private String base64Image;

    public ImageUploadRequest(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
}
