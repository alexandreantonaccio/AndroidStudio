package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

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
    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // Atualizar a cada 5 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity_chart);

        lineChart = findViewById(R.id.lineChart);

        // Iniciar a atualização periódica do gráfico
        startUpdating();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Parar atualizações quando a atividade for destruída
    }

    private void startUpdating() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new FetchDataTask().execute(); // Buscar os dados do banco
                handler.postDelayed(this, UPDATE_INTERVAL); // Agendar a próxima execução
            }
        }, UPDATE_INTERVAL);
    }

    private class FetchDataTask extends AsyncTask<Void, Void, ArrayList<Entry>> {

        @Override
        protected ArrayList<Entry> doInBackground(Void... voids) {
            ArrayList<Entry> tempEntries = new ArrayList<>();

            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASSWORD);
                if (connection != null) {
                    // Consulta para obter as 10 últimas leituras
                    String query = "SELECT id, humidity FROM readings ORDER BY id DESC LIMIT 10";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    while (resultSet.next()) {
                        float humidity = resultSet.getFloat("humidity");
                        tempEntries.add(new Entry(resultSet.getInt("id"), humidity));
                    }

                    connection.close();
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

            // Inverter a ordem das entradas
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = tempEntries.size() - 1; i >= 0; i--) {
                entries.add(tempEntries.get(i));
            }

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            if (!entries.isEmpty()) {
                LineDataSet dataSet = new LineDataSet(entries, "Umidade");
                dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                dataSet.setValueTextColor(getResources().getColor(android.R.color.holo_blue_dark));

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);

                // Configurar o gráfico
                Description description = new Description();
                description.setText("Últimas 10 leituras de umidade");
                lineChart.setDescription(description);
                lineChart.invalidate(); // Atualizar o gráfico
            }
        }
    }
}
