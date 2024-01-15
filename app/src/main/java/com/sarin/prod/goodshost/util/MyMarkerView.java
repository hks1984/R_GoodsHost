package com.sarin.prod.goodshost.util;

import static com.sarin.prod.goodshost.util.StringUtil.replaceIntToPrice;

import com.sarin.prod.goodshost.R;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.Map;

public class MyMarkerView extends MarkerView {
    private TextView date, value;
    private Map<Integer, String> xValuesMap; // X 값과 문자열 매핑
    static StringUtil sUtil = StringUtil.getInstance();

    public MyMarkerView(Context context, int layoutResource, Map<Integer, String> xValuesMap) {
        super(context, layoutResource);
        date = findViewById(R.id.date);
        value = findViewById(R.id.value);
        this.xValuesMap = xValuesMap;
    }

    // 매번 MarkerView가 재사용될 때 호출됩니다.
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String xValue = xValuesMap.get((int)e.getX());
        date.setText(xValue);
        value.setText("" + sUtil.replaceStringPriceToInt((int)e.getY()) + "원");
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        // MarkerView가 클릭한 포인트에 정확히 위치하도록 조정
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
