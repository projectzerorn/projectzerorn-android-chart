package cn.mandata.react_native_mpchart;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.ThemedReactContext;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieRadarChartBase;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import android.graphics.Color;
import android.text.Spanned;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.AbsoluteSizeSpan;

import java.util.ArrayList;

public class MPPieChartManager extends SimpleViewManager<PieChart> {
    private String CLASS_NAME = "MPPieChart";
    private boolean hasHoleFrame;

    @Override
    public String getName() {
        return this.CLASS_NAME;
    }

    @Override
    protected PieChart createViewInstance(ThemedReactContext reactContext) {
        PieChart chart = new PieChart(reactContext);
        return chart;
    }

    @ReactProp(name = "data")
    public void setData(PieChart chart, ReadableMap rm) {

        ReadableArray xArray = rm.getArray("xValues");
        ArrayList<String> xVals = new ArrayList<String>();
        for (int m = 0; m < xArray.size(); m++) {
            xVals.add(xArray.getString(m));
        }

        ReadableArray ra = rm.getArray("yValues");
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<Integer> colorsArrayList = new ArrayList<Integer>();
        for (int i = 0; i < ra.size(); i++) {
            ReadableMap temp = ra.getMap(i);

            if (temp.hasKey("data")) {
                yVals.add(new Entry((float) temp.getInt("data"), i));
            }
            if (temp.hasKey("color")) {
                colorsArrayList.add(Color.parseColor(temp.getString("color")));
            }
        }

        PieDataSet dataSet = new PieDataSet(yVals, "");

        dataSet.setColors(colorsArrayList);

        if (rm.hasKey("sliceSpace")) {
            dataSet.setSliceSpace(rm.getInt("sliceSpace"));
        }
        if (rm.hasKey("selectionShift")) {
            dataSet.setSelectionShift(rm.getInt("selectionShift"));
        }
        if (rm.hasKey("drawValues")) {
            dataSet.setDrawValues(rm.getBoolean("drawValues"));
        }

        PieData pieData = new PieData(xVals, dataSet);
        if (rm.hasKey("valuesTextSize")) {
            pieData.setValueTextSize(rm.getInt("valuesTextSize"));
        }
        if (rm.hasKey("valueTextColor")) {
            pieData.setValueTextColor(Color.parseColor(rm.getString("valueTextColor")));
        }
        chart.setData(pieData);
        chart.setRotationEnabled(false); // 不可以手动旋转
        chart.invalidate();
    }

    @ReactProp(name = "description")
    public void setDescription(PieChart chart, String v) {
        chart.setDescription(v);
    }

    @ReactProp(name = "drawSliceText")
    public void setDescription(PieChart chart, boolean v) {
        chart.setDrawSliceText(v);
    }

    @ReactProp(name="legend")
    public void setLegend(PieChart chart,ReadableMap v){
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
    public void setTouchEnabled(PieChart chart, boolean enable) {
        chart.setTouchEnabled(enable);
    }

    @ReactProp(name="hasAnimate",defaultBoolean = true)
    public void setHasAnimate(PieChart chart, boolean enable) {
        if(enable){
            chart.animateY(1000, Easing.EasingOption.EaseInOutQuad); //设置动画
        }
    }

    @ReactProp(name = "holeRadius")
    public void setHasAnimate(PieChart chart, int v) {
        chart.setHoleRadius(v); //半径
        if (v == 0) {
            chart.setTransparentCircleRadius(0);// 半透明圈
        }else if(hasHoleFrame){
            chart.setTransparentCircleRadius(v+5);
        }
    }

    @ReactProp(name="hasHoleFrame",defaultBoolean = true)
    public void setHasHoleTransparent(PieChart chart, boolean v) {
        hasHoleFrame = v;
        if(!v){
            chart.setTransparentCircleRadius(chart.getHoleRadius());
        }
    }

    @ReactProp(name="centerText")
    public void setCenterText(PieChart chart, ReadableArray ra) {
        String allStr = "";
        for (int i = 0; i < ra.size(); i++) {
            ReadableMap temp = ra.getMap(i);
            String tempText = temp.getString("text");
            Integer tempColor = Color.parseColor(temp.getString("color"));
            int tempSize = temp.getInt("size");
            boolean isWrap = temp.getBoolean("isWrap");

            if(allStr.length() == 0){
                allStr = tempText;
            }else{
                if(isWrap){
                    allStr = allStr + "\n" + tempText;
                }else{
                    allStr = allStr + tempText;
                }

            }
        }
        SpannableString centerText = new SpannableString(allStr);

        int index = 0;
        for (int i = 0; i < ra.size(); i++) {
            ReadableMap temp = ra.getMap(i);
            String tempText = temp.getString("text");
            Integer tempColor = Color.parseColor(temp.getString("color"));
            int tempSize = temp.getInt("size");
            boolean isWrap = temp.getBoolean("isWrap");
            if(isWrap){
                index++;
            }

            //文字颜色
            centerText.setSpan(new ForegroundColorSpan(tempColor), index, index+tempText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            //文字大小, //AbsoluteSizeSpan第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素
            centerText.setSpan(new AbsoluteSizeSpan(tempSize,true), index, index+tempText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            index = index + tempText.length();
        }

        chart.setCenterText(centerText);
    }
}
