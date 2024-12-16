package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.github.anastr.speedviewlib.SpeedView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // Update every 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        statusTextView = findViewById(R.id.statusTextView);

        // Sensor boxes and buttons
        Button temperatureGraphButton = findViewById(R.id.temperatureGraphButton);

        Button humidityGraphButton = findViewById(R.id.humidityGraphButton);

        Button pressureGraphButton = findViewById(R.id.pressureGraphButton);

        Button altitudeGraphButton = findViewById(R.id.altitudeGraphButton);

        Button lightGraphButton = findViewById(R.id.lightGraphButton);

        Button smokeGraphButton = findViewById(R.id.smokeGraphButton);

        Button highSoundGraphButton = findViewById(R.id.highSoundGraphButton);

        // Set click listeners for graph buttons
        temperatureGraphButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TemperatureChartActivity.class);
            startActivity(intent);
        });
        humidityGraphButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HumidityChartActivity.class);
            startActivity(intent);
        });
        pressureGraphButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PressureChartActivity.class);
            startActivity(intent);
        });
        altitudeGraphButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AltitudeChartActivity.class);
            startActivity(intent);
        });
        lightGraphButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LightChartActivity.class);
            startActivity(intent);
        });
        smokeGraphButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SmokeChartActivity.class);
            startActivity(intent);
        });

        highSoundGraphButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HighSoundChartActivity.class);
            startActivity(intent);
        });

        // Start automatic updates
        startUpdating();
    }


    private void startUpdating() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new FetchLatestReadingsTask().execute();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    private class FetchLatestReadingsTask extends AsyncTask<Void, Void, String> {

        private static final String API_URL = "http://172.20.10.2:8000/weather?limit=1";

        private float temperature, humidity, pressure, altitude, light, smoke;

        private String highSound;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() != 200) {
                    return "HTTP error code: " + connection.getResponseCode();
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                StringBuilder response = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }

                connection.disconnect();

                // Parse JSON response
                JSONArray jsonResponse = new JSONArray(response.toString());
                JSONObject firstObject = jsonResponse.getJSONObject(0);

                temperature = (float) firstObject.getDouble("temperature");
                humidity = (float) firstObject.getDouble("humidity");
                pressure = (float) firstObject.getDouble("pressure");
                altitude = (float) firstObject.getDouble("altitude");
                light = (float) firstObject.getDouble("light");
                smoke = (float) firstObject.getDouble("smoke");
                highSound = firstObject.getBoolean("high_sound") ? "Sim" : "Não" ;

                return "Successful connection!";

            } catch (Exception e) {
                return "Connection error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            statusTextView.setText("Status: " + result);
            if (result.equals("Successful connection!")) {
                // Update speedometer values
                ((TextView) findViewById(R.id.temperatureValue)).setText(String.format("Temperatura: %.2f°C", temperature));
                ((TextView) findViewById(R.id.humidityValue)).setText(String.format("Umidade: %.2f%%", humidity));
                ((TextView) findViewById(R.id.pressureValue)).setText(String.format("Pressão: %.2fhPa", pressure));
                ((TextView) findViewById(R.id.altitudeValue)).setText(String.format("Altitude: %.2fm", altitude));
                ((TextView) findViewById(R.id.lightValue)).setText(String.format("Luminosidade: %.2flux", light));
                ((TextView) findViewById(R.id.smokeValue)).setText(String.format("Fumaça: %.2fppm", smoke));
                ((TextView) findViewById(R.id.highSoundValue)).setText(String.format("Som Alto: %s", highSound));
            }
        }
    }
}