package com.labyrinth.team01.labyrinth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.labyrinth.team01.labyrinth.game.Labirinth;
import com.labyrinth.team01.labyrinth.game.LabirinthImpl;
import com.labyrinth.team01.labyrinth.game.TypeLabirinthsCells;
import com.labyrinth.team01.labyrinth.game.Vec2d;
import com.labyrinth.team01.labyrinth.utils.DatabaseHelper;


/**
 * Created by Андрей on 20.04.2016.
 */
public class ReplayActivity extends Activity {
    private int id;
    private TextView text;
    private DatabaseHelper mDatabaseHelper;
    private String path;
    private Labirinth labirinth;
    private int width;
    private int height;
    private long seed;
    private Vec2d pos = new Vec2d();
    private int step = -1;


    private static final String KEY_STEP = "STEP";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_STEP, step);
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

        setContentView(R.layout.activity_replays);
        text = (TextView) findViewById(R.id.replayField);
        text.setTypeface(Typeface.MONOSPACE);

        id = getIntent().getIntExtra("labirinth_id", 0);

        mDatabaseHelper = new DatabaseHelper(this);

        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(DatabaseHelper.TABLE_REPLAYS, new String[]{
                DatabaseHelper._ID,
                DatabaseHelper.COLUMN_REPLAYS_SEED,
                DatabaseHelper.COLUMN_REPLAYS_WIDTH,
                DatabaseHelper.COLUMN_REPLAYS_HEIGHT,
                DatabaseHelper.COLUMN_REPLAYS_PATH
        }, DatabaseHelper._ID + " = ?", new String[]{Integer.toString(id)},
         null, null, null, null);



        cursor.moveToFirst();
        path = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REPLAYS_PATH));
        seed = Long.parseLong(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REPLAYS_SEED)));
        width = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_REPLAYS_WIDTH));
        height = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_REPLAYS_HEIGHT));
        cursor.close();


        labirinth = new LabirinthImpl(width, height, seed);
        pos = labirinth.getStartPosition();

        if (savedInstanceState != null) {
            int i = savedInstanceState.getInt(KEY_STEP, -1);
            for(; i>0; --i){
                if(step != -1 && step < path.length()) {
                    updPos(path.charAt(step));
                }
                text.setText(getField(pos));
                ++step;
                if(step == path.length()){
                    ifWin();
                }
            }
        }

        onClickNext(null);
    }

    private void updPos(char c){
        switch (c){
            case 'w': pos.x -=1; break;
            case 's': pos.x +=1; break;
            case 'a': pos.y -=1; break;
            case 'd': pos.y +=1; break;
            default: break;
        }
    }

    public void onClickNext(View view){
        if(step >= path.length()){
            return;
        }
        if(step != -1) {
            updPos(path.charAt(step));
        }
        text.setText(getField(pos));
        ++step;
        if(step == path.length()){
            ifWin();
        }
    }

    private String getField(Vec2d p){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=-3; i<4; ++i){
            for(int j=-3; j<4; ++j){
                if(i==0 && j==0){
                    stringBuilder.append("OO");
                } else {
                    stringBuilder.append(TypeLabirinthsCells.getChar(labirinth.getCell((int) p.x + i, (int) p.y + j)));
                    stringBuilder.append(TypeLabirinthsCells.getChar(labirinth.getCell((int) p.x + i, (int) p.y + j)));
                }
            }
            stringBuilder.append('\n');
        }
        return  stringBuilder.toString();
    }

    private void ifWin(){
        text.setText("You win!");
    }
}
