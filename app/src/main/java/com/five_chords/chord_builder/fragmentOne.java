package com.five_chords.chord_builder;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * used for linking the activity view
 */
public class fragmentOne extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_1, parentViewGroup, false);
    }
}