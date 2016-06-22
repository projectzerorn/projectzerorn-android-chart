package cn.mandata.react_native_mpchart;

import android.graphics.Color;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;


public class MPBarChartManager extends MPBarLineChartManager {
    private String CLASS_NAME="MPBarChart";
    @Override
    public String getName() {
        return this.CLASS_NAME;
    }

    @Override
    protected BarChart createViewInstance(ThemedReactContext reactContext) {
        BarChart chart=new BarChart(reactContext);

        return  chart;
    }

    //{XValues:[],YValues:[{Data:[],Label:""},{}]}
    @ReactProp(name="data")
    public void setData(BarChart chart,ReadableMap rm){
        Boolean isStacked = false;
        if(rm.hasKey("isStacked")){
            isStacked = rm.getBoolean("isStacked");
            if(isStacked){//处理StackedBarChart
                ReadableArray xArray=rm.getArray("xValues");
                ArrayList<String> xVals=new ArrayList<String>();
                for(int m=0;m<xArray.size();m++){
                    xVals.add(xArray.getString(m));
                }
                BarData barData=new BarData(xVals);

                ArrayList<BarEntry> entries=new ArrayList<BarEntry>();
                ArrayList<StackedBarData> list = new ArrayList<StackedBarData>();

                ReadableArray ra=rm.getArray("yValues");
                for(int i=0;i<xVals.size();i++) {
                    StackedBarData need2save = new StackedBarData();

                    for(int j=0;j<ra.size();j++){
                        ReadableMap map = ra.getMap(j);
                        ReadableArray data = map.getArray("data");

                        need2save.getData().add(data.getDouble(i));
                        need2save.getColors().add(map.getMap("config").getArray("color").getString(0));
                        need2save.getLabels().add(map.getString("label"));

                        if(map.hasKey("config")){
                            ReadableMap config = map.getMap("config");
                            if(config.hasKey("drawValues")){
                                need2save.setDrawValues(config.getBoolean("drawValues"));
                            }
                            if(config.hasKey("valueTextColor")){
                                need2save.setValueTextColor(config.getString("valueTextColor"));
                            }
                            if(config.hasKey("valueTextFontSize")){
                                need2save.setValueTextFontSize(config.getInt("valueTextFontSize"));
                            }
                        }
                    }

                    list.add(need2save);
                }

                for(int i=0;i<list.size();i++){
                    StackedBarData temp = list.get(i);
                    float[] tempY = new float[temp.getData().size()];
                    for(int j=0;j<tempY.length;j++){
                        tempY[j] = temp.getData().get(j).floatValue();
                    }
                    entries.add(new BarEntry(tempY, i));
                }
                BarDataSet dataSet = new BarDataSet(entries,"");
                ArrayList<Integer> colorsArrayList = new ArrayList<Integer>();
                ArrayList<String> collorArray = list.get(0).getColors();
                for(int m=0; m < collorArray.size(); m++){
                    colorsArrayList.add(Color.parseColor(collorArray.get(m)));
                }
                dataSet.setColors(colorsArrayList);
                if(list != null && list.size()>0){
                    StackedBarData temp = list.get(0);
                    if(temp.getLabels() != null){
                        dataSet.setStackLabels(temp.getLabels().toArray(new String[temp.getLabels().size()]));
                    }
                    if(temp.getDrawValues() !=null){
                        dataSet.setDrawValues(temp.getDrawValues());
                    }
                    if(temp.getValueTextFontSize() != null){
                        dataSet.setValueTextSize(temp.getValueTextFontSize());
                    }
                    if(temp.getValueTextColor() != null){
                        dataSet.setValueTextColor(Color.parseColor(temp.getValueTextColor()));
                    }

                }

                barData.addDataSet(dataSet);

                if (rm.hasKey("valueFormat")) {
                    barData.setValueFormatter(new ValueFormatter(rm.getString("valueFormat")));
                }
                chart.setData(barData);
                chart.invalidate();
                return;
            }
        }

        //处理一般的BarChart
        ReadableArray xArray=rm.getArray("xValues");
        ArrayList<String> xVals=new ArrayList<String>();
        for(int m=0;m<xArray.size();m++){
            xVals.add(xArray.getString(m));
        }
        ReadableArray ra=rm.getArray("yValues");
        BarData barData=new BarData(xVals);
        for(int i=0;i<ra.size();i++){
            ReadableMap map=ra.getMap(i);
            ReadableArray data=map.getArray("data");
            String label=map.getString("label");
            float[] vals=new float[data.size()];
            ArrayList<BarEntry> entries=new ArrayList<BarEntry>();
            for (int j=0;j<data.size();j++){
                vals[j]=(float)data.getDouble(j);
                BarEntry be=new BarEntry((float)data.getDouble(j),j);
                entries.add(be);
            }
        /*BarEntry be=new BarEntry(vals,i);
        entries.add(be);*/
            BarDataSet dataSet=new BarDataSet(entries,label);
            ReadableMap config= map.getMap("config");

            if(config.hasKey("color")) {
                ArrayList<Integer> colorsArrayList = new ArrayList<Integer>();
                ReadableArray collorArray = config.getArray("color");
                for(int k=0; k < collorArray.size(); k++){
                    colorsArrayList.add(Color.parseColor(collorArray.getString(k)));
                }
                dataSet.setColors(colorsArrayList);
            }

            if (config.hasKey("drawValues")) {
                dataSet.setDrawValues(config.getBoolean("drawValues"));
            }
            if (config.hasKey("valueTextFontSize")) {
                dataSet.setValueTextSize(config.getInt("valueTextFontSize"));
            }
            if (config.hasKey("valueTextColor")) {
                dataSet.setValueTextColor(Color.parseColor(config.getString("valueTextColor")));
            }
            barData.addDataSet(dataSet);
        }

        if (rm.hasKey("valueFormat")) {
            barData.setValueFormatter(new ValueFormatter(rm.getString("valueFormat")));
        }
        chart.setData(barData);
        chart.invalidate();
    }

    @ReactProp(name = "backgroundColor")
    public void setDescription(BarChart chart, String v) {
        if(v.startsWith("#")){
            chart.setBackgroundColor(Color.parseColor(v));
        }else{
            chart.setBackgroundColor(Color.parseColor("#00000000"));
        }

    }



    //StackedBar数据结构
    static class StackedBarData {
        ArrayList<Double>  data = new ArrayList<>();
        ArrayList<String> colors = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        Boolean           drawValues;
        Integer               valueTextFontSize;
        String            valueTextColor;

        public ArrayList<Double> getData() {
            return data;
        }

        public void setData(ArrayList<Double> data) {
            this.data = data;
        }

        public ArrayList<String> getColors() {
            return colors;
        }

        public void setColors(ArrayList<String> colors) {
            this.colors = colors;
        }

        public ArrayList<String> getLabels() {
            return labels;
        }

        public void setLabels(ArrayList<String> labels) {
            this.labels = labels;
        }

        public Boolean getDrawValues() {
            return drawValues;
        }

        public void setDrawValues(Boolean drawValues) {
            this.drawValues = drawValues;
        }

        public Integer getValueTextFontSize() {
            return valueTextFontSize;
        }

        public void setValueTextFontSize(Integer valueTextFontSize) {
            this.valueTextFontSize = valueTextFontSize;
        }

        public String getValueTextColor() {
            return valueTextColor;
        }

        public void setValueTextColor(String valueTextColor) {
            this.valueTextColor = valueTextColor;
        }
    }
}