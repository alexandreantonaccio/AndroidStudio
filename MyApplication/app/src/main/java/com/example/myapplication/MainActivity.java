package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.anastr.speedviewlib.SpeedView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;
    private SpeedView speedTemperature, speedHumidity, speedPressure, speedAltitude, speedLight, speedSmoke;

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // Atualizar a cada 5 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);

        // Inicializar os velocímetros
        speedTemperature = findViewById(R.id.speedTemperature);
        speedHumidity = findViewById(R.id.speedHumidity);
        speedPressure = findViewById(R.id.speedPressure);
        speedAltitude = findViewById(R.id.speedAltitude);
        speedLight = findViewById(R.id.speedLight);
        speedSmoke = findViewById(R.id.speedSmoke);

        // Iniciar atualizações automáticas
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

        private static final String DB_URL = "jdbc:postgresql://192.168.0.8:5432/postgres";
        private static final String USER = "postgres";
        private static final String PASSWORD = "password";

        private float temperature, humidity, pressure, altitude, light, smoke;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                if (connection != null) {
                    // Consulta para obter a última leitura
                    String query = "SELECT temperature, humidity, pressure, altitude, light, smoke " +
                            "FROM readings ORDER BY id DESC LIMIT 1";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    if (resultSet.next()) {
                        temperature = resultSet.getFloat("temperature");
                        humidity = resultSet.getFloat("humidity");
                        pressure = resultSet.getFloat("pressure");
                        altitude = resultSet.getFloat("altitude");
                        light = resultSet.getFloat("light");
                        smoke = resultSet.getFloat("smoke");
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
            if (result.equals("Conexão bem-sucedida!")) {
                // Atualizar os valores dos velocímetros
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
