package com.labyrinth.team01.labyrinth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        SharedPreferences sPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.style_pref_default);
        String appStyle = sPref.getString(getString(R.string.style_pref), defaultValue);
        if(appStyle.equals(getString(R.string.style_pref_dark))) {
            setTheme(R.style.DarkTheme);
        } else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_multiplayer);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
