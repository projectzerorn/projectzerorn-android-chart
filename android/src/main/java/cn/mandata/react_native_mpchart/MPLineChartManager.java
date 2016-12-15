package cn.mandata.react_native_mpchart;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2015/11/6.
 */
public class MPLineChartManager extends MPBarLineChartManager {
    private String CLASS_NAME="MPLineChart";
    private Random random;//用于产生随机数

    private LineChart chart;
    private LineData data;
    private LineDataSet dataSet;
    @Override
    public String getName() {
        return this.CLASS_NAME;
    }

    @Override
    protected LineChart createViewInstance(ThemedReactContext reactContext) {
        LineChart chart=new LineChart(reactContext);

        return  chart;
    }

    //{XValues:[],YValues:[{Data:[],Label:""},{}]}
    @ReactProp(name="data")
    public void setData(LineChart chart,ReadableMap rm){

        ReadableArray xArray=rm.getArray("xValues");
        ArrayList<String> xVals=new ArrayList<String>();
        for(int m=0;m<xArray.size();m++){
            xVals.add(xArray.getString(m));
        }
        ReadableArray ra=rm.getArray("yValues");
        LineData chartData=new LineData(xVals);
        for(int i=0;i<ra.size();i++){
            ReadableMap map=ra.getMap(i);
            ReadableArray data=map.getArray("data");
            String label=map.getString("label");
            float[] vals=new float[data.size()];
            ArrayList<Entry> entries=new ArrayList<Entry>();
            for (int j=0;j<data.size();j++){
                vals[j]=(float)data.getDouble(j);
                Entry be=new Entry((float)data.getDouble(j),j);
                entries.add(be);
            }
            /*BarEntry be=new BarEntry(vals,i);
            entries.add(be);*/
            ReadableMap config= map.getMap("config");
            LineDataSet dataSet=new LineDataSet(entries,label);
            if(config.hasKey("drawCircles")) dataSet.setDrawCircles(config.getBoolean("drawCircles"));
            if(config.hasKey("drawCubic")) dataSet.setDrawCubic(config.getBoolean("drawCubic"));
            if(config.hasKey("circleSize")) dataSet.setCircleSize((float) config.getDouble("circleSize"));
            if(config.hasKey("color")) {
                int[] colors=new int[]{Color.parseColor(config.getString("color"))};
                dataSet.setColors(colors);
            }
            if(config.hasKey("circleHoleColor")){
                dataSet.setCircleColorHole(Color.parseColor(config.getString("circleHoleColor")));
            }
            if(config.hasKey("circleColors")){
                dataSet.setCircleColors(new int[]{Color.parseColor(config.getString("circleColors"))});
            }
            if(config.hasKey("drawFilled")){
                dataSet.setDrawFilled(config.getBoolean("drawFilled"));
            }
            if(config.hasKey("fillColor")){
                dataSet.setFillColor(Color.parseColor(config.getString("fillColor")));
            }
            if(config.hasKey("fillColorTop") && config.hasKey("fillColorBottom")){
                GradientDrawable drawable = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {Color.parseColor(config.getString("fillColorTop")),
                                    Color.parseColor(config.getString("fillColorBottom"))});
                dataSet.setFillDrawable(drawable);
            }
            if(config.hasKey("fillAlpha")){
                int fillAlpha = (int) (config.getDouble("fillAlpha")*255);
                dataSet.setFillAlpha(fillAlpha);
            }

            if(config.hasKey("lineWidth")){
                dataSet.setLineWidth(config.getInt("lineWidth"));
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
            chartData.addDataSet(dataSet);
        }

        if (rm.hasKey("valueFormat")) {
            chartData.setValueFormatter(new ValueFormatter(rm.getString("valueFormat")));
        }
        chart.setData(chartData);
        chart.invalidate();
    }

    @ReactProp(name = "backgroundColor")
    public void setDescription(LineChart chart, String v) {
        if(v.startsWith("#")){
            chart.setBackgroundColor(Color.parseColor(v));
        }else{
            chart.setBackgroundColor(Color.parseColor("#00000000"));
        }

    }
}
