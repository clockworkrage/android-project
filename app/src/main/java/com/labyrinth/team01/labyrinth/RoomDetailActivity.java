package com.labyrinth.team01.labyrinth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RoomDetailActivity extends AppCompatActivity {
    Integer roomId = 0;
    Integer max_players = 0;
    Integer min_players = 0;
    Integer players = 0;
    Integer status = 0;
    Integer time_step = 0;
    Boolean is_password = false;

    private TextView minPlayersTextView;
    private TextView maxPlayersTextView;
    private TextView playersTextView;
    private TextView timeStampTextView;
    private TextView passwordTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_detail_layout);

        min_players = getIntent().getIntExtra("min_players", 0);
        max_players = getIntent().getIntExtra("max_players", 0);
        players = getIntent().getIntExtra("players", 0);
        status = getIntent().getIntExtra("status", 0);
        time_step = getIntent().getIntExtra("time_step", 0);
        is_password = getIntent().getBooleanExtra("is_password", false);
        roomId = getIntent().getIntExtra("roomId", 0);

        minPlayersTextView = (TextView) findViewById(R.id.min_players);
        maxPlayersTextView = (TextView) findViewById(R.id.max_players);
        playersTextView = (TextView) findViewById(R.id.number_players);
        timeStampTextView = (TextView) findViewById(R.id.step_time);
        passwordTextView = (TextView) findViewById(R.id.game_password);

        minPlayersTextView.setText(min_players.toString());
        maxPlayersTextView.setText(max_players.toString());
        playersTextView.setText(players.toString());
        timeStampTextView.setText(time_step.toString());

        if (is_password == true) {
            passwordTextView.setText(is_password.toString());
        }else{
            passwordTextView.setText(is_password.toString());
        }

        Button joinButton = (Button) findViewById(R.id.join_game);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MultiplayerGameActivity.class);
                intent.putExtra("roomId", roomId.toString());
                intent.putExtra("password", "");
                startActivity(intent);
            }
        });

        if (roomId == 0) {
            TextView roomSizeView = (TextView) findViewById(R.id.room_size);
            TextView roomSizeLabelView = (TextView) findViewById(R.id.label_room_size);
            TextView labelStepTimeView = (TextView) findViewById(R.id.label_step_time);
            TextView labelNumberPlayersView = (TextView) findViewById(R.id.label_number_players);
            TextView labelMaxPlayersView = (TextView) findViewById(R.id.label_max_players);
            TextView labelMinPlayersView = (TextView) findViewById(R.id.label_min_players);
            TextView labelGamePasswordView = (TextView) findViewById(R.id.label_game_password);
            joinButton.setVisibility(View.GONE);
            minPlayersTextView.setVisibility(View.GONE);
            maxPlayersTextView.setVisibility(View.GONE);
            playersTextView.setVisibility(View.GONE);
            timeStampTextView.setVisibility(View.GONE);
            passwordTextView.setVisibility(View.GONE);

            roomSizeLabelView.setVisibility(View.GONE);
            labelStepTimeView.setVisibility(View.GONE);
            labelNumberPlayersView.setVisibility(View.GONE);
            labelMaxPlayersView.setVisibility(View.GONE);
            labelMinPlayersView.setVisibility(View.GONE);
            labelGamePasswordView.setVisibility(View.GONE);
            roomSizeView.setVisibility(View.GONE);
        }


    }
}
