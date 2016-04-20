package com.labyrinth.team01.labyrinth;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.labyrinth.team01.labyrinth.game.Labirinth;
import com.labyrinth.team01.labyrinth.game.LabirinthImpl;
import com.labyrinth.team01.labyrinth.game.TypeLabirinthsCells;
import com.labyrinth.team01.labyrinth.game.Vec2d;
import com.labyrinth.team01.labyrinth.utils.DatabaseHelper;

import java.util.Date;

/**
 * Created by Андрей on 17.04.2016.
 */
public class GameActivity extends Activity {
    private TextView text;
    private int size = 15;
    private Labirinth labirinth = new LabirinthImpl(size, size);
    private StringBuilder playerPath = new StringBuilder();
    private Vec2d pos = labirinth.getStartPosition();
    private boolean isWin = false;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.style_pref_default);
        String appStyle = sPref.getString(getString(R.string.style_pref), defaultValue);
        if(appStyle.equals(getString(R.string.style_pref_dark))) {
            setTheme(R.style.DarkTheme);
        } else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_game);
        text = (TextView) findViewById(R.id.textView);
        text.setTypeface(Typeface.MONOSPACE);
        updateLabirinth();

        mDatabaseHelper = new DatabaseHelper(this);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }

    private void updateLabirinth(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=-3; i<4; ++i){
            for(int j=-3; j<4; ++j){
                if(i==0 && j==0){
                    stringBuilder.append("OO");
                } else {
                    stringBuilder.append(TypeLabirinthsCells.getChar(labirinth.getCell((int) pos.x + i, (int) pos.y + j)));
                    stringBuilder.append(TypeLabirinthsCells.getChar(labirinth.getCell((int) pos.x + i, (int) pos.y + j)));
                }
            }
            stringBuilder.append('\n');
        }
        text.setText(stringBuilder.toString());
    }

    private boolean isMayMove(Vec2d position){
        return labirinth.getCell(position) != TypeLabirinthsCells.WALL;
    }

    private void ifWin(){
        isWin = true;
        text.setText("You win!");

        writeReplay(playerPath.toString());
    }

    private void writeReplay(String path){/*
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_REPLAYS_SEED, labirinth.getSeed());
        values.put(DatabaseHelper.COLUMN_REPLAYS_HEIGHT, labirinth.getHeight());
        values.put(DatabaseHelper.COLUMN_REPLAYS_WIDTH, labirinth.getWidth());
        values.put(DatabaseHelper.COLUMN_REPLAYS_DATE, Long.toString(new Date().getTime()));
        values.put(DatabaseHelper.COLUMN_REPLAYS_PATH, path);
        mSqLiteDatabase.insert(DatabaseHelper.TABLE_REPLAYS, null, values);
    */}

    private void step(){
        updateLabirinth();
        if(TypeLabirinthsCells.EXIT == labirinth.getCell(pos)){
            ifWin();
        }
    }

    public void onClickTop(View view){
        if(isWin) return;
        Vec2d newPos = new Vec2d();
        newPos.set(pos.x-1, pos.y);
        if(isMayMove(newPos)){
            pos = newPos;
            playerPath.append('t');
        }
        step();
    }

    public void onClickBottom(View view){
        if(isWin) return;
        Vec2d newPos = new Vec2d();
        newPos.set(pos.x+1, pos.y);
        if(isMayMove(newPos)){
            pos = newPos;
            playerPath.append('b');
        }
        step();
    }

    public void onClickLeft(View view){
        if(isWin) return;
        Vec2d newPos = new Vec2d();
        newPos.set(pos.x, pos.y-1);
        if(isMayMove(newPos)){
            pos = newPos;
            playerPath.append('l');
        }
        step();
    }

    public void onClickRight(View view){
        if(isWin) return;
        Vec2d newPos = new Vec2d();
        newPos.set(pos.x, pos.y+1);
        if(isMayMove(newPos)){
            pos = newPos;
            playerPath.append('r');
        }
        step();
    }
}
