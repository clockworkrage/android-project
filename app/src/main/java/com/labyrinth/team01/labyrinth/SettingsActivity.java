package com.labyrinth.team01.labyrinth;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.style_pref_default);
        String appStyle = sPref.getString(getString(R.string.style_pref), defaultValue);
        if (appStyle.equals(getString(R.string.style_pref_dark))) {
            setTheme(R.style.DarkTheme);
        } else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_settings);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.styles_array, R.layout.style_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (appStyle.equals(getString(R.string.style_pref_dark))) {
            spinner.setSelection(0);
        } else
            spinner.setSelection(1);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                SharedPreferences sPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                if (position == 0) {
                    editor.clear();
                    editor.putString(getString(R.string.style_pref), getString(R.string.style_pref_dark));
                    getBaseContext().setTheme(R.style.DarkTheme);
                    setTheme(R.style.DarkTheme);
                }
                if (position == 1) {
                    editor.clear();
                    editor.putString(getString(R.string.style_pref), getString(R.string.style_pref_light));
                    getBaseContext().setTheme(R.style.AppTheme);
                    setTheme(R.style.AppTheme);
                }
                editor.apply();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}
