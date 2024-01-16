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
        // 차트 뷰의 너비를 가져옵니다.
        float chartWidth = getChartView().getWidth();

        // MarkerView의 너비와 높이를 고려하여 우측 상단에 위치하도록 오프셋을 계산합니다.
        // 여기서 getWidth()는 MarkerView의 너비, getHeight()는 MarkerView의 높이를 반환합니다.
        return new MPPointF(chartWidth - getWidth(), -300 - getWidth());
    }


}
