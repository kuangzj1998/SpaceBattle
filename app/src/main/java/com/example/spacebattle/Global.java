package com.example.spacebattle;

import java.util.Random;

class Global {
    static int statusBarHeight;
    static int contentTop;
    static int toTop;
    static int realW;               // 实际屏幕宽度
    static int realH;               // 实际屏幕高度
    static int virtualW = 1080;     // 虚拟屏幕宽度
    static int virtualH = 1920;     // 虚拟屏幕高度
    static long LOOP_TIME = 50;     // 一帧时长（ms）
    static int KILL = 0;            // 击杀计数
    static Random rnd = new Random();
    // virtualToReal  X方向
    static float v2Rx(float virtualSize){
        return virtualSize * realW /virtualW;
    }
    // virtualToReal  Y方向
    static float v2Ry(float virtualSize){
        return virtualSize * realH /virtualH;
    }
    // 点击事件 realToVirtual  X方向
    static float r2Vx(float realSize){
        return realSize * virtualW / realW;
    }
    // 点击事件 realToVirtual  Y方向
    static float r2Vy(float realSize){
        return (realSize-statusBarHeight-contentTop) * virtualH / realH;
    }
    // virtualToReal  默认
    static float v2R(float virtualSize){
        return virtualSize * realW /virtualW;
    }
    // realToVirtual  默认
    static float r2V(float realSize){
        return realSize * virtualW / realW;
    }
    // 生成[min,max)范围的随机数
    static int ramdom(int min,int max){
        return rnd.nextInt(max-min)+min;
    }
}

