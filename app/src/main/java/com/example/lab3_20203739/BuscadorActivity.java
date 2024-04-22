package com.example.lab3_20203739;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BuscadorActivity extends AppCompatActivity {

    private TextView pelicula, director, actores, fecha, genero, escritor, descripcion, intMocRat, rotTomRat, metaRat;
    private CheckBox checkBoxConforme;
    private Button botonRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showToast("Buscador de Películas");

        setContentView(R.layout.buscador_peliculas);
        pelicula = findViewById(R.id.titulo);
        director = findViewById(R.id.idDirector);
        actores = findViewById(R.id.idActores);
        fecha = findViewById(R.id.idFecha);
        genero = findViewById(R.id.idGenero);
        escritor = findViewById(R.id.idEscritor);
        descripcion = findViewById(R.id.idDescripcion);
        intMocRat = findViewById(R.id.ratIntMovDat);
        rotTomRat = findViewById(R.id.ratRotTom);
        metaRat = findViewById(R.id.ratMetacritic);
        checkBoxConforme = findViewById(R.id.checkBoxConforme);
        botonRegresar = findViewById(R.id.botonRegresar);
        botonRegresar.setVisibility(View.GONE);

        checkBoxConforme.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    botonRegresar.setVisibility(View.VISIBLE);
                } else {
                    botonRegresar.setVisibility(View.GONE);
                }
            }
        });

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuscadorActivity.this, InicioActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String idPelicula = intent.getStringExtra("ID_PELICULA");

        if (idPelicula != null) {
            String omdbApiUrl = "https://www.omdbapi.com/?apikey=bf81d461&i=" + idPelicula;
            new FetchMovieTask().execute(omdbApiUrl);
        } else {
            Toast.makeText(this, "No se encontró ID de película", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchMovieTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bufferedReader.close();
                inputStream.close();
                connection.disconnect();

                return new JSONObject(stringBuilder.toString());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            if (jsonObject != null) {
                try {
                    pelicula.setText(jsonObject.getString("Title"));
                    director.setText(jsonObject.getString("Director"));
                    actores.setText(jsonObject.getString("Actors"));
                    fecha.setText(jsonObject.getString("Released"));
                    genero.setText(jsonObject.getString("Genre"));
                    escritor.setText(jsonObject.getString("Writer"));
                    descripcion.setText(jsonObject.getString("Plot"));

                    try {
                        JSONArray ratingsArray = jsonObject.getJSONArray("Ratings");

                        for (int i = 0; i < ratingsArray.length(); i++) {
                            JSONObject ratingObject = ratingsArray.getJSONObject(i);
                            String source = ratingObject.getString("Source");
                            String value = ratingObject.getString("Value");

                            if (source.equals("Internet Movie Database")) {
                                intMocRat.setText(value);
                            } else if (source.equals("Rotten Tomatoes")) {
                                rotTomRat.setText(value);
                            } else if (source.equals("Metacritic")) {
                                metaRat.setText(value);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(BuscadorActivity.this, "Error al obtener la información de la película", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
