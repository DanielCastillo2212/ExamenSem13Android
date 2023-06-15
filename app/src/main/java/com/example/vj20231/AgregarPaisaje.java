package com.example.vj20231;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.vj20231.ImageUpload.ImageUploadRequest;
import com.example.vj20231.ImageUpload.ImageUploadResponse;
import com.example.vj20231.adapters.PaisajeAdapter;
import com.example.vj20231.entities.Paisaje;
import com.example.vj20231.services.PaisajeService;
import com.example.vj20231.services.ImageUploadService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.appcompat.app.AlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AgregarPaisaje extends AppCompatActivity implements LocationListener {

    private EditText etNombre;
    private EditText etNumero;
    private Button btnAgregar;
    private Button btnAgregarImagen;
    private Button btnAgregarUbicacion;
    private LinearLayout ubicacionLayout;

    private PaisajeService paisajeService;

    private PaisajeAdapter adapter;

    private LocationManager mLocationManager;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private Uri selectedImageUri;

    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_paisaje);

        etNombre = findViewById(R.id.etNombre);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregarImagen = findViewById(R.id.btnAgregarImagen);
        btnAgregarUbicacion = findViewById(R.id.btnAgregarUbicacion);
        ubicacionLayout = findViewById(R.id.ubicacionLayout);

        // Solicitar actualizaciones de ubicación
        if(
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            requestPermissions(permissions, 3000);

        }
        else {
            // configurar frecuencia de actualización de GPS
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1, this);
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                Log.i("MAIN_APP: Location - ",  "Latitude: " + location.getLatitude());
            } else {
                Log.i("MAIN_APP: Location - ",  "Location is null");
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://64779d129233e82dd53beed7.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        paisajeService = retrofit.create(PaisajeService.class);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();

                if (!nombre.isEmpty()) {
                    // Crear un nuevo objeto Contact con los datos ingresados
                    Paisaje paisaje = new Paisaje();
                    paisaje.setNamePaisaje(nombre);

                    // Obtener los valores de longitud y latitud desde los EditText correspondientes
                    EditText etLatitud = findViewById(R.id.etLatitud);
                    EditText etLongitud = findViewById(R.id.etLongitud);
                    Double latitud = Double.parseDouble(etLatitud.getText().toString());
                    Double longitud = Double.parseDouble(etLongitud.getText().toString());

                    // Asignar los valores de longitud y latitud al objeto Contact
                    paisaje.setLatitude(latitud);
                    paisaje.setLongitude(longitud);

                    if (!nombre.isEmpty() ) {
                        // ...

                        if (selectedImageUri != null) {
                            String imageBase64 = convertImageToBase64(selectedImageUri);
                            paisaje.setImgPaisaje(imageBase64);

                            // Subir la imagen a la API
                            uploadImageToApi(imageBase64);
                        }

                        // ...
                    } else {
                        Toast.makeText(AgregarPaisaje.this, "Ingrese un nombre y número válidos", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        btnAgregarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activar el LinearLayout ubicacionLayout
                ubicacionLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openImagePicker() {
        // Crear el cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarPaisaje.this);
        builder.setTitle("Seleccionar imagen");
        builder.setItems(new CharSequence[]{"Cámara"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Opción de la cámara seleccionada
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // El permiso de cámara está concedido, abrir la cámara
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, REQUEST_CAMERA);
                    } else {
                        // Solicitar permiso de cámara si no está concedido
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                    }
                    break;
            }
        });

        // Mostrar el cuadro de diálogo
        builder.show();
    }

    private void uploadImageToApi(String base64Image) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://demo-upn.bit2bittest.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ImageUploadService imageUploadService = retrofit.create(ImageUploadService.class);

        ImageUploadRequest request = new ImageUploadRequest(base64Image);

        Call<ImageUploadResponse> call = imageUploadService.uploadImage(request);
        call.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful()) {
                    // La imagen se ha subido correctamente
                    ImageUploadResponse uploadResponse = response.body();
                    String imageUrl = uploadResponse.getImageUrl();

                    // Agregar el contacto con la URL de la imagen
                    agregarContactoConImagen(imageUrl);
                } else {
                    // Error al subir la imagen
                    Toast.makeText(AgregarPaisaje.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                // Error de conexión
                Toast.makeText(AgregarPaisaje.this, "Error de conexión al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso de cámara fue concedido, abrir la cámara
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                Toast.makeText(this, "El permiso de cámara es requerido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            // Obtener la URI de la imagen seleccionada desde la galería
            selectedImageUri = data.getData();
            Toast.makeText(AgregarPaisaje.this, "Imagen agregada correctamente", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            // Obtener la imagen capturada por la cámara
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                selectedImageUri = getImageUri(imageBitmap);
                Toast.makeText(AgregarPaisaje.this, "Imagen agregada correctamente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Obtener la longitud y la latitud del objeto Location
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // Actualizar los valores de longitud y latitud en los EditText correspondientes
        EditText etLatitud = findViewById(R.id.etLatitud);
        EditText etLongitud = findViewById(R.id.etLongitud);
        etLatitud.setText(String.valueOf(latitude));
        etLongitud.setText(String.valueOf(longitude));
    }

    private void agregarContactoConImagen(String imageUrl) {
        String nombre = etNombre.getText().toString();

        if (!nombre.isEmpty()) {
            // Crear un nuevo objeto Contact con los datos ingresados
            Paisaje paisaje = new Paisaje();
            paisaje.setNamePaisaje(nombre);
            paisaje.setImgPaisaje("https://demo-upn.bit2bittest.com/" + imageUrl); // Asignar la URL de la imagen
            paisaje.setLatitude(latitude); // Asignar la latitud obtenida en onLocationChanged
            paisaje.setLongitude(longitude); // Asignar la longitud obtenida en onLocationChanged

            // Llamar al método create para guardar el nuevo contacto en MockAPI
            Call<Paisaje> createCall = paisajeService.create(paisaje);
            createCall.enqueue(new Callback<Paisaje>() {
                @Override
                public void onResponse(Call<Paisaje> call, Response<Paisaje> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AgregarPaisaje.this, "Contacto agregado correctamente", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad después de agregar el contacto
                    } else {
                        Toast.makeText(AgregarPaisaje.this, "Error al agregar el contacto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Paisaje> call, Throwable t) {
                    Toast.makeText(AgregarPaisaje.this, "Error al agregar el contacto", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AgregarPaisaje.this, "Ingrese un nombre y número válidos", Toast.LENGTH_SHORT).show();
        }
    }

    // ...
}



