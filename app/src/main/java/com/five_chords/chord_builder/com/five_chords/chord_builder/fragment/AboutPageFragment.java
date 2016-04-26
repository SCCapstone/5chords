package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

/**
 * The About page Activity.
 * @date 31 March 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */

public class AboutPageFragment extends Fragment
{
    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_page, container, false);

//        // Set Done Button action
//        Button doneButton = (Button) view.findViewById(R.id.button_about_page_done);
//        doneButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Activity activity = AboutPageFragment.this.getActivity();
//
//                if (activity instanceof MainActivity)
//                    ((MainActivity)activity).setCurrentDrawer(MainActivity.DrawerFragment.MAIN.ordinal());
//            }
//        });

        return view;
    }
}
