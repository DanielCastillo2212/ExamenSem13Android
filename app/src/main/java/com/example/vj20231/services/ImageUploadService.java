package com.example.vj20231.services;

import com.example.vj20231.ImageUpload.ImageUploadRequest;
import com.example.vj20231.ImageUpload.ImageUploadResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageUploadService {
    @POST("image")
    Call<ImageUploadResponse> uploadImage(@Body ImageUploadRequest request);
}
