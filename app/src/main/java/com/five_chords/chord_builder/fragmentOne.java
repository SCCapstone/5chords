package com.five_chords.chord_builder;

import android.app.Fragment;

/**
 * used for linking the activity view
 */
public class fragmentOne extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_1, parentViewGroup, false);
        return rootView;
    }
}
