package com.example.spacebattle;

import android.content.Context;
import android.graphics.Canvas;

class Buttons {
    private int ButtonNum = 4;
    private Button[] arrButtons;
    Buttons(Context context){
        arrButtons = new Button[ButtonNum];
        String[] texts = {"开始","开火","自动","关闭"};
        for(int i = 0; i < ButtonNum; i++){
            arrButtons[i] = new Button(context);
            arrButtons[i].text = texts[i];
        }
    }
    // 定位所有按钮
    void pos() {
        arrButtons[0].centerX=135;
        arrButtons[0].centerY=1820;
        arrButtons[1].centerX=405;
        arrButtons[1].centerY=1820;
        arrButtons[2].centerX=675;
        arrButtons[2].centerY=1820;
        arrButtons[3].centerX=945;
        arrButtons[3].centerY=1820;
    }
    // 绘制所有按钮
    void draw(Canvas canvas) {
        for(Button button : arrButtons){
            button.draw(canvas);
        }
    }
    // 判断哪个按钮被点击(失败是None)
    String getPressedButton(float x,float y) {
        for(Button button : arrButtons){
            if(button.getPressed(x,y)) return button.text;
        }
        return "None";
    }
}

