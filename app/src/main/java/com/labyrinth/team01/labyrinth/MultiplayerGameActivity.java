package com.labyrinth.team01.labyrinth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.labyrinth.team01.labyrinth.utils.GameResultsReceiver;
import com.labyrinth.team01.labyrinth.utils.GameService;

public class MultiplayerGameActivity extends Activity implements GameResultsReceiver.Receiver {
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

    private static final String KEY_FIELD = "FIELD";
    private static final String KEY_ISSTARTED = "ISSTARTED";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_FIELD, gameArea);
        outState.putParcelable("RECEIVER", mReceiver);
        outState.putBoolean(KEY_ISSTARTED, isStarted);

        super.onSaveInstanceState(outState);
    }

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


        if(savedInstanceState == null) {
            mReceiver = new GameResultsReceiver(new Handler());
            mReceiver.setReceiver(this);
            progressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, GameService.class);
            intent.putExtra("roomId", roomId.toString());
            intent.putExtra("password", "");
            intent.putExtra("RECEIVER", mReceiver);
            startService(intent);
        }
        else {

            gameArea = savedInstanceState.getString(KEY_FIELD);
            updateLabirinth();
            isStarted = savedInstanceState.getBoolean(KEY_ISSTARTED);
            mReceiver = savedInstanceState.getParcelable("RECEIVER");
            if (isStarted){
                progressBar.setVisibility(View.GONE);
            } else
                progressBar.setVisibility(View.VISIBLE);
            if(mReceiver != null)
                mReceiver.setReceiver(this);
        }

    }

    private void updateLabirinth(){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 7; ++i) {
                for (int j = 0; j < 7; ++j) {
                    if (i == 3 && j == 3) {
                        stringBuilder.append("OO");
                    } else {
                        stringBuilder.append(gameArea.charAt(i * 30 + j * 4 + 2 + 1));
                        stringBuilder.append(gameArea.charAt(i * 30 + j * 4 + 2 + 1));
                    }
                }
                stringBuilder.append('\n');
            }
            text.setText(stringBuilder.toString());
            clearButton();
        }catch (Exception e){}
    }

    public void clearButton(){
        Button buttonL = (Button) findViewById(R.id.button3);
        buttonL.setBackgroundResource(R.drawable.button_arrow_left_image);
        Button buttonR = (Button) findViewById(R.id.button4);
        buttonR.setBackgroundResource(R.drawable.button_arrow_right_image);
        Button buttonT = (Button) findViewById(R.id.button);
        buttonT.setBackgroundResource(R.drawable.button_arrow_up_image);
        Button buttonB = (Button) findViewById(R.id.button2);
        buttonB.setBackgroundResource(R.drawable.button_arrow_down_image);
    }

    public void onClickTop(View view){
        if (isStarted) {
            clearButton();
            Button buttonT = (Button) findViewById(R.id.button);
            buttonT.setBackgroundResource(R.drawable.arrow_up_pressed);
            GameService.goTop();
        }
    }

    public void onClickBottom(View view){
        if (isStarted) {
            clearButton();
            Button buttonB = (Button) findViewById(R.id.button2);
            buttonB.setBackgroundResource(R.drawable.arrow_down_pressed);
            GameService.goBottom();
        }
    }

    public void onClickLeft(View view){
        if (isStarted) {
            clearButton();
            Button buttonL = (Button) findViewById(R.id.button3);
            buttonL.setBackgroundResource(R.drawable.arrow_left_pressed);
            GameService.goLeft();
        }
    }

    public void onClickRight(View view){
        if (isStarted) {
            clearButton();
            Button buttonR = (Button) findViewById(R.id.button4);
            buttonR.setBackgroundResource(R.drawable.arrow_right_pressed);
            GameService.goRight();
        }
    }

    @Override
    public void onStop () {
        //if(isStarted)
        //    GameService.exitRoom();
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
