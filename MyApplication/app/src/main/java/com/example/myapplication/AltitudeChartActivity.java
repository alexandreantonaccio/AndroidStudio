package com.example.myapplication;

import org.json.JSONObject;

public class AltitudeChartActivity extends BaseChartActivity {

    @Override
    protected String getChartDescription() {
        return "Últimas 100 leituras de altitude";
    }

    @Override
    protected String getDataLabel() {
        return "Altitude";
    }

    @Override
    protected String getChartTitle() {
        return "Gráfico de Altitude";
    }

    @Override
    protected float parseData(JSONObject jsonObject) throws Exception {
        return (float) jsonObject.getDouble("altitude");
    }
}