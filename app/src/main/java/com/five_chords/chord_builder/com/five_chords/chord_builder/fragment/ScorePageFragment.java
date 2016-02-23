package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;

import java.util.Random;

/**
 * A Fragment containing the score page.
 * @author tstone95
 */
public class ScorePageFragment extends DialogFragment implements TabLayout.OnTabSelectedListener
{
    /** Handle to the View of this ScorePageFragment */
    private View view;

    /**
     * Required empty public constructor.
     */
    public ScorePageFragment()
    {   }

    /**
     * Create a new instance of ScorePageFragment, providing ...
     * as an argument.
     * @return A new instance of ScorePageFragment
     */
    public static ScorePageFragment newInstance()
    {
        ScorePageFragment f = new ScorePageFragment();

        // Supply arguments to Bundle
        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
    }

    /**
     * Fills the list of scores for the given chord type.
     * @param major Whether or not to add major chords scores
     * @param minor Whether or not to add minor chords scores
     */
    public void addScores(boolean major, boolean minor)
    {
        if (view == null)
        {
            Log.e(getClass().getSimpleName(), "Error adding scores: view is null");
            return;
        }

        // Populate list view of scores
        ListView listView = (ListView)view.findViewById(R.id.score_page_scorelist);
        ScoreItemAdapter adapter = new ScoreItemAdapter(getActivity(), android.R.layout.simple_list_item_1);

        // Compute the start index into the score name array
        int startIndex;

        if (major)
            startIndex = 0;
        else if (minor)
            startIndex = 12;
        else
            startIndex = 24;

        // Get scores
        for (int i = 0; i < 12; ++i)
            adapter.add(Score.scores[startIndex + i]);

        // Set the adapter
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Set size if dialog
        if (getDialog() != null)
        {
            int width = getResources().getDimensionPixelSize(R.dimen.score_dialog_width);
            int height = getResources().getDimensionPixelSize(R.dimen.score_dialog_height);
            getDialog().getWindow().setLayout(width, height);
        }
    }

    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_score_page, container, false);

        // Set title
        if (getDialog() != null)
            getDialog().setTitle(R.string.scores);

        // Create tabs
        TabLayout tabs = (TabLayout)view.findViewById(R.id.tabs_score_page);
        TabLayout.Tab majorTab = tabs.newTab();
        majorTab.setText(getResources().getString(R.string.major_chords));
        tabs.addTab(majorTab);

        TabLayout.Tab minorTab = tabs.newTab();
        minorTab.setText(getResources().getString(R.string.minor_chords));
        tabs.addTab(minorTab);

        TabLayout.Tab dominantTab = tabs.newTab();
        dominantTab.setText(getResources().getString(R.string.dominant_chords));
        tabs.addTab(dominantTab);

        // Add listener
        tabs.setOnTabSelectedListener(this);

        // Create view mode tabs
        tabs = (TabLayout)view.findViewById(R.id.tabs_view_mode_score_page);

        TabLayout.Tab currentTab = tabs.newTab();
        currentTab.setText(getResources().getString(R.string.current));
        tabs.addTab(currentTab);

        TabLayout.Tab historyTab = tabs.newTab();
        historyTab.setText(getResources().getString(R.string.history));
        tabs.addTab(historyTab);

        // Add listener
        tabs.setOnTabSelectedListener(this);

        // Populate list view of scores
        addScores(true, false);

        // Return the created View
        return view;
    }

    /**
     * Called when a tab enters the selected state.
     * @param tab The tab that was selected
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        if (tab.getText() == null)
            return;

        if (tab.getText().equals(getString(R.string.major_chords)))
            addScores(true, false);
        else if (tab.getText().equals(getString(R.string.minor_chords)))
            addScores(false, true);
        else if (tab.getText().equals(getString(R.string.dominant_chords)))
            addScores(false, false);
    }

    /**
     * Called when a tab exits the selected state.
     * @param tab The tab that was unselected
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab)
    {    }

    /**
     * Called when a tab that is already selected is chosen again by the user. Some applications
     * may use this action to return to the top level of a category.
     * @param tab The tab that was reselected.
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab)
    {    }

    /**
     * Implementation of an ArrayAdapter containing ScoreItems.
     */
    public static class ScoreItemAdapter extends ArrayAdapter<Score.ScoreWrapper>
    {
        /**
         * Constructor.
         * @param context  The current context
         * @param resource The resource ID for a layout file containing a TextView to use
         */
        public ScoreItemAdapter(Context context, int resource)
        {
            super(context, resource);
        }

        /**
         * Get the view that displays the data at the specified position.
         * @param position The position of the item
         * @param convertView The old view
         * @param parent The parent viewgroup
         * @return The view that displays the data
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view;

            // Reuse old view if possible
            if (convertView != null && convertView.getId() == R.id.component_score_item)
                view = convertView;
            else
                view = LayoutInflater.from(getContext()).inflate(R.layout.component_score_item, parent, false);

            // Get the Score object that this item represents
            Score.ScoreWrapper item = getItem(position);
            TextView chordName  = (TextView)view.findViewById(R.id.textview_score_item_chord_name);
            TextView chordScore  = (TextView)view.findViewById(R.id.textview_score_item_score);
            ProgressBar chordProgress = (ProgressBar)view.findViewById(R.id.progress_score_item_score);

            // Set the chord name
            chordName.setText(item.CHORD_NAME);

            // Set the score progress
            if (item.numTotalGuesses == 0)
            {
                chordScore.setText("Not Attempted");
                chordProgress.setProgress(0);
            }
            else
            {
                // Set progress text
                double progress = (100.0 * item.numCorrectGuesses / item.numTotalGuesses);
                chordScore.setText(String.format("%.3f", progress) + " %");

                // Set progress value
                chordProgress.setProgress((int)Math.round(progress));
            }

            return view;
        }
    }
}
