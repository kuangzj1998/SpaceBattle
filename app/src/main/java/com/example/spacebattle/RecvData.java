package com.example.spacebattle;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

class RecvData implements Runnable {
    private Socket socket;
    private GameObjects gameObjects;
    boolean isRunning = true;
    private DataInputStream fromServer;

    RecvData(Socket sock, GameObjects gameObjects) {
        this.gameObjects = gameObjects;
        socket = sock;
        try {
            fromServer = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void run() {
        while (isRunning) {
            try {
                String data = fromServer.readUTF();
                if(gameObjects!=null) {
                    gameObjects.handleRecvData(data);
                }
            } catch (IOException ex) {
                isRunning = false;
            }
        }
    }
}