package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public abstract class BaseChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    private String apiUrl;
    private String chartDescription;
    private String dataLabel;
    private String chartTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        lineChart = findViewById(R.id.lineChart);
        TextView titleView = findViewById(R.id.chartTitle);

        apiUrl = getApiUrl();
        chartDescription = getChartDescription();
        dataLabel = getDataLabel();
        chartTitle = getChartTitle();

        titleView.setText(chartTitle);

        // Iniciar a tarefa para buscar os dados da API
        new FetchDataTask().execute();
    }

    protected String getApiUrl() {
        return "http://kris-pc.local:8000/weather?limit=100"; // Replace with actual API URL
    }

    protected abstract String getChartDescription();
    protected abstract String getDataLabel();
    protected abstract String getChartTitle();
    protected abstract float parseData(JSONObject jsonObject) throws Exception;

    private class FetchDataTask extends AsyncTask<Void, Void, ArrayList<Entry>> {

        @Override
        protected ArrayList<Entry> doInBackground(Void... voids) {
            ArrayList<Entry> entries = new ArrayList<>();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());

                int index = 0; // Índice para o gráfico
                int length = 100;
                for (int i = length - 1; i >= 0; i--) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    float value = parseData(jsonObject);
                    entries.add(new Entry(index++, value));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            if (!entries.isEmpty()) {
                LineDataSet dataSet = new LineDataSet(entries, dataLabel);
                dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                dataSet.setValueTextColor(getResources().getColor(android.R.color.holo_blue_dark));

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);

                // Configurar o gráfico
                Description description = new Description();
                description.setText(chartDescription);
                lineChart.setDescription(description);
                lineChart.invalidate(); // Atualizar o gráfico
            }
        }
    }
}