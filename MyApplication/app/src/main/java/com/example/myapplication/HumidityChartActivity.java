package com.example.myapplication;

import org.json.JSONObject;

public class HumidityChartActivity extends BaseChartActivity {
    @Override
    protected String getChartDescription() {
        return "Últimas 10 leituras de umidade";
    }

    @Override
    protected String getDataLabel() {
        return "Humidity";
    }

    @Override
    protected String getChartTitle() {
        return "Gráfico de Umidade";
    }

    @Override
    protected float parseData(JSONObject jsonObject) throws Exception {
        return (float) jsonObject.getDouble("humidity");
    }
}