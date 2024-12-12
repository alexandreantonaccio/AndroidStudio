package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;
    private LinearLayout temperatureBox, humidityBox, pressureBox, altitudeBox, lightBox, smokeBox;

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // Atualizar a cada 5 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);

        // Inicializar os velocímetros
        temperatureBox = findViewById(R.id.temperatureBox);
        humidityBox = findViewById(R.id.humidityBox);
        pressureBox = findViewById(R.id.pressureBox);
        altitudeBox = findViewById(R.id.altitudeBox);
        lightBox = findViewById(R.id.lightBox);
        smokeBox = findViewById(R.id.smokeBox);

        // Iniciar atualizações automáticas
        startUpdating();

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

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASSWORD); // Usando Config
                if (connection != null) {
                    // Consulta para obter a última leitura
                    String query = "SELECT temperature, humidity, pressure, altitude, light, smoke " +
                            "FROM readings ORDER BY id DESC LIMIT 1";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    if (resultSet.next()) {
                        float temperature = resultSet.getFloat("temperature");
                        float humidity = resultSet.getFloat("humidity");
                        float pressure = resultSet.getFloat("pressure");
                        float altitude = resultSet.getFloat("altitude");
                        float light = resultSet.getFloat("light");
                        float smoke = resultSet.getFloat("smoke");

                        // Atualizando os valores dos velocímetros
                        runOnUiThread(() -> {
                            ((TextView) temperatureBox.findViewById(R.id.temperatureValue)).setText(String.format("Temperature: %.2f°C", temperature));
                            ((TextView) humidityBox.findViewById(R.id.humidityValue)).setText(String.format("Humidity: %.2f%%", humidity));
                            ((TextView) pressureBox.findViewById(R.id.pressureValue)).setText(String.format("Pressure: %.2fhPa", pressure));
                            ((TextView) altitudeBox.findViewById(R.id.altitudeValue)).setText(String.format("Altitude: %.2fm", altitude));
                            ((TextView) lightBox.findViewById(R.id.lightValue)).setText(String.format("Light: %.2flux", light));
                            ((TextView) smokeBox.findViewById(R.id.smokeValue)).setText(String.format("Smoke: %.2fppm", smoke));
                        });
                    }

                    connection.close();
                    return "Conexão bem-sucedida!";
                } else {
                    return "Falha na conexão.";
                }
            } catch (ClassNotFoundException e) {
                return "Driver não encontrado: " + e.getMessage();
            } catch (SQLException e) {
                return "Erro de conexão: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            statusTextView.setText("Status: " + result);
        }
    }
}


