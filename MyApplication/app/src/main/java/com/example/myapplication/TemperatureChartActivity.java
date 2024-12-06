package com.example.myapplication;

import org.json.JSONObject;

public class TemperatureChartActivity extends BaseChartActivity {

    @Override
    protected String getChartDescription() {
        return "Últimas 10 leituras de temperatura";
    }

    @Override
    protected String getDataLabel() {
        return "Temperature";
    }

    @Override
    protected String getChartTitle() {
        return "Gráfico de Temperatura";
    }

    @Override
    protected float parseData(JSONObject jsonObject) throws Exception {
        return (float) jsonObject.getDouble("temperature");
    }
}