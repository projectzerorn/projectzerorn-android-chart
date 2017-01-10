package cn.mandata.react_native_mpchart;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MPPieChartManager extends SimpleViewManager<PieChart> {
    private String CLASS_NAME = "MPPieChart";
    private boolean hasHoleFrame;
    private Context mContext;

    @Override
    public String getName() {
        return this.CLASS_NAME;
    }

    @Override
    protected PieChart createViewInstance(ThemedReactContext reactContext) {
        mContext = reactContext;
        PieChart chart = new PieChart(reactContext);
        chart.setHoleColorTransparent(true);
        return chart;
    }

    @ReactProp(name = "data")
    public void setData(PieChart chart, ReadableMap rm) {

        ReadableArray ra = rm.getArray("yValues");
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<Integer> colorsArrayList = new ArrayList<Integer>();
        for (int i = 0; i < ra.size(); i++) {
            ReadableMap temp = ra.getMap(i);

            if (temp.hasKey("data")) {
                yVals.add(new Entry((float) temp.getDouble("data"), i));
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

        ArrayList<String> xVals = new ArrayList<String>();
        if(rm.hasKey("xValues")){
            ReadableArray xArray = rm.getArray("xValues");
            for (int m = 0; m < xArray.size(); m++) {
                xVals.add(xArray.getString(m));
            }
        }else{
            for(int x = 0; x < dataSet.getEntryCount(); x++){//补齐数据避免报错One or more of the DataSet Entry arrays are longer than the x-values array of this ChartData object.
                xVals.add("");
            }
        }

        PieData pieData = new PieData(xVals, dataSet);
        if (rm.hasKey("valuesTextSize")) {
            pieData.setValueTextSize(rm.getInt("valuesTextSize"));
        }
        if (rm.hasKey("valueTextColor")) {
            pieData.setValueTextColor(Color.parseColor(rm.getString("valueTextColor")));
        }
        if (rm.hasKey("selected")){
            ReadableArray selectedArray =rm.getArray("selected");
            Highlight[] list = new Highlight[selectedArray.size()];
            for (int i = 0; i < selectedArray.size(); i++) {
                int temp = selectedArray.getInt(i);
                list[i] = new Highlight(temp, 0);
            }
            chart.highlightValues(list);
        }

        if(rm.hasKey("isShowValuesPercent") && rm.getBoolean("isShowValuesPercent")){//百分比数据

            if(rm.hasKey("isShowZero")){
                pieData.setValueFormatter(new PercentValueFormatter("#.#", rm.getBoolean("isShowZero")));
            }else{
                pieData.setValueFormatter(new PercentValueFormatter("#.#"));
            }

            if(rm.hasKey("showPercentAbove")){
                pieData.setValueFormatter(new PercentValueFormatter("#.#", rm.getDouble("showPercentAbove")));
            }

            chart.setUsePercentValues(true);

        }else{//正常数据

            if(rm.hasKey("isShowZero")){
                pieData.setValueFormatter(new NormalValueFormatter("#", rm.getBoolean("isShowZero")));
            }else{
                pieData.setValueFormatter(new NormalValueFormatter("#"));
            }

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
            float tempSize = temp.getInt("size") / 3 * UIUtil.getWindowDensity(mContext);
            boolean isWrap = temp.getBoolean("isWrap");
            if(isWrap){
                index++;
            }

            //文字颜色
            centerText.setSpan(new ForegroundColorSpan(tempColor), index, index+tempText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            //文字大小, //AbsoluteSizeSpan第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素
            centerText.setSpan(new AbsoluteSizeSpan(Math.round(tempSize),true), index, index+tempText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            index = index + tempText.length();
        }

        chart.setCenterText(centerText);
    }

    public class PercentValueFormatter implements ValueFormatter {
        private DecimalFormat mFormat;
        private boolean mIsShowZero = false;
        private double mShowPercentAbove = 0d;

        public PercentValueFormatter(String f, boolean isShowZero) {
            mFormat = new DecimalFormat(f);
            mIsShowZero = isShowZero;
        }
        public PercentValueFormatter(String f, double showPercentAbove) {
            mFormat = new DecimalFormat(f);
            mShowPercentAbove = showPercentAbove;
        }

        public PercentValueFormatter(String f) {
            mFormat = new DecimalFormat(f);
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            String ret = mFormat.format(value) + "%";
            if(value == 0){
                if(mIsShowZero){
                    ret = mFormat.format(value) + "%";
                }else{
                    ret = "";
                }
            }

            if(mShowPercentAbove > 0 && value < mShowPercentAbove*100d){
                ret = "";
            }

            return ret;
        }
    }

    public class NormalValueFormatter implements ValueFormatter {
        private DecimalFormat mFormat;
        private boolean mIsShowZero = false;

        public NormalValueFormatter(String f, boolean isShowZero) {
            mFormat = new DecimalFormat(f);
            mIsShowZero = isShowZero;
        }

        public NormalValueFormatter(String f) {
            mFormat = new DecimalFormat(f);
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            String ret = mFormat.format(value);
            if(value == 0){
                if(mIsShowZero){
                    ret = mFormat.format(value);
                }else{
                    ret = "";
                }
            }
            return ret;
        }
    }
}
