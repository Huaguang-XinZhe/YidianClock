package com.example.yidianClock;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextBGSpan extends ReplacementSpan {
    int bgColor;
    int size;
    int radius = 12;

    public TextBGSpan(int bgColor) {
        this.bgColor = bgColor;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text,
                       int start, int end, @Nullable Paint.FontMetricsInt fm) {
        size = (int) (paint.measureText(text, start, end) + 2 * radius);
        return size;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

        paint.setColor(bgColor);
        //设置画笔的锯齿效果
        paint.setAntiAlias(true);
        //设置文字背景矩形，x为span左上角相对整个TextView的x值，y为span左上角相对整个View的y值。
        // paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        RectF oval = new RectF(x, y + paint.ascent(), x + size, y + paint.descent());
        //不能直接用这个，全部都会变白，根本看不到效果
//        RectF oval = new RectF(x, y, x + size, y);
        //绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
        canvas.drawRoundRect(oval, radius, radius, paint);
        paint.setColor(Color.WHITE);//恢复画笔的文字颜色
        canvas.drawText(text, start, end, x + radius, y, paint);//绘制文字

    }


}
