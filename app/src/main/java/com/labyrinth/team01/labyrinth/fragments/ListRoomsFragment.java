package com.labyrinth.team01.labyrinth.fragments;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.labyrinth.team01.labyrinth.R;

/**
 * Created by SwordMaster on 25.03.2016.
 */
public class ListRoomsFragment extends ListFragment {
    String[] numbers_text = new String[]{"Комната1", "Комната2", "Комната3", "Комната4",
            "Комната5", "Комната6", "Комната7"};
    private OnItemSelectedListener mCallback;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onItemSelected(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), R.layout.room_list_item ,
                numbers_text);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
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

    // Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onItemSelected(int position);
    }
}
