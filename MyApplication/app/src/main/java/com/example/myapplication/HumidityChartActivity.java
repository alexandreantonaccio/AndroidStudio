package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class HumidityChartActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity_chart);

        lineChart = findViewById(R.id.lineChart);

        // Iniciar a tarefa para buscar os dados do banco
        new FetchDataTask().execute();
    }

    private class FetchDataTask extends AsyncTask<Void, Void, ArrayList<Entry>> {

        private static final String DB_URL = "jdbc:postgresql://192.168.0.8:5432/postgres";
        private static final String USER = "postgres";
        private static final String PASSWORD = "password";

        @Override
        protected ArrayList<Entry> doInBackground(Void... voids) {
            ArrayList<Entry> entries = new ArrayList<>();

            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                if (connection != null) {
                    // Consulta para obter as 10 últimas leituras
                    String query = "SELECT id, humidity FROM readings ORDER BY id DESC LIMIT 10";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    int index = 0; // Índice para o gráfico
                    while (resultSet.next()) {
                        float temperature = resultSet.getFloat("humidity");
                        entries.add(new Entry(index++, temperature));
                    }

                    connection.close();
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            if (!entries.isEmpty()) {
                LineDataSet dataSet = new LineDataSet(entries, "Humidade");
                dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                dataSet.setValueTextColor(getResources().getColor(android.R.color.holo_blue_dark));

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);

                // Configurar o gráfico
                Description description = new Description();
                description.setText("Últimas 10 leituras de humidade");
                lineChart.setDescription(description);
                lineChart.invalidate(); // Atualizar o gráfico
            }
        }
    }
}
