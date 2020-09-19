package com.example.spacebattle;

import android.content.Context;
import android.graphics.Canvas;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

class Sprites {
    private Context context;
    ConcurrentHashMap<String,Sprite> hmSprites;    // 所有精灵(与HashMap用法相同，用于多线程环境)
    String myName;                               // 本精灵的名称
    private Sprite mySprite;                            // 本精灵（用于区分其他精灵）
    Sprites(Context context,String myName){
        this.context = context;
        hmSprites = new ConcurrentHashMap<String,Sprite>();
        this.myName = myName;
        mySprite = new Sprite(context);
        mySprite.spName = myName;
        mySprite.x=0;
        mySprite.y=0;
        mySprite.dir=0;
        //mySprite.frameDuration=System.currentTimeMillis();
        GameObjects.mySprite = mySprite;
        GameObjects.myName = myName;
        hmSprites.put(myName,mySprite);
    }
    void add(String spName, float x, float y, float dir, float step,boolean active,boolean ai,boolean me){
        //Map除重，在外面判断
        Sprite newSprite = new Sprite(context);
        newSprite.spName=spName;
        newSprite.x = x;
        newSprite.y = y;
        newSprite.dir = dir;
        newSprite.step = step;
        newSprite.active = active;
        newSprite.ai = ai;
        newSprite.me = me;
        hmSprites.put(spName,newSprite);
    }
    void draw(Canvas canvas, long looptime){
        Iterator<String> it = hmSprites.keySet().iterator();
        while(it.hasNext()){
            Sprite sprite= hmSprites.get(it.next());
            Bullets.getHitBullet(sprite);
            if(!sprite.active){
                remove(sprite.spName);
                continue;
            }
            sprite.draw(canvas,looptime);
            if(sprite.ai){
                sprite.aiCalc(looptime);
                if(sprite.aiMoveDelay >= sprite.AI_MOVE_GAP){
                    sprite.aiMoveDelay -= sprite.AI_MOVE_GAP;
                    Sprite other = findOtherSprite(sprite.spName);
                    if(other!=null) sprite.getDirection(sprite.x,sprite.y,other.x,other.y);
                }
                else if(sprite.aiShotDelay >= sprite.AI_SHOT_GAP){
                    sprite.aiShotDelay -= sprite.AI_SHOT_GAP;
                    sprite.shot();
                }
            }
            if(!sprite.me){
                sprite.deadCalc(looptime);
            }
        }
    }
    private void remove(String spName){
        hmSprites.remove(spName);
        if(myName.equals(spName)){
            //myName = "";
            mySprite = null;
            GameObjects.mySprite = null;
        }
    }
    private Sprite findOtherSprite(String spName){
        int pos;
        String[] keys = hmSprites.keySet().toArray(new String[0]);
        if(keys.length==1) return null;
        while (true){
            pos = Global.ramdom(0, keys.length);
            //System.out.println(pos);
            if (!keys[pos].equals(spName)) break;
        }
        return hmSprites.get(keys[pos]);
    }
}
