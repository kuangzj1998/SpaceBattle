package com.example.spacebattle;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    final float PERIOD = 50;
    Context context;
    RecvData recvData;
    private SurfaceHolder holder;
    private boolean isRun=true;
    public GameObjects gameObjects;

    public GameSurfaceView(Context context) {
        super(context);
        this.context = context;
        holder = this.getHolder();
        holder.addCallback(this);         // 给SurfaceView当前的持有者一个回调对象
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        holder.setFormat(PixelFormat.TRANSPARENT);
    }

    @Override    // 在surface的大小发生改变时激发
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override  // 在创建时激发，一般在这里调用画图线程
    public void surfaceCreated(SurfaceHolder holder) {
        isRun = true;
        Global.realW = getWidth();    // 获取屏幕宽度
        Global.realH = getHeight();   // 获取屏幕高度
        //System.out.println("Real Width: "+realW+"Real Height: "+realH);
        gameObjects = new GameObjects(this.context,holder);
        Thread myThread = new Thread(this); // 创建一个绘图线程
        myThread.start();
    }

    @Override  // 销毁时激发，一般在这里将画图的线程停止、释放
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRun = false;
    }

    @Override
    public void run() {
        long start = 0;     // 开始时间
        long loopTime = 0;  // 每次循环的实际执行时间
        while(isRun) {
            start = System.currentTimeMillis();
            gameObjects.draw(loopTime);
            if (GameObjects.mySprite != null) {
                if(MainActivity.tcpSocket.isConnected()) {
                    String data = JsonFunc.Sprite2JSON(GameObjects.mySprite);
                    MainActivity.tcpSocket.sendString(data);
                }
            }
            sleep((float)System.currentTimeMillis()-start);  // 睡眠一段时间
            loopTime =  System.currentTimeMillis()-start;    // 本次循环的实际执行时间
        }
    }

    // 睡眠一段时间，使每次循环的时间为PERIOD
    void sleep(float runTime){
        try {
            float leftTime = PERIOD - runTime;  // 剩余时间
            Thread.sleep(leftTime>0 ? (int)leftTime : 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



