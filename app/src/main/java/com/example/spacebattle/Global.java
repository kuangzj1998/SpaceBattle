package com.example.spacebattle;

class Global {
    static int KILL = 0;            // 击杀计数
    static int realW;               // 实际屏幕宽度
    static int realH;               // 实际屏幕高度
    static int virtualW = 1080;     // 虚拟屏幕宽度
    static int virtualH = 1920;     // 虚拟屏幕高度
    static long LOOP_TIME = 50;     // 一帧时长（ms）
    // virtualToReal  X方向
    static float v2Rx(float virtualSize){
        return virtualSize * realW /virtualW;
    }
    // virtualToReal  Y方向
    static float v2Ry(float virtualSize){
        return virtualSize * realH /virtualH;
    }
    // realToVirtual  X方向
    static float r2Vx(float realSize){
        return realSize * virtualW / realW;
    }
    // realToVirtual  Y方向
    static float r2Vy(float realSize){
        return realSize * virtualH / realH;
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
        long randomNum = System.currentTimeMillis();
        return (int)(randomNum%(max-min)+min);
    }
}

