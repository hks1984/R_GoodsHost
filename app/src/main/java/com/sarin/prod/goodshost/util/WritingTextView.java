package com.sarin.prod.goodshost.util;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

public class WritingTextView extends View {
    private Paint paint;
    private Path path;
    private float length;

    public WritingTextView(Context context) {
        super(context);
        init();
    }

    public WritingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WritingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);

        path = new Path();

        // 텍스트 경로 생성
        String text = "Deal\nDive";
        Paint textPaint = new Paint();
        textPaint.setTextSize(150); // 텍스트 사이즈 설정
        textPaint.getTextPath(text, 0, text.length(), 0, 100, path); // 텍스트에 해당하는 경로를 Path 객체에 추가

        // Path의 전체 길이 계산
        PathMeasure measure = new PathMeasure(path, false);
        length = measure.getLength();

        // 애니메이션 생성 및 시작
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                paint.setPathEffect(new DashPathEffect(new float[]{length, length}, length * (1 - animatedValue)));
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }
}
