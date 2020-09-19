package com.example.spacebattle;

import org.json.JSONObject;

class JsonFunc {
    static String Sprite2JSON(Sprite sprite) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "Sprite");
            obj.put("spName", sprite.spName);
            obj.put("x", sprite.x);
            obj.put("y", sprite.y);
            obj.put("dir", sprite.dir);
            obj.put("step", sprite.step);
            obj.put("active", sprite.active);
            obj.put("ai", sprite.ai);
            obj.put("hit", sprite.hit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj.toString();
    }
    static String Bullet2JSON(Bullet bullet) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "Bullet");
            obj.put("spName", bullet.spName);
            obj.put("x", bullet.x);
            obj.put("y", bullet.y);
            obj.put("dir", bullet.dir);
            obj.put("step", bullet.step);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj.toString();
    }
    static JSONObject parserJSON2Object(String str) {
        JSONObject obj = null;
        if(str.equals("")) return null;
        try {
            obj = new JSONObject(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj;
    }
}
