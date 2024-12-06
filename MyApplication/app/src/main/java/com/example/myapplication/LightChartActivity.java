package com.example.myapplication;

import org.json.JSONObject;

public class LightChartActivity extends BaseChartActivity {

    @Override
    protected String getChartDescription() {
        return "Últimas 10 leituras de luminosidade";
    }

    @Override
    protected String getDataLabel() {
        return "Light";
    }

    @Override
    protected String getChartTitle() {
        return "Gráfico de Luminosidade";
    }

    @Override
    protected float parseData(JSONObject jsonObject) throws Exception {
        return (float) jsonObject.getDouble("light");
    }
}