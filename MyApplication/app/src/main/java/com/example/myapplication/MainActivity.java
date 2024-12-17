package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.anastr.speedviewlib.SpeedView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    @FunctionalInterface
    private interface ConditionChecker {
        boolean checkCondition(float value);
    }
    private TextView statusTextView;
    private LinearLayout temperatureBox;
    private LinearLayout humidityBox;
    private LinearLayout pressureBox;
    private LinearLayout lightBox;
    private LinearLayout altitudeBox;
    private LinearLayout smokeBox;
    private LinearLayout highSoundBox;

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // Update every 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        statusTextView = findViewById(R.id.statusTextView);

        temperatureBox = findViewById(R.id.temperatureBox);
        humidityBox = findViewById(R.id.humidityBox);
        pressureBox = findViewById(R.id.pressureBox);
        lightBox = findViewById(R.id.lightBox);
        altitudeBox = findViewById(R.id.altitudeBox);
        smokeBox = findViewById(R.id.smokeBox);
        highSoundBox = findViewById(R.id.highSoundBox);

        temperatureBox.setBackgroundResource(R.drawable.box_background_disconnected);
        humidityBox.setBackgroundResource(R.drawable.box_background_disconnected);
        pressureBox.setBackgroundResource(R.drawable.box_background_disconnected);
        lightBox.setBackgroundResource(R.drawable.box_background_disconnected);
        altitudeBox.setBackgroundResource(R.drawable.box_background_disconnected);
        smokeBox.setBackgroundResource(R.drawable.box_background_disconnected);
        highSoundBox.setBackgroundResource(R.drawable.box_background_disconnected);

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

        private static final String API_URL = "http://kris-pc:8000/weather?limit=1";

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
                highSound = firstObject.getBoolean("high_sound") ? "Sim" : "Não";

                return "Successful connection!";
            } catch (Exception e) {
                return "Connection error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            statusTextView.setText("Status: " + result);

            if (result.equals("Successful connection!")) {
                // Atualiza os valores e os backgrounds dinamicamente
                updateSensorBox(temperatureBox, R.id.temperatureValue, "Temperatura: %.2f°C", temperature, value -> value > 30.0);
                updateSensorBox(humidityBox, R.id.humidityValue, "Umidade: %.2f%%", humidity, value -> value < 30.0 || value > 80.0);
                updateSensorBox(pressureBox, R.id.pressureValue, "Pressão: %.2fhPa", pressure, value -> false); // Sempre normal
                updateSensorBox(altitudeBox, R.id.altitudeValue, "Altitude: %.2fm", altitude, value -> false); // Sempre normal
                updateSensorBox(lightBox, R.id.lightValue, "Luminosidade: %.2flux", light, value -> value < 300.0 || value > 500.0);
                updateSensorBox(smokeBox, R.id.smokeValue, "Fumaça: %.2fppm", smoke, value -> value > 50.0);
                updateSensorBox(highSoundBox, R.id.highSoundValue, "Som Alto: %s", highSound.equals("Sim") ? 1.0f : 0.0f, value -> value > 0.0);
            } else {
                // Define o background de alerta no caso de erro de conexão
                setAllBoxesBackground(R.drawable.box_background_alert);
            }
        }

        // Método para atualizar valor e background de uma caixa de sensor
        private void updateSensorBox(LinearLayout box, int valueViewId, String format, float value, ConditionChecker checker) {
            TextView valueView = findViewById(valueViewId);
            valueView.setText(String.format(format, value));
            if (checker.checkCondition(value)) {
                box.setBackgroundResource(R.drawable.box_background_alert);
            } else {
                box.setBackgroundResource(R.drawable.box_background);
            }
        }

        // Método para definir o mesmo background para todas as caixas
        private void setAllBoxesBackground(int backgroundResId) {
            temperatureBox.setBackgroundResource(backgroundResId);
            humidityBox.setBackgroundResource(backgroundResId);
            pressureBox.setBackgroundResource(backgroundResId);
            lightBox.setBackgroundResource(backgroundResId);
            altitudeBox.setBackgroundResource(backgroundResId);
            smokeBox.setBackgroundResource(backgroundResId);
            highSoundBox.setBackgroundResource(backgroundResId);
        }

    }
}
