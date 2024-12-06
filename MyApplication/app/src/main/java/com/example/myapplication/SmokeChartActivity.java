package com.example.myapplication;

import org.json.JSONObject;

public class SmokeChartActivity extends BaseChartActivity {

    @Override
    protected String getChartDescription() {
        return "Últimas 10 leituras de fumaça";
    }

    @Override
    protected String getDataLabel() {
        return "Smoke";
    }

    @Override
    protected String getChartTitle() {
        return "Gráfico de Fumaça";
    }

    @Override
    protected float parseData(JSONObject jsonObject) throws Exception {
        return (float) jsonObject.getDouble("smoke");
    }
}