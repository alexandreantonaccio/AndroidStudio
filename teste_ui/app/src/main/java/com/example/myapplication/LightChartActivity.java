package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Collections;
import java.util.List;

public class LightChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    private RecyclerView recyclerView;
    private TemperatureAdapter adapter;
    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // Atualizar a cada 5 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_chart);

        lineChart = findViewById(R.id.lineChart);
        recyclerView = findViewById(R.id.recyclerView);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TemperatureAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Iniciar a atualização periódica do gráfico e da lista
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

    private class FetchDataTask extends AsyncTask<Void, Void, FetchDataResult> {

        @Override
        protected FetchDataResult doInBackground(Void... voids) {
            ArrayList<Entry> entries = new ArrayList<>();
            List<String> temperatures = new ArrayList<>();

            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASSWORD);
                if (connection != null) {
                    // Consulta para obter as 10 últimas leituras
                    String query = "SELECT id, light FROM readings ORDER BY id DESC LIMIT 10";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    List<Float> temperatureList = new ArrayList<>();
                    while (resultSet.next()) {
                        temperatureList.add(resultSet.getFloat("light"));
                    }

                    // Adicionar os dados na ordem correta
                    int index = 0; // Índice para o gráfico
                    for (int i = temperatureList.size() - 1; i >= 0; i--) {
                        float light = temperatureList.get(i);
                        entries.add(new Entry(index++, light));
                        temperatures.add("Leitura " + index + ": " + light);
                    }

                    connection.close();
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

            return new FetchDataResult(entries, temperatures);
        }

        @Override
        protected void onPostExecute(FetchDataResult result) {
            if (!result.entries.isEmpty()) {
                LineDataSet dataSet = new LineDataSet(result.entries, "Luminosidade");
                dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                dataSet.setValueTextColor(getResources().getColor(android.R.color.holo_blue_dark));

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);

                // Configurar o gráfico
                Description description = new Description();
                description.setText("Últimas 10 leituras de luminosidade");
                lineChart.setDescription(description);
                lineChart.invalidate(); // Atualizar o gráfico
            }

            // Atualizar a RecyclerView
            adapter.updateData(result.temperatures);
        }
    }


    private class FetchDataResult {
        ArrayList<Entry> entries;
        List<String> temperatures;

        FetchDataResult(ArrayList<Entry> entries, List<String> temperatures) {
            this.entries = entries;
            this.temperatures = temperatures;
        }
    }
}
