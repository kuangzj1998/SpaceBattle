package com.example.spacebattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import org.json.JSONObject;

class GameObjects {
    private Context context;
    private SurfaceHolder holder;
    private Buttons buttons;
    static Sprites sprites = null;
    static Sprite mySprite = null;  // 本玩家精灵
    static String myName = "";      // 本玩家精灵的名字
    static Bullets bullets = null;
    private Paint BG,TEXT;
    private Bitmap bitmap;

    GameObjects(Context context,SurfaceHolder holder){
        this.context = context;
        this.holder = holder;
        buttons = new Buttons(context);
        buttons.pos();
        bullets = new Bullets(context);
        BG = new Paint();
        TEXT = new Paint();
        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.cloud);
        TEXT.setColor(Color.RED);
        TEXT.setTextSize(Global.v2R(60));
    }
    void draw(long loopTime){
        Canvas canvas=null;
        try{
            canvas = holder.lockCanvas();
            drawBackground(canvas);                                 //  绘制背景
            buttons.draw(canvas);                                  //  绘制按钮
            if(sprites != null) sprites.draw(canvas,loopTime);     //  绘制精灵
            bullets.draw(canvas,loopTime);                         //  绘制子弹
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(canvas!= null) {
                holder.unlockCanvasAndPost(canvas); //结束锁定画图，并提交改变。
            }
        }
    }
    //  绘制背景(天空和击杀数)
    private void drawBackground(Canvas canvas){
        if(canvas == null) return;
        canvas.drawBitmap(bitmap,0,0,BG);
        canvas.drawText("击杀数",Global.v2Rx(900),Global.v2Ry(80),TEXT);
        if(Global.KILL>=10) canvas.drawText(String.valueOf(Global.KILL),Global.v2Rx(980),Global.v2Ry(200),TEXT);
        else canvas.drawText(String.valueOf(Global.KILL),Global.v2Rx(1040),Global.v2Ry(200),TEXT);
    }
    //  返回底层计算的点击判断结果
    String getPressedButton(float x,float y){
        return buttons.getPressedButton(x,y);
    }

    void handleRecvData(String data) {
        JSONObject obj = JsonFunc.parserJSON2Object(data);
        if(obj==null) return;
        String type = obj.optString("type");
        String name = obj.optString("spName");
        if (type.equals("Bullet")){
            if(!name.equals(myName))
                bullets.add( name, (float)obj.optDouble("x"),(float)obj.optDouble("y"),(float)obj.optDouble("dir"),(float)obj.optDouble("step"));
        }
        else if(type.equals("Sprite")){
            if(mySprite!=null && !name.equals(myName)) {
                if (sprites.hmSprites.containsKey(name)) {
                    Sprite sp = sprites.hmSprites.get(name);
                    sp.x = (float) obj.optDouble("x");
                    sp.y = (float) obj.optDouble("y");
                    sp.dir = (float) obj.optDouble("dir");
                    sp.step = (float) obj.optDouble("step");
                    sp.active = obj.optBoolean("active");
                    sp.ai = obj.optBoolean("ai");
                    sp.hit = obj.optBoolean("hit");
                    sp.deadTime = 0f;
                }
                else
                    sprites.add(name, (float) obj.optDouble("x"), (float) obj.optDouble("y"), (float) obj.optDouble("dir"), (float) obj.optDouble("step"), obj.optBoolean("active"), obj.optBoolean("ai"),false);
            }
        }
        else {
            System.out.println("收到数据的类型错误！");
        }
    }
}
