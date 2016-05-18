package com.labyrinth.team01.labyrinth;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.labyrinth.team01.labyrinth.game.TypeLabirinthsCells;
import com.labyrinth.team01.labyrinth.game.Vec2d;
import com.labyrinth.team01.labyrinth.utils.GameResultsReceiver;
import com.labyrinth.team01.labyrinth.utils.GameService;

import org.json.JSONException;
import org.json.JSONObject;

import static com.labyrinth.team01.labyrinth.utils.GameService.*;

public class MultiplayerGameActivity extends AppCompatActivity implements GameResultsReceiver.Receiver {
    private Integer roomId = 0;
    private String password = "";
    private ProgressBar progressBar;
    private ProgressBar progressBarStep;
    private GameResultsReceiver mReceiver;
    private TextView text;
    private String gameArea;
    private Boolean isStarted = false;
    private Boolean isWin = false;

    private StringBuilder playerPath = new StringBuilder();
    private char lastDirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game);

        progressBar = (ProgressBar) findViewById(R.id.progressWaitGame);
        progressBarStep = (ProgressBar) findViewById(R.id.progressStep);

        text = (TextView) findViewById(R.id.textView);
        text.setTypeface(Typeface.MONOSPACE);

        roomId = Integer.parseInt(getIntent().getStringExtra("roomId"));
        password = getIntent().getStringExtra("password");

        progressBar.setVisibility(View.VISIBLE);

        mReceiver = new GameResultsReceiver(new Handler());
        mReceiver.setReceiver(this);

        Intent intent = new Intent(this, GameService.class);
        intent.putExtra("roomId", roomId.toString());
        intent.putExtra("password", "");
        intent.putExtra("RECEIVER", mReceiver);
        startService(intent);

    }

    private void updateLabirinth(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<7; ++i){
            for(int j=0; j<7; ++j){
                if(i==3 && j==3){
                    stringBuilder.append("OO");
                } else {
                    stringBuilder.append(gameArea.charAt(i*30 + j*4 + 2 + 1));
                    stringBuilder.append(gameArea.charAt(i*30 + j*4 + 2 + 1));
                }
            }
            stringBuilder.append('\n');
        }
        text.setText(stringBuilder.toString());
    }

    public void onClickTop(View view){
        if (isStarted == true)
            GameService.goTop();
    }

    public void onClickBottom(View view){
        if (isStarted == true)
            GameService.goBottom();
    }

    public void onClickLeft(View view){
        if (isStarted == true)
            GameService.goLeft();
    }

    public void onClickRight(View view){
        if (isStarted == true)
            GameService.goRight();
    }

    @Override
    public void onStop () {
        if (isStarted == true)
            GameService.exitRoom();
        super.onStop();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case 1 :
                switch (data.getString("status"))
                {
                    case "wait":
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                    case "in_game":
                            isStarted = true;
                            gameArea = data.getString("area");
                            progressBar.setVisibility(View.GONE);
                            updateLabirinth();
                            break;
                    case "finished":
                            ifWin();
                            break;
                }
                break;
            case 2:
                Integer progress = data.getInt("percent");
                if (isStarted == true)
                    progressBarStep.setProgress(progress);
                break;
        }
    }

    private void ifWin(){
        isWin = true;
        isStarted = false;
        text.setText("You win!");
    }
}
