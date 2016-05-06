package cn.mandata.react_native_mpchart;

import android.graphics.Color;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieRadarChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;

import java.util.ArrayList;
import java.util.Random;

public class MPRadarChartManager extends SimpleViewManager<RadarChart> {
    private String CLASS_NAME = "MPRadarChart";

    @Override
    public String getName() {
        return this.CLASS_NAME;
    }

    @Override
    protected RadarChart createViewInstance(ThemedReactContext reactContext) {
        RadarChart chart = new RadarChart(reactContext);
        return chart;
    }

    @ReactProp(name = "data")
    public void setData(RadarChart chart, ReadableMap rm) {

        ReadableArray xArray = rm.getArray("labels");
        ArrayList<String> xVals = new ArrayList<String>();
        for (int m = 0; m < xArray.size(); m++) {
            String temp = xArray.getString(m);
            //文字超长用...替换
            if(temp.length() > 6){
                temp = temp.substring(0, 6) + "...";
            }
            xVals.add(temp);
        }

        RadarData chartData = new RadarData(xVals);

        ReadableArray ra = rm.getArray("dataSets");
        for (int i = 0; i < ra.size(); i++) {
            ReadableMap map = ra.getMap(i);
            ReadableArray data = map.getArray("data");
            String label = map.getString("label");
            ArrayList<Entry> entries = new ArrayList<Entry>();
            for (int j = 0; j < data.size(); j++) {
                Entry be = new Entry((float) data.getDouble(j), j);
                entries.add(be);
            }

            RadarDataSet set = new RadarDataSet(entries, label);
            set.setColor(Color.parseColor(map.getArray("colors").getString(0)));
            if(map.hasKey("drawFilledEnabled")){
                set.setDrawFilled(map.getBoolean("drawFilledEnabled"));
            }
            if(map.hasKey("drawValues")){
                set.setDrawValues(map.getBoolean("drawValues"));
            }


            chartData.addDataSet(set);
        }

        chart.setData(chartData);

        chart.getYAxis().setStartAtZero(true);//蛛网由0开始
        chart.getYAxis().setEnabled(false);//去掉刻度数值显示
        chart.invalidate();
    }

    @ReactProp(name = "description")
    public void setDescription(RadarChart chart, String v) {
        chart.setDescription(v);
    }

    @ReactProp(name = "webCount")
    public void setWebCount(RadarChart chart, int v) {
        chart.getYAxis().setLabelCount(v + 1, true);
    }

    @ReactProp(name = "webLineWidth")
    public void setWebLineWidth(RadarChart chart, float v) {
        chart.setWebLineWidth(v);
    }

    @ReactProp(name = "webColor")
    public void setWebColor(RadarChart chart, String v) {
        chart.setWebColor(Color.parseColor(v));
    }

    @ReactProp(name = "innerWebLineWidth")
    public void setInnerWebLineWidth(RadarChart chart, float v) {
        chart.setWebLineWidthInner(v);
    }

    @ReactProp(name = "innerWebColor")
    public void setInnerWebColor(RadarChart chart, String v) {
        chart.setWebColorInner(Color.parseColor(v));
    }

    @ReactProp(name="legend")
    public void setLegend(RadarChart chart,ReadableMap v){
        Legend legend=chart.getLegend();
        if(v.hasKey("enable")) legend.setEnabled(v.getBoolean("enable"));
        if(v.hasKey("position"))  legend.setPosition(Legend.LegendPosition.valueOf(v.getString("position")));
        if(v.hasKey("direction"))  legend.setDirection(Legend.LegendDirection.valueOf(v.getString("direction")));

        if(v.hasKey("legendForm"))  legend.setForm(Legend.LegendForm.valueOf(v.getString("legendForm")));

        if(v.hasKey("textColor"))  legend.setTextColor(Color.parseColor(v.getString("textColor")));
        if(v.hasKey("textSize"))  legend.setTextSize((float) v.getDouble("textSize"));
        if(v.hasKey("xOffset"))  legend.setXOffset((float) v.getDouble("xOffset"));
        if(v.hasKey("yOffset"))  legend.setYOffset((float) v.getDouble("yOffset"));

        if(v.hasKey("custom")){
            ReadableMap custom=v.getMap("custom");
            ReadableArray colors=custom.getArray("colors");
            ReadableArray labels=custom.getArray("labels");
            if(colors.size()==labels.size()) {
                int[] cols = new int[colors.size()];
                String[] labs = new String[colors.size()];
                for (int j = 0; j < colors.size(); j++) {
                    cols[j] = Color.parseColor(colors.getString(j));
                    labs[j] = labels.getString(j);
                }
                legend.setCustom(cols,labs);
            }
        }
    }

    @ReactProp(name="touchEnabled",defaultBoolean = true)
    public void setTouchEnabled(RadarChart chart, boolean enable) {
        chart.setTouchEnabled(enable);
    }

    @ReactProp(name="xAxis")
    public  void  setXAxis(RadarChart chart,ReadableMap v){
        XAxis x= chart.getXAxis();
        setAxisInfo(x, v);
        setXAxisInfo(x, v);
    }

    private void setAxisInfo(AxisBase axis,ReadableMap v){
        if(v.hasKey("enable")) axis.setEnabled(v.getBoolean("enable"));
        if(v.hasKey("drawAxisLine")) axis.setDrawAxisLine(v.getBoolean("drawAxisLine"));

        if(v.hasKey("drawGridLines")) axis.setDrawGridLines(v.getBoolean("drawGridLines"));
        if(v.hasKey("drawLabels")) axis.setDrawLabels(v.getBoolean("drawLabels"));

        if(v.hasKey("textColor")) axis.setTextColor(Color.parseColor(v.getString("textColor")));
        if(v.hasKey("gridColor")) axis.setGridColor(Color.parseColor(v.getString("gridColor")));

        if(v.hasKey("gridLineWidth")) axis.setGridLineWidth((float)v.getDouble("gridLineWidth"));
        if(v.hasKey("axisLineColor")) axis.setAxisLineColor(Color.parseColor(v.getString("axisLineColor")));
        if(v.hasKey("axisLineWidth")) axis.setAxisLineWidth((float)(v.getDouble("axisLineWidth")));
        if(v.hasKey("gridDashedLine")) {
            ReadableMap gdl=v.getMap("gridDashedLine");
            axis.enableGridDashedLine((float)gdl.getDouble("lineLength"),
                    (float)gdl.getDouble("spaceLength"),
                    (float)gdl.getDouble("phase"));
        }

    }

    private  void setXAxisInfo(XAxis axis,ReadableMap v){

        if(v.hasKey("labelRotationAngle")) axis.setLabelRotationAngle((float) v.getDouble("labelRotationAngle"));
        if(v.hasKey("spaceBetweenLabels")) axis.setSpaceBetweenLabels(v.getInt("spaceBetweenLabels"));
        if(v.hasKey("labelsToSkip")) axis.setLabelsToSkip(v.getInt("labelsToSkip"));
        if(v.hasKey("position")) {
            String name=v.getString("position");
            axis.setPosition(XAxis.XAxisPosition.valueOf(name));
        }
    }

}
