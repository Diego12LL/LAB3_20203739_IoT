package com.example.lab3_20203739;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.inicio);


        if(isNetworkAvailable()) {
            showToast("Conexión a internet establecida");
        } else {
            showToast("No hay conexión a Internet");
        }
        showToast("Página principal de la app");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.botonVisualizar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button botonVisualizar = findViewById(R.id.botonVisualizar);
        Button botonBuscar = findViewById(R.id.botonBuscar);

        EditText editTextBusqueda = findViewById(R.id.editTextBusqueda);

        botonVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioActivity.this, ContadorActivity.class);
                startActivity(intent);
            }
        });

        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idPelicula = editTextBusqueda.getText().toString().trim();

                Intent intent = new Intent(InicioActivity.this, BuscadorActivity.class);
                intent.putExtra("ID_PELICULA", idPelicula);
                startActivity(intent);
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
