package com.example.vj20231.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vj20231.MapsActivity;
import com.example.vj20231.R;
import com.example.vj20231.entities.Paisaje;
import com.example.vj20231.services.PaisajeResponse;
import com.example.vj20231.services.GetUbicationService;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaisajeAdapter extends RecyclerView.Adapter<PaisajeAdapter.NameViewHolder> {

    private List<Paisaje> items;

    public PaisajeAdapter(List<Paisaje> items) {
        this.items = items;
    }

    public void setContacts(List<Paisaje> paisajes) {
        this.items = paisajes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_paisaje, parent, false);
        NameViewHolder viewHolder = new NameViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        Paisaje item = items.get(position);
        View view = holder.itemView;

        ImageView ivContactImage = view.findViewById(R.id.ivContactImage);
        TextView tvNombre = view.findViewById(R.id.tvNombre);
        TextView tvNumero = view.findViewById(R.id.tvNumero);
        Button btnVer = view.findViewById(R.id.btnVer);
        Button btnUbicacion = view.findViewById(R.id.btnUbicacion);

        // Load the image using Picasso
        Picasso.get().load(item.getImgPaisaje()).into(ivContactImage);

        tvNombre.setText(item.getNamePaisaje());

        btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                Paisaje clickedItem = items.get(clickedPosition);

                // Crea una instancia de Retrofit y la interfaz del servicio
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://64779d129233e82dd53beed7.mockapi.io/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                GetUbicationService ubicationService = retrofit.create(GetUbicationService.class);

                // Realiza la solicitud para obtener los datos de latitud y longitud
                Call<PaisajeResponse> call = ubicationService.getContact(clickedItem.getId());
                call.enqueue(new Callback<PaisajeResponse>() {
                    @Override
                    public void onResponse(Call<PaisajeResponse> call, Response<PaisajeResponse> response) {
                        if (response.isSuccessful()) {
                            PaisajeResponse paisajeResponse = response.body();
                            if (paisajeResponse != null) {
                                double latitude = paisajeResponse.getLatitude();
                                double longitude = paisajeResponse.getLongitude();
                                // Realiza la redirección a MapsActivity y pasa los datos de latitud y longitud
                                Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
                                intent.putExtra("nombre", clickedItem.getNamePaisaje());
                                intent.putExtra("latitud", latitude);
                                intent.putExtra("longitud", longitude);
                                holder.itemView.getContext().startActivity(intent);
                            }
                        } else {
                            // Maneja el caso de respuesta no exitosa de la API
                        }
                    }

                    @Override
                    public void onFailure(Call<PaisajeResponse> call, Throwable t) {
                        // Maneja el caso de error de la solicitud a la API
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addContact(Paisaje paisaje) {
        items.add(paisaje);
        notifyDataSetChanged();
    }

    public static class NameViewHolder extends RecyclerView.ViewHolder {
        public NameViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}


