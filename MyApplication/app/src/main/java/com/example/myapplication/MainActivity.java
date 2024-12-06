package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

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
    private SpeedView speedTemperature, speedHumidity, speedPressure, speedAltitude, speedLight, speedSmoke;

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // Update every 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);

        // Initialize speedometers
        speedTemperature = findViewById(R.id.speedTemperature);
        speedHumidity = findViewById(R.id.speedHumidity);
        speedPressure = findViewById(R.id.speedPressure);
        speedAltitude = findViewById(R.id.speedAltitude);
        speedLight = findViewById(R.id.speedLight);
        speedSmoke = findViewById(R.id.speedSmoke);

        // Start automatic updates
        startUpdating();

        // Set click listeners for speedometers to open respective chart activities
        speedTemperature.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, TemperatureChartActivity.class);
            startActivity(intent1);
        });
        speedHumidity.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity.this, HumidityChartActivity.class);
            startActivity(intent2);
        });
        speedPressure.setOnClickListener(v -> {
            Intent intent3 = new Intent(MainActivity.this, PressureChartActivity.class);
            startActivity(intent3);
        });
        speedAltitude.setOnClickListener(v -> {
            Intent intent4 = new Intent(MainActivity.this, AltitudeChartActivity.class);
            startActivity(intent4);
        });
        speedLight.setOnClickListener(v -> {
            Intent intent5 = new Intent(MainActivity.this, LightChartActivity.class);
            startActivity(intent5);
        });
        speedSmoke.setOnClickListener(v -> {
            Intent intent6 = new Intent(MainActivity.this, SmokeChartActivity.class);
            startActivity(intent6);
        });
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

        private static final String API_URL = "http://kris-pc.local:8000/weather?limit=1";

        private float temperature, humidity, pressure, altitude, light, smoke;

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
                speedTemperature.speedTo(temperature);
                speedHumidity.speedTo(humidity);
                speedPressure.speedTo(pressure);
                speedAltitude.speedTo(altitude);
                speedLight.speedTo(light);
                speedSmoke.speedTo(smoke);
            }
        }
    }
}