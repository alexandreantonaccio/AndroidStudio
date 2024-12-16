package com.example.myapplication;

import org.json.JSONObject;

public class HighSoundChartActivity extends BaseChartActivity {

    @Override
    protected String getChartDescription() {
        return "Últimas 100 leituras de som alto";
    }

    @Override
    protected String getDataLabel() {
        return "Som alto";
    }

    @Override
    protected String getChartTitle() {
        return "Gráfico de Som alto";
    }

    @Override
    protected float parseData(JSONObject jsonObject) throws Exception {

        return 100;
    }
}