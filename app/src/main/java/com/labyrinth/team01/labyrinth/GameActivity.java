package com.labyrinth.team01.labyrinth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.labyrinth.team01.labyrinth.game.Labirinth;
import com.labyrinth.team01.labyrinth.game.LabirinthImpl;
import com.labyrinth.team01.labyrinth.game.RandomHelper;
import com.labyrinth.team01.labyrinth.game.TypeLabirinthsCells;
import com.labyrinth.team01.labyrinth.game.Vec2d;
import com.labyrinth.team01.labyrinth.utils.DatabaseHelper;

/**
 * Created by Андрей on 17.04.2016.
 */
public class GameActivity extends Activity {
    private TextView text;
    private int size = RandomHelper.sRand(5, 25);
    private Labirinth labirinth = new LabirinthImpl(size, size);
    private StringBuilder playerPath = new StringBuilder();
    private Vec2d pos = labirinth.getStartPosition();
    private boolean isWin = false;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    private static final String KEY_WIDTH = "WIDTH";
    private static final String KEY_HIDTH = "HIDTH";
    private static final String KEY_SEED = "SEED";
    private static final String KEY_PLAYER_PATH = "PLAYER_PATH";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_HIDTH, labirinth.getHeight());
        outState.putInt(KEY_WIDTH, labirinth.getWidth());
        outState.putLong(KEY_SEED, labirinth.getSeed());
        outState.putString(KEY_PLAYER_PATH, playerPath.toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.style_pref_default);
        String appStyle = sPref.getString(getString(R.string.style_pref), defaultValue);



        if(appStyle.equals(getString(R.string.style_pref_dark))) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);

        }

        setContentView(R.layout.activity_game);
        text = (TextView) findViewById(R.id.textView);
        text.setTypeface(Typeface.MONOSPACE);

        if (savedInstanceState != null) {
            int width = savedInstanceState.getInt(KEY_WIDTH, 0);
            int height = savedInstanceState.getInt(KEY_WIDTH, 0);
            long seed = savedInstanceState.getLong(KEY_SEED, 0);
            playerPath.append(savedInstanceState.getString(KEY_PLAYER_PATH));
            System.out.println("Saved seed: " + seed);
            labirinth = new LabirinthImpl(width, height, seed);
            pos = labirinth.getStartPosition();
            String temp = playerPath.toString();
            for(int i=0; i<temp.length(); ++i){
                switch (temp.charAt(i)){
                    case 'w': onClickTop(null); break;
                    case 's': onClickBottom(null); break;
                    case 'a': onClickLeft(null); break;
                    case 'd': onClickRight(null); break;
                }
            }
        }

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

    private void writeReplay(String path){
        mDatabaseHelper.savePassage(mSqLiteDatabase, labirinth.getSeed(), labirinth.getHeight(), labirinth.getWidth(), path);
    }

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
            playerPath.append('w');
        }
        step();
    }

    public void onClickBottom(View view){
        if(isWin) return;
        Vec2d newPos = new Vec2d();
        newPos.set(pos.x+1, pos.y);
        if(isMayMove(newPos)){
            pos = newPos;
            playerPath.append('s');
        }
        step();
    }

    public void onClickLeft(View view){
        if(isWin) return;
        Vec2d newPos = new Vec2d();
        newPos.set(pos.x, pos.y-1);
        if(isMayMove(newPos)){
            pos = newPos;
            playerPath.append('a');
        }
        step();
    }

    public void onClickRight(View view){
        if(isWin) return;
        Vec2d newPos = new Vec2d();
        newPos.set(pos.x, pos.y+1);
        if(isMayMove(newPos)){
            pos = newPos;
            playerPath.append('d');
        }
        step();
    }
}
