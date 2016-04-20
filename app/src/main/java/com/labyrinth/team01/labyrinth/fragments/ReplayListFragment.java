package com.labyrinth.team01.labyrinth.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.labyrinth.team01.labyrinth.R;

/**
 * Created by Андрей on 20.04.2016.
 */
public class ReplayListFragment extends ListFragment {
    String[] listReplays = new String[]{"qweret"};

    public void setListReplays(String[] listReplays) {
        this.listReplays = listReplays;
    }

    private OnItemSelectedListener mCallback;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onItemSelectedReplay(position, listReplays[position]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), R.layout.replays_list , listReplays);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
        }
    }

    // Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onItemSelectedReplay(int position, String value);
    }
}
