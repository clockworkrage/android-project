package com.labyrinth.team01.labyrinth;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v7.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.widget.ProgressBar;

import com.labyrinth.team01.labyrinth.fragments.DetailRoomFragment;
import com.labyrinth.team01.labyrinth.fragments.ListRoomsFragment;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ListRoomsFragment.OnItemSelectedListener {

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mScreenTitles;

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

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mDrawerList = (ListView) findViewById(R.id.navList);

        mScreenTitles = getResources().getStringArray(R.array.screen_array);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mScreenTitles));
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
                mDrawerLayout.closeDrawer(Gravity.LEFT);

                DetailRoomFragment oldDetailFragment = (DetailRoomFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container);
                ListRoomsFragment oldRoomsFragment = (ListRoomsFragment) getSupportFragmentManager().findFragmentById(R.id.rooms_container);
                if (oldDetailFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(oldDetailFragment).commit();
                }
                if (oldRoomsFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(oldRoomsFragment).commit();
                }
                /**/
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    startActivity(intent);
                }

                if(position == 1) {
                    //Получение списка комнат
                    GetRoomListTask task = new GetRoomListTask();
                    task.execute();
                }

                if(position == 3) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }

            }
        });

        Toolbar toolbar = (Toolbar)  findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar
                , R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

        };

        drawerLayout.setDrawerListener(mDrawerToggle);
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}
