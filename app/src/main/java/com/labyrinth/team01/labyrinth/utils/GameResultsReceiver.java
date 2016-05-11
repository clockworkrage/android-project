package com.labyrinth.team01.labyrinth.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


/**
 * Created by SwordMaster on 10.05.2016.
 */
@SuppressLint("ParcelCreator")
public class GameResultsReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public GameResultsReceiver(Handler handler) {
        super(handler);
        // TODO Auto-generated constructor stub
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);

    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

}
