package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    private LinearLayout temperatureBox, humidityBox, pressureBox, altitudeBox, lightBox, smokeBox;

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // Update every 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);

        // Initialize speedometers
        temperatureBox = findViewById(R.id.temperatureBox);
        humidityBox = findViewById(R.id.humidityBox);
        pressureBox = findViewById(R.id.pressureBox);
        altitudeBox = findViewById(R.id.altitudeBox);
        lightBox = findViewById(R.id.lightBox);
        smokeBox = findViewById(R.id.smokeBox);

        // Start automatic updates
        startUpdating();

        // Set click listeners for speedometers to open respective chart activities
        temperatureBox.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, TemperatureChartActivity.class);
            startActivity(intent1);
        });
        humidityBox.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity.this, HumidityChartActivity.class);
            startActivity(intent2);
        });
        pressureBox.setOnClickListener(v -> {
            Intent intent3 = new Intent(MainActivity.this, PressureChartActivity.class);
            startActivity(intent3);
        });
        altitudeBox.setOnClickListener(v -> {
            Intent intent4 = new Intent(MainActivity.this, AltitudeChartActivity.class);
            startActivity(intent4);
        });
        lightBox.setOnClickListener(v -> {
            Intent intent5 = new Intent(MainActivity.this, LightChartActivity.class);
            startActivity(intent5);
        });
        smokeBox.setOnClickListener(v -> {
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

        private static final String API_URL = "http://kris-pc:8000/weather?limit=1";

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
                ((TextView) temperatureBox.findViewById(R.id.temperatureValue)).setText(String.format("Temperature: %.2f°C", temperature));
                ((TextView) humidityBox.findViewById(R.id.humidityValue)).setText(String.format("Humidity: %.2f%%", humidity));
                ((TextView) pressureBox.findViewById(R.id.pressureValue)).setText(String.format("Pressure: %.2fhPa", pressure));
                ((TextView) altitudeBox.findViewById(R.id.altitudeValue)).setText(String.format("Altitude: %.2fm", altitude));
                ((TextView) lightBox.findViewById(R.id.lightValue)).setText(String.format("Light: %.2flux", light));
                ((TextView) smokeBox.findViewById(R.id.smokeValue)).setText(String.format("Smoke: %.2fppm", smoke));
            }
        }
    }
}