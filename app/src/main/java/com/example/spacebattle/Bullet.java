package com.example.spacebattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Bullet {
    final float RADIUS = 5;   // 子弹半径
    String spName;            // 精灵名称（它发出的子弹）
    float x;                  // 子弹x轴定位（left）
    float y;                  // 子弹y轴定位（top）
    float dir;                // 移动方向，单位：degree
    float step = 10;          // 移动步幅 取值1~ Any
    boolean me = false;       // 是否是本玩家发出的子弹
    boolean active = true;    // 是否活动（击中精灵或离开画面则变为非活动）
    private Paint paint1;     // 绘制子弹的画笔
    Context context;

    // 构造子弹
    Bullet(Context context){
        this.context = context;
        paint1 = new Paint();
        paint1.setColor(Color.RED);
    }

    // 计算子弹下一帧的位置
    private void pos(long loopTime){
        float step1 = this.step * loopTime/Global.LOOP_TIME;
        x = x + step1 * (float)Math.cos(dir * Math.PI /180);
        y = y + step1 * (float)Math.sin(dir * Math.PI /180);
        if(x<0 || x>Global.virtualW || y<0 || y>Global.virtualH) active=false;
    }

    // 绘制子弹
    void draw(Canvas canvas, long loopTime){
        if(canvas==null)return;
        pos(loopTime);
        canvas.drawCircle(Global.v2Rx(x),Global.v2Ry(y),Global.v2R(RADIUS),paint1);
    }

    // 判断子弹是否命中sprite
    boolean hitSprite(Sprite sprite){
        // todo:优化旋转之后的判断
        return x>=sprite.x+sprite.WIDTH/6 && x<=sprite.x+sprite.WIDTH*5/6 && y>=sprite.y+sprite.HEIGHT/6 && y<=sprite.y+sprite.HEIGHT*5/6;
    }
}
