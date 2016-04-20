package com.labyrinth.team01.labyrinth;

import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.labyrinth.team01.labyrinth.fragments.DetailRoomFragment;
import com.labyrinth.team01.labyrinth.fragments.ListRoomsFragment;

import java.util.concurrent.TimeUnit;

public class MultiplayerActivity extends AppCompatActivity implements ListRoomsFragment.OnItemSelectedListener {

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        DetailRoomFragment oldDetailFragment = (DetailRoomFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container);
        ListRoomsFragment oldRoomsFragment = (ListRoomsFragment) getSupportFragmentManager().findFragmentById(R.id.rooms_container);
        if(oldDetailFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(oldDetailFragment).commit();
        }
        if(oldRoomsFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(oldRoomsFragment).commit();
        }
        /**/
        //Получение списка комнат
        GetRoomListTask task = new GetRoomListTask();
        task.execute();

    }

    @Override
    public void onItemSelected(int position) {

        DetailRoomFragment oldFragment = (DetailRoomFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container);

        if(oldFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
        }

        //Получение данных о комнате
        GetRoomDetailTask task = new GetRoomDetailTask();
        task.roomId = position;
        task.execute();

    }

    class GetRoomListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            getSupportFragmentManager().beginTransaction().add(R.id.rooms_container, new ListRoomsFragment()).commit();

            //Получение данных о комнате
            GetRoomDetailTask task = new GetRoomDetailTask();
            task.roomId = 0; //Первая комната
            task.execute();
        }
    }

    class GetRoomDetailTask extends AsyncTask<Integer, Void, Integer> {
        Integer roomId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                TimeUnit.SECONDS.sleep(1); //Запрос на сервер
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            DetailRoomFragment newFragment = DetailRoomFragment.getInstance(roomId);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.detail_container, newFragment);
            transaction.commit();
            progressBar.setVisibility(View.GONE);
        }
    }
}