package com.labyrinth.team01.labyrinth;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v7.app.ActionBar;
import android.content.SharedPreferences;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.labyrinth.team01.labyrinth.fragments.DetailRoomFragment;
import com.labyrinth.team01.labyrinth.fragments.ListRoomsFragment;
import com.labyrinth.team01.labyrinth.fragments.ReplayListFragment;
import com.labyrinth.team01.labyrinth.utils.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListRoomsFragment.OnItemSelectedListener, ReplayListFragment.OnItemSelectedListener {

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mScreenTitles;
//    private ImageView logoImage;

    private ProgressBar progressBar;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private Button createButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout mainContent = (RelativeLayout) findViewById(R.id.mainContent);
        if (mainContent != null){
            mainContent.setBackgroundColor(Color.TRANSPARENT);
        }
        SharedPreferences sPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.style_pref_default);
        String appStyle = sPref.getString(getString(R.string.style_pref), defaultValue);
        if(appStyle.equals(getString(R.string.style_pref_dark))) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mDrawerList = (ListView) findViewById(R.id.navList);
        createButton = (Button) findViewById(R.id.create_game);


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateRoomActivity.class);
                startActivity(intent);
            }
        });

        mDatabaseHelper = new DatabaseHelper(this);
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        mScreenTitles = getResources().getStringArray(R.array.screen_array);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mScreenTitles));
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                createButton.setVisibility(View.GONE);

                DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
                RelativeLayout mainContent = (RelativeLayout) findViewById(R.id.mainContent);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                DetailRoomFragment oldDetailFragment = (DetailRoomFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container);
                ListRoomsFragment oldRoomsFragment = (ListRoomsFragment) getSupportFragmentManager().findFragmentById(R.id.rooms_container);
                ReplayListFragment oldReplayListFragment = (ReplayListFragment) getSupportFragmentManager().findFragmentById(R.id.replays_container);

                if(    position != 0
                    && position != 3) {
                    if (oldDetailFragment != null) {
                        getSupportFragmentManager().beginTransaction().remove(oldDetailFragment).commit();
                    }
                    if (oldRoomsFragment != null) {
                        getSupportFragmentManager().beginTransaction().remove(oldRoomsFragment).commit();
                    }
                    if (oldReplayListFragment != null) {
                        getSupportFragmentManager().beginTransaction().remove(oldReplayListFragment).commit();
                    }
                }
                /**/
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    startActivity(intent);
                }

                if(position == 1) {
                    createButton.setVisibility(View.VISIBLE);
                    if (mainContent != null){
                        mainContent.setBackgroundColor(Color.TRANSPARENT);
                    }
                    //Получение списка комнат
                    GetRoomListTask task = new GetRoomListTask();
                    task.execute();
                }
                if(position == 2){
                    if (mainContent != null){
                        mainContent.setBackgroundColor(Color.TRANSPARENT);
                    }
                    GetReplaysListTask task = new GetReplaysListTask();
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

        if (oldFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
        }

        //Получение данных о комнате
        ListRoomsFragment listRoomsFragment = (ListRoomsFragment) getSupportFragmentManager().findFragmentById(R.id.rooms_container);
        int roomId = listRoomsFragment.getRoomId(position);
        if (roomId != 0) {
            GetRoomDetailTask task = new GetRoomDetailTask();
            task.roomId = roomId;
            task.execute();
        }
    }

    @Override
    public void onItemSelectedReplay(int position, String value) {
        Intent intent = new Intent(MainActivity.this, ReplayActivity.class);

        intent.putExtra("labirinth_id", Integer.parseInt(value.substring(1, value.indexOf(","))));
        startActivity(intent);
    }

    class GetRoomListTask extends AsyncTask<Void, Void, Void> {

        List<String> listRooms = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://"+ getString(R.string.server_ip)+":8080/api/listroom/");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();

                if (code == 200) {
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String result = "", line = "";
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }

                    JSONObject jObject = new JSONObject(result);

                    JSONObject body = jObject.getJSONObject("body");

                    JSONArray rooms = body.getJSONArray("arr");

                    for (int i=0; i < rooms.length(); i++)
                    {
                        try {
                            String roomId = rooms.get(i).toString();
                            listRooms.add(roomId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                connection.disconnect();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListRoomsFragment listRoomsFragment = new ListRoomsFragment();
            listRoomsFragment.setListRooms(listRooms.toArray(new String[0]));
            getSupportFragmentManager().beginTransaction().add(R.id.rooms_container, listRoomsFragment).commit();

            progressBar.setVisibility(View.GONE);

        }

    }



    class GetRoomDetailTask extends AsyncTask<Void, Void, Void> {
        Integer roomId = 0;
        Integer max_players = 0;
        Integer min_players = 0;
        Integer players = 0;
        Integer status = 0;
        Integer time_step = 0;
        Integer room_size = 0;
        Boolean is_password = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://"+ getString(R.string.server_ip)+":8080/api/roominfo/?roomId=" + roomId.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //connection.setRequestProperty("roomId", );
                connection.connect();
                int code = connection.getResponseCode();

                if (code == 200) {
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String result = "", line = "";
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }

                    JSONObject jObject = new JSONObject(result);
                    String type = (String)jObject.get("type");
                    if (type.equals("room_info")) {

                        JSONObject body = jObject.getJSONObject("body");

                        min_players = (Integer) body.get("min_players");
                        max_players = (Integer) body.get("max_players");
                        players = (Integer) body.get("players");
                        status = (Integer) body.get("status");
                        time_step = (Integer) body.get("time_step");
                        //room_size = (Integer) body.get("");
                        is_password = (Boolean) body.get("is_password");
                    }
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
                return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            Intent intent = new Intent(MainActivity.this, RoomDetailActivity.class);
            intent.putExtra("min_players", min_players);
            intent.putExtra("max_players", max_players);
            intent.putExtra("players", players);
            intent.putExtra("status", status);
            intent.putExtra("time_step", time_step);
            intent.putExtra("is_password", is_password);
            intent.putExtra("roomId", roomId);
            startActivity(intent);
            progressBar.setVisibility(View.GONE);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    class GetReplaysListTask extends AsyncTask<Void, Void, Void>{
        List<String> listReplays = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = mSqLiteDatabase.query(DatabaseHelper.TABLE_REPLAYS, new String[]{
                    DatabaseHelper._ID,
                    DatabaseHelper.COLUMN_REPLAYS_DATE,
                    DatabaseHelper.COLUMN_REPLAYS_LENGTH,
                    DatabaseHelper.COLUMN_REPLAYS_WIDTH
            }, null, null, null, null, null);

            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                String str = Integer.toString(cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID)));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REPLAYS_DATE));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                date = format.format(new Date(Long.parseLong(date)));
                String length = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REPLAYS_LENGTH));
                listReplays.add("№" + str + ", " + date + "; " + length + " steps");
                cursor.moveToNext();
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ReplayListFragment replayListFragment = new ReplayListFragment();
            replayListFragment.setListReplays(listReplays.toArray(new String[0]));
            getSupportFragmentManager().beginTransaction().add(R.id.replays_container, replayListFragment).commit();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else
                mDrawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String screen = savedInstanceState.getString("screen","null");
        if(screen.equals("multiplayer"))
            createButton.setVisibility(View.VISIBLE);

    }


    protected void onSaveInstanceState(Bundle outState) {
        ListRoomsFragment listRoomsFragment = (ListRoomsFragment) getSupportFragmentManager().findFragmentById(R.id.rooms_container);
        if(listRoomsFragment != null)
            outState.putString("screen", "multiplayer");
        super.onSaveInstanceState(outState);
    }

}


