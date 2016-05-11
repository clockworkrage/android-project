package com.labyrinth.team01.labyrinth.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.labyrinth.team01.labyrinth.CreateRoomActivity;
import com.labyrinth.team01.labyrinth.MainActivity;
import com.labyrinth.team01.labyrinth.MiltiplayerGameActivity;
import com.labyrinth.team01.labyrinth.R;

/**
 * Created by SwordMaster on 25.03.2016.
 */
public class DetailRoomFragment extends Fragment {
    public static String ROOM_SIZE = "room_size";
    private Integer roomId = 0;
    private Integer max_players = 0;
    private Integer min_players = 0;
    private Integer players = 0;
    private Integer status = 0;
    private Integer time_step = 0;
    private Boolean is_password = false;

    private TextView minPlayersTextView;
    private TextView maxPlayersTextView;
    private TextView playersTextView;
    private TextView timeStampTextView;
    private TextView passwordTextView;


    public static DetailRoomFragment getInstance(int news_no) {
        DetailRoomFragment detailFragment = new DetailRoomFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ROOM_SIZE, news_no);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.room_detail, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        minPlayersTextView = (TextView) view.findViewById(R.id.min_players);
        maxPlayersTextView = (TextView) view.findViewById(R.id.max_players);
        playersTextView = (TextView) view.findViewById(R.id.number_players);
        timeStampTextView = (TextView) view.findViewById(R.id.step_time);
        passwordTextView = (TextView) view.findViewById(R.id.game_password);

        minPlayersTextView.setText(min_players.toString());
        maxPlayersTextView.setText(max_players.toString());
        playersTextView.setText(players.toString());
        timeStampTextView.setText(time_step.toString());

        if (is_password == true) {
            passwordTextView.setText(is_password.toString());
        }else{
            passwordTextView.setText(is_password.toString());
        }



        Button joinButton = (Button) view.findViewById(R.id.join_game);
        Button createButton = (Button) view.findViewById(R.id.create_game);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MiltiplayerGameActivity.class);
                intent.putExtra("roomId", roomId.toString());
                intent.putExtra("password", "");
                startActivity(intent);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateRoomActivity.class);
                startActivity(intent);
            }
        });

    }


    public void setMax_players(Integer max_players) {
        this.max_players = max_players;
    }

    public void setMin_players(Integer min_players) {
        this.min_players = min_players;
    }

    public void setPlayers(Integer players) {
        this.players = players;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setTime_step(Integer time_step) {
        this.time_step = time_step;
    }

    public void setIs_password(Boolean is_password) {
        this.is_password = is_password;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}
