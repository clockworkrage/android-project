package com.labyrinth.team01.labyrinth.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.labyrinth.team01.labyrinth.R;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SwordMaster on 25.03.2016.
 */
public class ListRoomsFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeLayout;
    ArrayAdapter<String> ListArrayAdapter;

    private OnItemSelectedListener mCallback;

    String[] listRooms;
    ArrayList<String> listIdRooms = new ArrayList<>();
    public void setListRooms(String[] listRooms) {
        this.listRooms = listRooms;
        for (String room: listRooms
             ) {
            listIdRooms.add(room);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onItemSelected(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<String> listDrawRooms = new ArrayList<>();
        for (String roomId:
                listRooms) {
            listDrawRooms.add(getResources().getString(R.string.room_name) + " " + roomId);
        }

        if (listDrawRooms.size() == 0)
            listDrawRooms.add(getString(R.string.room_warn));

        View view = inflater.inflate(R.layout.room_list_layout, container, false);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(this);

        ListArrayAdapter = new ArrayAdapter<String>(inflater.getContext(), R.layout.room_list_item ,
                listDrawRooms);
        setListAdapter(ListArrayAdapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onRefresh() {
        RefreshRoomListTask task = new RefreshRoomListTask();
        task.execute();
    }

    public Integer getRoomId(int position) {
        if (listIdRooms.size() > 0) {
            return Integer.parseInt(listIdRooms.get(position));
        }else
            return 0;
    }

    // Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onItemSelected(int position);
    }

    class RefreshRoomListTask extends AsyncTask<Void, Void, Void> {

        List<String> listRooms = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

                    listIdRooms.clear();
                    for (int i=0; i < rooms.length(); i++)
                    {
                        try {
                            String roomId = rooms.get(i).toString();
                            listRooms.add(roomId);
                            listIdRooms.add(roomId);
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

            ArrayList<String> listDrawRooms = new ArrayList<>();
            for (String roomId:
                    listRooms) {
                listDrawRooms.add(getResources().getString(R.string.room_name) + " " + roomId);
            }

            if (listDrawRooms.size() == 0)
                listDrawRooms.add(getString(R.string.room_warn));



            ListArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.room_list_item ,
                    listDrawRooms);
            setListAdapter(ListArrayAdapter);
            swipeLayout.setRefreshing(false);
        }
    }
}
