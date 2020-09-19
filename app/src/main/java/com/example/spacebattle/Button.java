package com.example.spacebattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Button {
    Context context;
    private final float RADIUS = 80;  // 半径 （虚拟单位）
    private final int FONT_SIZE = 48; // 字体大小（虚拟单位）
    String text;               // 按钮文本
    float centerX;            // 虚拟坐标
    float centerY;
    private Paint paint1;
    private Paint paint2;
    Button(Context context){
        this.context = context;
        paint1 = new Paint();
        paint1.setColor(Color.argb(128,100,160,100));
        paint2 = new Paint();
        paint2.setColor(Color.argb(128,50,50,50));
        paint2.setTextSize(Global.v2R(FONT_SIZE)); //px
    }
    //  绘制这个按钮
    void draw(Canvas canvas){
        if(canvas == null) return;
        canvas.drawCircle(Global.v2Rx(centerX),Global.v2Ry(centerY),Global.v2R(RADIUS),paint1);         //圆的坐标是中心
        canvas.drawText(text,Global.v2Rx(centerX-FONT_SIZE),Global.v2Ry(centerY+FONT_SIZE/2),paint2); //文本的坐标是左下角
    }
    //  是否按到这个按钮
    boolean getPressed(float x, float y){
        return Math.sqrt((x-centerX)*(x-centerX)+(y-centerY)*(y-centerY))<RADIUS;
    }
}