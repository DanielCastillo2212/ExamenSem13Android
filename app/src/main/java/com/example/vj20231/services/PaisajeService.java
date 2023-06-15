package com.example.vj20231.services;

import com.example.vj20231.ImageUpload.ImageUploadRequest;
import com.example.vj20231.ImageUpload.ImageUploadResponse;
import com.example.vj20231.entities.Paisaje;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PaisajeService {
    @GET("contact")
    Call<List<Paisaje>> getAllContact();

    @GET("contact/{id}")
    Call<Paisaje> findUser(@Path("id") int id);

    @POST("contact")
    Call<Paisaje> create(@Body Paisaje paisaje);

    @PUT("contact/{id}")
    Call <Paisaje> updateContacto(@Path("id") int id, @Body Paisaje paisaje);

    @DELETE("contact/{id}")
    Call<Void> delete(@Path("id") int id);

    @POST("image")
    Call<ImageUploadResponse> uploadImage(@Body ImageUploadRequest request);

}
