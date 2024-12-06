package com.example.myapplication;

import org.json.JSONObject;

public class PressureChartActivity extends BaseChartActivity {

    @Override
    protected String getChartDescription() {
        return "Últimas 10 leituras de pressão";
    }

    @Override
    protected String getDataLabel() {
        return "Pressure";
    }

    @Override
    protected String getChartTitle() {
        return "Gráfico de Pressão";
    }

    @Override
    protected float parseData(JSONObject jsonObject) throws Exception {
        return (float) jsonObject.getDouble("pressure");
    }
}