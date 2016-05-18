package com.labyrinth.team01.labyrinth;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class CreateRoomActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private Spinner sizeRoomSpinner;
    private Spinner delayRoomSpinner;
    private Spinner maxPlayersSpinner;
    private Spinner minPalyersSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button button = (Button) findViewById(R.id.create_room_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateRoomTask task = new CreateRoomTask();
                task.size = (String)sizeRoomSpinner.getSelectedItem();
                task.minPlayers = (String)minPalyersSpinner.getSelectedItem();
                task.maxPlayers = (String)maxPlayersSpinner.getSelectedItem();
                task.delay = (String)delayRoomSpinner.getSelectedItem();
                task.execute();
            }
        });
        sizeRoomSpinner = (Spinner) findViewById(R.id.spinner_room_size);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.romm_size_array, R.layout.style_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeRoomSpinner.setAdapter(adapter);

        delayRoomSpinner = (Spinner) findViewById(R.id.spinner_delay);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.delay_array, R.layout.style_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delayRoomSpinner.setAdapter(adapter);

        maxPlayersSpinner = (Spinner) findViewById(R.id.spinner_max_players);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.max_players_array, R.layout.style_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxPlayersSpinner.setAdapter(adapter);

        minPalyersSpinner = (Spinner) findViewById(R.id.spinner_min_palyers);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.min_players_array, R.layout.style_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minPalyersSpinner.setAdapter(adapter);

    }


    class CreateRoomTask extends AsyncTask<Void, Void, Void> {
        public String size;
        public String minPlayers;
        public String maxPlayers;
        public String delay;
        public Integer roomId = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection connection = null;
            try {

                URL url = new URL("http://"+ getString(R.string.server_ip)+":8080/api/createroom/");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                String data = URLEncoder.encode("size", "UTF-8") + "=" + URLEncoder.encode(size, "UTF-8");
                data += "&" + URLEncoder.encode("minPlayers", "UTF-8") + "=" + URLEncoder.encode(minPlayers, "UTF-8");
                data += "&" + URLEncoder.encode("maxPlayers", "UTF-8") + "=" + URLEncoder.encode(maxPlayers, "UTF-8");
                data += "&" + URLEncoder.encode("delay", "UTF-8") + "=" + URLEncoder.encode(delay, "UTF-8");
                connection.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(data);
                wr.flush();

                int code = connection.getResponseCode();
                if (code == 200) {
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String result = "", line = "";
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                    Log.e("QWQWQW", result);
                    JSONObject jObject = new JSONObject(result);

                    JSONObject body = jObject.getJSONObject("body");

                    roomId = body.getInt("id");
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
            Intent intent = new Intent(getBaseContext(), MultiplayerGameActivity.class);
            intent.putExtra("roomId", roomId.toString());
            intent.putExtra("password", "");
            startActivity(intent);
        }

    }
}
