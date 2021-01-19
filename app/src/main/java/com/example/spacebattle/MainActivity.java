package com.example.spacebattle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    static String serverIpAddr="120.78.181.125";
    static int port = 5000;
    static TcpSocket tcpSocket;
    private RecvData recvData;
    GameSurfaceView gameSurfaceView;
    MediaPlayer mp=null;
    static boolean networkMode = false;
    String user_name = "";
    private MediaPlayer.OnCompletionListener CompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if(mp!=null){
                mp.release();
                mp=null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        gameSurfaceView = new GameSurfaceView(this);
        setContentView(gameSurfaceView);

        tcpSocket = new TcpSocket();

        //背景音乐
        Intent intent = new Intent();
        intent.putExtra("Msg","Wish");
        intent.setAction("com.example.user.MyReceiver");
        sendBroadcast(intent);   //发送广播
        mp = new MediaPlayer();

        final View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.input_user,null);
        view.post(new Runnable() {
            @Override
            public void run() {

            }
        });
        final EditText user_name_in = (EditText)view.findViewById(R.id.input_name);
        final EditText server_in = (EditText)view.findViewById(R.id.input_server);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog alertDialog = dialogBuilder
                .setIcon(R.drawable.sysu)
                .setTitle("输入用户")
                .setView(view)
                .setPositiveButton("连网", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user_name = user_name_in.getText().toString().trim();
                        serverIpAddr = server_in.getText().toString().trim();
                        networkMode = true;
                        String report = "用户名："+user_name+"\n服务器："+serverIpAddr+"\n连网：是";
                        Toast.makeText(MainActivity.this,report,Toast.LENGTH_SHORT).show();
                        if (tcpSocket.testConnection(serverIpAddr, port)) {
                            Toast.makeText(MainActivity.this,"连网成功！",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this,"连网失败！",Toast.LENGTH_LONG).show();
                        }

                        if (tcpSocket.isConnected() || tcpSocket.connect(serverIpAddr, port)) {
                            if (recvData != null)
                                recvData.isRunning = false;
                            recvData = new RecvData(tcpSocket.socket, gameSurfaceView.gameObjects);
                            Thread thread = new Thread(recvData);
                            thread.start();
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user_name = String.valueOf(Global.ramdom(1000,9999));
                        networkMode = false;
                        //alertDialog.dismiss();
                    }
                })
                .setNeutralButton("单机版", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user_name = user_name_in.getText().toString().trim();
                        serverIpAddr = server_in.getText().toString().trim();
                        networkMode = true;
                        String report = "用户名："+user_name+"\n服务器："+serverIpAddr+"\n连网：否";
                        Toast.makeText(MainActivity.this,report,Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        alertDialog.show();
        //alertDialog.getWindow().setLayout(1000,800);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //todo check click
                Global.contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

                System.out.println("bar: "+Global.statusBarHeight+"contenttop: "+Global.contentTop+"totop:"+(Global.realH-Global.contentTop));
                //Toast.makeText(this,String.valueOf(event.getX())+" "+String.valueOf(event.getY()-240),Toast.LENGTH_SHORT).show();
                float dx = Global.r2Vx(event.getX());
                float dy = Global.r2Vy(event.getY());
                Toast.makeText(this,String.valueOf(dx)+" "+String.valueOf(dy),Toast.LENGTH_SHORT).show();

                String result = gameSurfaceView.gameObjects.getPressedButton(dx,dy);
                if(!result.equals("None")) {
                    System.out.println("按了["+result+"]按钮");
                    if(result.equals("开始")){
                        //if(GameObjects.sprites!=null) System.out.println(GameObjects.sprites.hmSprites.size());
                        if(GameObjects.myName.equals("")){
                            if(user_name.equals("")) user_name = String.valueOf(Global.ramdom(1000,9999));
                            GameObjects.sprites = new Sprites(MainActivity.this,user_name);
                        }
                        else if(GameObjects.mySprite==null){
                            String name = String.valueOf(Global.ramdom(1000,9999));
                            GameObjects.sprites.add(name,0,0,0,10,true,false,true);
                            GameObjects.sprites.myName = name;
                            GameObjects.myName = name;
                            GameObjects.mySprite = GameObjects.sprites.hmSprites.get(name);
                            Global.KILL = 0;
                        }
                        else if(!GameObjects.sprites.hmSprites.containsKey("other")){
                            GameObjects.sprites.add("other",Global.ramdom(0,Global.virtualW-200),Global.ramdom(0,Global.virtualH-200),Global.ramdom(0,360),8,true,true,true);
                            //GameObjects.sprites.add("other",100,100,Global.ramdom(0,360),15,false,true,true);
                        }
                    }
                    if(result.equals("开火")){
                        if(GameObjects.mySprite==null) break;
                        mp = MediaPlayer.create(MainActivity.this,R.raw.bullet);
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mp.setLooping(false);
                        mp.start();
                        mp.setVolume(14.0f, 14.0f); //声音调不了
                        mp.setOnCompletionListener(CompletionListener);

                        long num = GameObjects.mySprite.shot();
                        if(networkMode) {
                            String data = JsonFunc.Bullet2JSON(Bullets.lqBullets.get(String.valueOf(num)));
                            tcpSocket.sendString(data);
                        }
                    }
                    if(result.equals("自动")){
                        if(GameObjects.mySprite==null) break;
                        GameObjects.mySprite.ai = !GameObjects.mySprite.ai;
                    }
                    if(result.equals("关闭")){
                        Intent intent = new Intent();
                        intent.putExtra("Msg","Stop");
                        intent.setAction("com.example.user.MyReceiver");
                        sendBroadcast(intent);   //发送广播
                        this.finish();
                    }
                }
                else {
                    if(GameObjects.mySprite==null) break;
                    GameObjects.mySprite.getDirection(GameObjects.mySprite.x,GameObjects.mySprite.y,dx,dy);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(networkMode) tcpSocket.close();
    }
}
