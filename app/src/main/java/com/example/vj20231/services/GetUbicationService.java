package com.example.vj20231.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetUbicationService {
    @GET("contact/{id}")
    Call<PaisajeResponse> getContact(@Path("id") int contactId);
}

