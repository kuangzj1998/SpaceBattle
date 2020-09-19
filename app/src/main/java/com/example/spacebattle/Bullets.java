package com.example.spacebattle;

import android.content.Context;
import android.graphics.Canvas;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

class Bullets {
    Context context;
    private long seqnoX = 0;   // 当前子弹的最大序号
    static ConcurrentHashMap<String,Bullet> lqBullets;
    Bullets(Context context){
        this.context = context;
        lqBullets = new ConcurrentHashMap<String,Bullet>();
    }
    long add(String spName, float x, float y, float dir, float step){
        //Map除重，在外面判断
        seqnoX=(seqnoX+1)%4096;
        Bullet newBullet = new Bullet(context);
        newBullet.spName=spName;
        newBullet.x = x;
        newBullet.y = y;
        newBullet.dir = dir;
        newBullet.step = step;
        newBullet.me = GameObjects.myName.equals(spName);
        lqBullets.put(String.valueOf(seqnoX),newBullet);
        return seqnoX;
    }
    void draw(Canvas canvas, long looptime){
        Iterator<String> it = lqBullets.keySet().iterator();
        while(it.hasNext()){
            Bullet bullet= lqBullets.get(it.next());
            if(bullet.active) {
                bullet.draw(canvas,looptime);
            }
            else remove(bullet.spName);
        }
    }
    private void remove(String spName){
        lqBullets.remove(spName);
    }

// sprite判断是否被其它精灵发出的子弹击中，击中则返回该子弹，否则，返回null
    static void getHitBullet(Sprite sprite){
        if(sprite.hit) return;
        Iterator it = lqBullets.entrySet().iterator();
        Bullet bullet;
        while(it.hasNext()){
            bullet=(Bullet) ((ConcurrentHashMap.Entry)it.next()).getValue();
            if(!bullet.spName.equals(sprite.spName) && bullet.active) {
                if (bullet.hitSprite(sprite)) {
                    bullet.active = false;
                    sprite.hit = true;
                    sprite.step*=4;
                    if(!sprite.spName.equals(GameObjects.myName)) Global.KILL = (Global.KILL+1)%100;
                }
            }
        }
    }

}
