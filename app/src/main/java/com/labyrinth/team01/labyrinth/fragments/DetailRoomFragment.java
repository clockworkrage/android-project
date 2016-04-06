package com.labyrinth.team01.labyrinth.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.labyrinth.team01.labyrinth.R;

/**
 * Created by SwordMaster on 25.03.2016.
 */
public class DetailRoomFragment extends Fragment {
    public static String ROOM_SIZE = "room_size";

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
        TextView sizeRoomTextview = (TextView) view.findViewById(R.id.room_size);
        Button joinButton = (Button) view.findViewById(R.id.join_game);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Start game*/
            }
        });

        sizeRoomTextview.setText(Integer.toHexString(getArguments().getInt(ROOM_SIZE)));
    }

}
