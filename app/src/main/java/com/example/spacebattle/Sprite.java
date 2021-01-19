package com.example.spacebattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

class Sprite {
    final float HEIGHT = 120;   // 精灵高度（虚拟尺寸）。final变量(Java常量)只能赋一次值
    final float WIDTH = 120;    // 精灵宽度（虚拟尺寸）
    final int FONT_SIZE = 24;   // 字体大小（虚拟尺寸）
    final float FRAME_DURATION = 1 * Global.LOOP_TIME;  // 每帧持续时间（ms）
    float step = 10;            // 每个LOOP_TIME的移动步幅（用于控制速度）取值1~ Any(虚拟单位)
    String spName;              // 精灵名称
    float x;                    // 精灵x轴定位
    float y;                    // 精灵y轴定位
    float dir;                  // 移动方向，单位：degree
    boolean me = true;          // 区分自己的精灵还是其它用户的精灵
    boolean active = true;      // 是否活动（可见）。重用（reuse）方式可用
    boolean ai = false;         // 是否托管
    boolean hit = false;        // 是否被击中
    private int curFrameIndex;         // 当前帧号，取值0~7
    private float frameDuration = 0;   // 当前帧已显示时间
    final float AI_MOVE_GAP = 80* Global.LOOP_TIME;      // AI：自动转向的时间间隔(ms)
    float aiMoveDelay = 0;             // 自动转向的累计时间（到AI_MOVE_GAP时移动方向）
    final float AI_SHOT_GAP = 30 * Global.LOOP_TIME;     // AI：自动发射的时间间隔(ms)
    float aiShotDelay = 0;             // 自动射击的累计时间（到AI_SHOT_GAP时射击）
    final float DEAD_TIME = 240 * Global.LOOP_TIME;      // 杀死掉线用户的时间间隔(ms)
    float deadTime = 0;                // 杀死掉线用户的累计时间（到DEAD_TIME时射击）

    private int[] frames = { R.drawable.sprite1,R.drawable.sprite2,R.drawable.sprite3,
            R.drawable.sprite4,R.drawable.sprite5,R.drawable.sprite6,
            R.drawable.sprite7,R.drawable.sprite8
    };
    private Paint paint1;       // 精灵图像画笔
    private Paint paint2;       // 精灵文字画笔
    private Context context;

    Sprite(Context context){
        this.context = context;
        paint1 = new Paint();
        paint2 = new Paint();
        paint2.setColor(Color.argb(60,50,50,50));
        paint2.setTextSize(Global.v2Rx(FONT_SIZE));
        curFrameIndex = 0;
    }

    // 绘制精灵：先计算下一帧的索引，计算精灵的位置，再绘制精灵
    void draw(Canvas canvas, long loopTime){
        if(canvas == null) return;
        if(spName.equals("other")) setSpritePaints();
        if(hit) drop(loopTime);
        else {
            nextFrame(loopTime);
            pos(loopTime);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),frames[curFrameIndex]);
        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //计算压缩的比率
        float scaleWidth=Global.v2Rx(WIDTH)/width;
        float scaleHeight=Global.v2Ry(HEIGHT)/height;
        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        if(dir>90&&dir<270){
            matrix.postScale(-scaleWidth,scaleHeight);
            matrix.preRotate(180-dir);
        }
        else{
            matrix.postScale(scaleWidth,scaleHeight);
            matrix.preRotate(dir);
        }
        //获取新的bitmap
        Bitmap newbitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        canvas.drawBitmap(newbitmap,Global.v2Rx(x),Global.v2Ry(y),paint1);
        Point text_point = getTextPosition();
        canvas.drawText(spName,text_point.x,text_point.y,paint2);//文本的坐标是左下角
        /*Paint p = new Paint();
        p.setColor(Color.argb(128,100,160,100));
        canvas.drawCircle(Global.v2Rx(x),Global.v2Ry(y),Global.v2R(20),p);//圆的坐标是中心*/
    }

    // 通过累计frameDuration计算下一个帧的索引
    private void nextFrame(long loopTime){
        float time = frameDuration + loopTime;
        curFrameIndex = curFrameIndex + (int)(time/FRAME_DURATION);
        curFrameIndex %= frames.length;
        frameDuration = time%FRAME_DURATION;
    }

    // 计算下一帧sprite所在的位置
    private void pos(long loopTime){
        if(!me) return;
        float step1 = this.step * loopTime/Global.LOOP_TIME;
        x = x + step1 * (float)Math.cos(dir * Math.PI /180);
        y = y + step1 * (float)Math.sin(dir * Math.PI /180);
        if(x<0) x=0;
        if(x>Global.virtualW-WIDTH) x=Global.virtualW-WIDTH;
        if(y<0) y=0;
        if(y>Global.virtualH-HEIGHT) y=Global.virtualH-HEIGHT;
    }

    //  垂直下落(击中之后)
    private void drop(long loopTime){
        ai = false;
        dir = 90;
        float step1 = this.step * loopTime/Global.LOOP_TIME;
        y = y + step1 ;
        if(y>Global.virtualH) active = false;
    }

    // 给出两个点(left,top)和（newLeft,newTop），计算出方向（degree）
    void getDirection(float left,float top, float newLeft, float newTop){
        float dx = newLeft - left;
        float dy = newTop - top;
        double dist = Math.sqrt(dx*dx+dy*dy);
        float theta=0;
        if((int)dist!=0) theta = (float) Math.asin(Math.abs(dy) / dist) * 180.0f / (float)Math.PI;
        else theta = 0;
        if(dx<=0 && dy>=0) theta= 180-theta;
        if(dx<=0 && dy<=0) theta= 180+theta;
        if(dx>0 && dy<=0)  theta= 360-theta;
        dir = theta;
        //System.out.println(String.valueOf(dir));
    }

    //  根据情况设置名字的位置
    private Point getTextPosition(){
        Point p = new Point();
        if(dir>270){
            p.x = (int)Global.v2Rx(x);
            p.y = (int)Global.v2Ry(y+HEIGHT/2);
            return p;
        }
        if(dir>180){
            p.x = (int)Global.v2Rx(x+WIDTH/2);
            p.y = (int)Global.v2Ry(y);
            return p;
        }
        if(dir>90){
            p.x = (int)Global.v2Rx(x);
            p.y = (int)Global.v2Ry(y+HEIGHT/2);
            return p;
        }
            p.x = (int)Global.v2Rx(x+WIDTH/2);
            p.y = (int)Global.v2Ry(y);
            return p;
    }

    // 射击（产生新子弹）,速度是精灵移动速度的3倍
    long shot(){
        Point point = getShotStPos();
        return GameObjects.bullets.add(spName,point.x,point.y,dir,step*3);
    }

    // todo:优化旋转后出发点的考虑，封装一个不太依赖图片的
    private Point getShotStPos(){
        float or_x = x + WIDTH/2;
        float or_y = y + HEIGHT/2;
        return new Point((int)or_x,(int)or_y);
    }

    // AI计算变向累计时间和射击累计时间
    void aiCalc(long loopTime){
        aiMoveDelay+=loopTime;
        aiShotDelay+=loopTime;
    }

    // 设置精灵图像画笔
    private void setSpritePaints(){
        LightingColorFilter light = new LightingColorFilter(0xFF00FF00, 0x000000FF);
        paint1.setColorFilter(light);
    }

    // 计算累积更新时间
    void deadCalc(long loopTime){
        deadTime += loopTime;
        if(deadTime > DEAD_TIME){
            active = false;
        }
    }
}

