package com.example.vj20231;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.vj20231.adapters.PaisajeAdapter;
import com.example.vj20231.entities.Paisaje;
import com.example.vj20231.services.PaisajeService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaPaisajeActivity extends AppCompatActivity {

    private PaisajeService paisajeService;
    private PaisajeAdapter adapter;

    //private static final int REQUEST_CODE_ACTUALIZAR_CONTACTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contact);

        RecyclerView rvListaContact = findViewById(R.id.rvListaContact);
        rvListaContact.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PaisajeAdapter(new ArrayList<>());
        rvListaContact.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://64779d129233e82dd53beed7.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        paisajeService = retrofit.create(PaisajeService.class);

        Call<List<Paisaje>> call = paisajeService.getAllContact();

        call.enqueue(new Callback<List<Paisaje>>() {
            @Override
            public void onResponse(Call<List<Paisaje>> call, Response<List<Paisaje>> response) {
                if (response.isSuccessful()) {
                    List<Paisaje> data = response.body();
                    Log.i("MAIN_APP", new Gson().toJson(data));
                    adapter.setContacts(data);
                } else {
                    Log.e("MAIN_APP", "Request failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Paisaje>> call, Throwable t) {
                Log.e("MAIN_APP", "Request failed: " + t.getMessage());
            }
        });

    }
}