package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.ScoreProgressView;

import java.util.Date;


/**
 * A Fragment containing the score page.
 * @author tstone95
 */
public class ScorePageFragment extends DialogFragment implements TabLayout.OnTabSelectedListener
{
    /** The bundle id for the major chords view flag */
    private static final String BUNDLE_ID_MAJOR_CHORDS = "ScorePageFragment.onMajorChords";

    /** The bundle id for the minor chords view flag */
    private static final String BUNDLE_ID_MINOR_CHORDS = "ScorePageFragment.onMinorChords";

    /** Handle to the View of this ScorePageFragment */
    private View view;

    /** Stores whether or not major chords are currently selected */
    private boolean onMajorChords;

    /** Stores whether or not minor chords are currently selected */
    private boolean onMinorChords;

    /**
     * Required empty public constructor.
     */
    public ScorePageFragment()
    {   }

//    /**
//     * Create a new instance of ScorePageFragment.
//     * @param onMajorChords Whether or not major chords should be selected
//     * @param onMinorChords Whether or not minor chords should be selected
//     * @return A new new instance of ScorePageFragment
//     */
//    public static ScorePageFragment newInstance(boolean onMajorChords, boolean onMinorChords)
//    {
//        ScorePageFragment f = new ScorePageFragment();
//
//        // Supply arguments to Bundle
//        Bundle args = new Bundle();
//        args.putBoolean(BUNDLE_ID_MAJOR_CHORDS, onMajorChords);
//        args.putBoolean(BUNDLE_ID_MINOR_CHORDS, onMinorChords);
//        f.setArguments(args);
//
//        return f;
//    }

    /**
     * Refreshes the list view containing scores.
     */
    public void refreshListView()
    {
        if (view == null)
        {
            Log.e(getClass().getSimpleName(), "Error adding scores: view is null");
            return;
        }

        // Populate list view of scores
        ListView listView = (ListView)view.findViewById(R.id.score_page_scorelist);
        ScoreItemHistoryAdapter adapter = new ScoreItemHistoryAdapter(getActivity(), android.R.layout.simple_list_item_1);

        // Compute the start index into the score name array
        int startIndex;

        if (onMajorChords)
            startIndex = 0;
        else if (onMinorChords)
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
        // Read arguments
        Bundle arguments = getArguments();

        if (arguments == null) // Use default values in this case
        {
            onMajorChords = true;
            onMinorChords = false;
        }
        else
        {
            onMajorChords = arguments.getBoolean(BUNDLE_ID_MAJOR_CHORDS);
            onMinorChords = arguments.getBoolean(BUNDLE_ID_MINOR_CHORDS);
        }

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_score_page, container, false);

        // Set title
        if (getDialog() != null)
            getDialog().setTitle(R.string.scores);

        // Create and add tabs
        addScoreTypeTabs(view);

        // Populate list view of scores
        refreshListView();

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
        {
            onMajorChords = true;
            onMinorChords = false;
        }
        else if (tab.getText().equals(getString(R.string.minor_chords)))
        {
            onMajorChords = false;
            onMinorChords = true;
        }
        else if (tab.getText().equals(getString(R.string.dominant_chords)))
        {
            onMajorChords = false;
            onMinorChords = false;
        }

        // Refresh
        refreshListView();

        // Save arguments
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            arguments.putBoolean(BUNDLE_ID_MAJOR_CHORDS, onMajorChords);
            arguments.putBoolean(BUNDLE_ID_MINOR_CHORDS, onMinorChords);
        }
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
     * Used to add the score type tabs.
     * @param view the View to which to add the tabs
     */
    private void addScoreTypeTabs(View view)
    {
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

        if (onMajorChords)
            majorTab.select();
        else if (onMinorChords)
            minorTab.select();
        else
            dominantTab.select();

        // Add listener
        tabs.setOnTabSelectedListener(this);
    }

    /**
     * Implementation of an ArrayAdapter containing views for displaying the history of a chord's scores.
     */
    public static class ScoreItemHistoryAdapter extends ArrayAdapter<Score>
    {
        /**
         * Constructor.
         *
         * @param context  The current context
         * @param resource The resource ID for a layout file containing a TextView to use
         */
        public ScoreItemHistoryAdapter(Context context, int resource)
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
            if (convertView != null && convertView.getId() == R.id.component_score_history_item)
                view = convertView;
            else
                view = LayoutInflater.from(getContext()).inflate(R.layout.component_score_history_item, parent, false);

            // Get components on View
            Score item = getItem(position);
            TextView textView = (TextView)view.findViewById(R.id.score_history_chord_name);
            ScoreProgressView progressView = (ScoreProgressView)view.findViewById(R.id.score_history_progress_view);

            // Set chord name
            textView.setText(getLabel(item));

            // Setup history view
//            item.loadHistory((Activity) getContext(), false); // Make sure the History is loaded

            progressView.setWidthPixels(ScoreProgressView.calculateWidth(item.getHistory()));
            progressView.setHeightPixels(ScoreProgressView.calculateHeight());
            progressView.setMinimumWidth(progressView.getWidthPixels());
            progressView.setMinimumHeight(progressView.getHeightPixels());
            progressView.setHistory(item.getHistory());

            // Scroll the progress bar all the way over to the right
            final HorizontalScrollView scrollView = (HorizontalScrollView)view.findViewById(R.id.score_history_progress_scrollview);
            scrollView.setVisibility(View.INVISIBLE);
            scrollView.post(new Runnable()
            {
                public void run()
                {
                    scrollView.scrollTo(10000, 0);
                    scrollView.setVisibility(View.VISIBLE);
                }
            });

             // TODO temporary
//            Log.w("PIX_WIDTH", item.CHORD_NAME + ": " + progressView.getWidthPixels());
//            Log.w("DISC_HIST", item.CHORD_NAME + ": " + item.getHistory().size + " points");
//            int i = 0;
//            for (Score.ScoreValue value: item.getHistory().values)
//                Log.w("\tPoint", "(" + (i++) + ") " + (value == null ? "NULL" : value.numCorrectGuesses + " / " + value.numTotalGuesses)
//                + " Time = " + (value == null ? "0" : "" + new Date(value.time * 1000).toString()));

            return view;
        }

        /**
         * Gets a label for a CurrentScoreWrapper.
         * @param score The Score
         * @return A label for the CurrentScoreWrapper
         */
        private String getLabel(Score score)
        {
            return score.CHORD_NAME + (score.getOverallValue().numTotalGuesses == 0 ?
                    " - " + getContext().getString(R.string.not_attempted) : "");
        }
    }
}
