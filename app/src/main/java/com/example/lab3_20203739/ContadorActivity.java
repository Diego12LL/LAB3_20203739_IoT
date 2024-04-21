package com.example.lab3_20203739;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.ArrayList;

public class ContadorActivity extends AppCompatActivity {

    private static final String TAG = "ContadorActivity";
    private TextView textoNumerosPrimos;
    private ArrayList<Integer> primesList;
    private int currentIndex = 0;
    private Handler handler = new Handler();
    private Button botonAscDesc;
    private Button botonReinPaus;
    private boolean isAscending = true;
    private boolean isPaused = false;
    private TextView textoIndicador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contador);
        textoNumerosPrimos = findViewById(R.id.textoNumerosPrimos);
        botonAscDesc = findViewById(R.id.botosAscDesc);
        botonReinPaus = findViewById(R.id.botonReinPaus);
        textoIndicador = findViewById(R.id.textoIndicador);
        botonAscDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAscDesc();
            }
        });
        botonReinPaus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleReinPaus();
            }
        });
        Button botonBuscar = findViewById(R.id.botonBuscar);
        EditText editTextBusqueda = findViewById(R.id.editTextBusqueda);
        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editTextBusqueda.getText().toString();
                if (!input.isEmpty()) {
                    int order = Integer.parseInt(input);
                    showPrimeByOrder(order);
                }
            }
        });
        new GetPrimesTask().execute();
    }

    private void toggleAscDesc() {
        isAscending = !isAscending;

        if (isAscending) {
            botonAscDesc.setText("Descender");
            textoIndicador.setText("Actualmente el contador está ascendiendo.");
        } else {
            botonAscDesc.setText("Ascender");
            textoIndicador.setText("Actualmente el contador está descendiendo.");
            showNextPrime();
        }
        showNextPrime();
    }

    private void toggleReinPaus() {
        isPaused = !isPaused;
        if (isPaused) {
            botonReinPaus.setText("Reiniciar");
            botonAscDesc.setVisibility(View.GONE);
            textoIndicador.setText("Actualmente el contador está en pausa.");
        } else {
            botonReinPaus.setText("Pausar");
            botonAscDesc.setVisibility(View.VISIBLE);
            textoIndicador.setText("Actualmente el contador sigue mostrando.");
            showNextPrime();
        }
    }

    private class GetPrimesTask extends AsyncTask<Void, Void, ArrayList<Integer>> {

        @Override
        protected ArrayList<Integer> doInBackground(Void... voids) {
            ArrayList<Integer> primesList = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://prime-number-api.onrender.com/primeNumbers?len=999&order=1");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                JSONArray jsonArray = new JSONArray(buffer.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject primeObj = jsonArray.getJSONObject(i);
                    int prime = primeObj.getInt("number");
                    primesList.add(prime);
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error al obtener números primos: " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error al cerrar el lector: " + e.getMessage());
                    }
                }
            }
            return primesList;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> result) {
            super.onPostExecute(result);
            if (result != null && !result.isEmpty()) {
                primesList = result;
                showNextPrime();
            } else {
                textoNumerosPrimos.setText("No se han encontrado números primos.");
            }
        }
    }

    private void showNextPrime() {
        if (!isPaused && currentIndex < primesList.size()) {
            int prime = primesList.get(isAscending ? currentIndex++ : currentIndex--);
            textoNumerosPrimos.setText(String.valueOf(prime));
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(this::showNextPrime, 1000);
        }
    }

    private void showPrimeByOrder(int order) {
        if (order > 0 && order <= primesList.size()) {
            currentIndex = order - 1;
            textoNumerosPrimos.setText(String.valueOf(primesList.get(currentIndex)));
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(this::showNextPrime, 1000);
        } else {
            Log.e(TAG, "El número ingresado está fuera de rango.");
        }
    }
}
