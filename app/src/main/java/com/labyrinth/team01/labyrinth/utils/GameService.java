package com.labyrinth.team01.labyrinth.utils;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.labyrinth.team01.labyrinth.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SwordMaster on 10.05.2016.
 */
public class GameService extends Service {
    private Integer roomId = 0;
    private static String number = "1";
    private String password = "";
    private static WebSocket gameWebSocket;
    public void onCreate() {
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        roomId = Integer.parseInt(intent.getStringExtra("roomId"));
        password = intent.getStringExtra("password");
        final ResultReceiver receiver = intent.getParcelableExtra("RECEIVER");
        final Bundle data = new Bundle();

        AsyncHttpClient.getDefaultInstance().websocket("http://"+ getString(R.string.server_ip)+":8080/gameplay/", null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                gameWebSocket = webSocket;
                webSocket.send("{\"number\":"+number+",\"command\":\"join_game\", \"roomId\":"+ roomId.toString()+ ", \"password\":\""+password+"\"}");
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        System.out.println("I got a string: " + s);
                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(s);
                            String type = jObject.getString("type");
                            if (type.equals("player_status")) {
                                String status = jObject.getString("status");
                                switch (status) {
                                    case "wait":
                                        data.putString("status", "wait");
                                        receiver.send(1, data);
                                        break;
                                    case "in_game":
                                        data.putString("status", "in_game");
                                        data.putString("area",jObject.getString("area"));
                                        receiver.send(1, data);
                                        break;
                                    case "finished":
                                        data.putString("status", "finished");
                                        receiver.send(1, data);
                                        break;
                                }

                            }
                            if (type.equals("percent")) {
                                Integer percent = jObject.getInt("percent");
                                data.putInt("percent", percent);
                                receiver.send(2, data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

                webSocket.setDataCallback(new DataCallback() {
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                        byteBufferList.recycle();
                    }
                });

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();

    }

    public IBinder onBind(Intent intent) {

        return null;
    }

    public static void goTop(){
        gameWebSocket.send("{\"number\":"+number+",\"command\":\"move\", \"move\":\"top\"}");
    }

    public static void goBottom(){
        gameWebSocket.send("{\"number\":"+number+",\"command\":\"move\", \"move\":\"bottom\"}");
    }

    public static void goLeft() {
        gameWebSocket.send("{\"number\":"+number+",\"command\":\"move\", \"move\":\"left\"}");
    }

    public static void goRight(){
        gameWebSocket.send("{\"number\":"+number+",\"command\":\"move\", \"move\":\"right\"}");
    }

    public static void exitRoom(){
        gameWebSocket.send("{\"number\":"+number+",\"command\":\"leave_game\"}");
        gameWebSocket.close();
    }

}