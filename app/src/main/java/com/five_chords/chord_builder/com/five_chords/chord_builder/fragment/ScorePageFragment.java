package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    /** The bundle id for the current score view mode flag */
    private static final String BUNDLE_ID_SCORE_VIEW_MODE = "ScorePageFragment.currentScoreViewMode";

    /** The bundle id for the major chords view flag */
    private static final String BUNDLE_ID_MAJOR_CHORDS = "ScorePageFragment.onMajorChords";

    /** The bundle id for the minor chords view flag */
    private static final String BUNDLE_ID_MINOR_CHORDS = "ScorePageFragment.onMinorChords";

    /** Handle to the View of this ScorePageFragment */
    private View view;

    /** Stores the current score View mode */
    private boolean currentScoreViewMode;

    /** Stores whether or not major chords are currently selected */
    private boolean onMajorChords;

    /** Stores whether or not minor chords are currently selected */
    private boolean onMinorChords;

    /**
     * Required empty public constructor.
     */
    public ScorePageFragment()
    {   }

    /**
     * Create a new instance of ScorePageFragment.
     * @param onMajorChords Whether or not major chords should be selected
     * @param onMinorChords Whether or not minor chords should be selected
     * @param currentScoreViewMode The current score view mode (current or history)
     * @return A new new instance of ScorePageFragment
     */
    public static ScorePageFragment newInstance(boolean onMajorChords, boolean onMinorChords, boolean currentScoreViewMode)
    {
        ScorePageFragment f = new ScorePageFragment();

        // Supply arguments to Bundle
        Bundle args = new Bundle();

        args.putBoolean(BUNDLE_ID_MAJOR_CHORDS, onMajorChords);
        args.putBoolean(BUNDLE_ID_MINOR_CHORDS, onMinorChords);
        args.putBoolean(BUNDLE_ID_SCORE_VIEW_MODE, currentScoreViewMode);

        f.setArguments(args);

        return f;
    }

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
        AbstractScoreItemAdapter adapter;

        if (currentScoreViewMode)
            adapter = new ScoreItemAdapter(getActivity(), android.R.layout.simple_list_item_1);
        else
            adapter = new ScoreItemHistoryAdapter(getActivity(), android.R.layout.simple_list_item_1);

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
            currentScoreViewMode = true;
            onMajorChords = true;
            onMinorChords = false;
        }
        else
        {
            currentScoreViewMode = arguments.getBoolean(BUNDLE_ID_SCORE_VIEW_MODE);
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
        addScoreViewModeTabs(view);

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
        else // Set tab view mode
        {
            currentScoreViewMode = tab.getText().equals(getString(R.string.current));
        }

        // Refresh
        refreshListView();

        // Save arguments
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            arguments.putBoolean(BUNDLE_ID_SCORE_VIEW_MODE, currentScoreViewMode);
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
     * Used to add the score view mode tabs.
     * @param view the View to which to add the tabs
     */
    private void addScoreViewModeTabs(View view)
    {
        // Create view mode tabs
        TabLayout tabs = (TabLayout)view.findViewById(R.id.tabs_view_mode_score_page);

        TabLayout.Tab currentTab = tabs.newTab();
        currentTab.setText(getResources().getString(R.string.current));
        tabs.addTab(currentTab);

        TabLayout.Tab historyTab = tabs.newTab();
        historyTab.setText(getResources().getString(R.string.history));
        tabs.addTab(historyTab);

        if (currentScoreViewMode)
            currentTab.select();
        else
            historyTab.select();

        // Add listener
        tabs.setOnTabSelectedListener(this);
    }

    /**
     * Implementation of an AbstractScoreItemAdapter containing views for displaying the current score of a chord.
     */
    public static class ScoreItemAdapter extends AbstractScoreItemAdapter
    {
        /**
         * Constructor.
         *
         * @param context  The current context
         * @param resource The resource ID for a layout file containing a TextView to use
         */
        public ScoreItemAdapter(Context context, int resource)
        {
            super(context, resource);
        }

        /**
         * Override to get the view id of this AbstractScoreItemAdapter.
         *
         * @return The view id of this AbstractScoreItemAdapter
         */
        @Override
        public int getViewId()
        {
            return R.id.component_score_item;
        }

        /**
         * Override to get the view layout id of this AbstractScoreItemAdapter.
         * @return The view layout id of this AbstractScoreItemAdapter
         */
        @Override
        public int getViewLayout()
        {
            return R.layout.component_score_item;
        }

        /**
         * Override to initialize the given View in the array.
         *
         * @param view     The view at the given position
         * @param position The position of the view
         */
        @Override
        public void initialize(View view, int position)
        {
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
        }
    }

    /**
     * Implementation of an AbstractScoreItemAdapter containing views for displaying the history of a chords scores.
     */
    public static class ScoreItemHistoryAdapter extends AbstractScoreItemAdapter
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
         * Override to get the view id of this AbstractScoreItemAdapter.
         *
         * @return The view id of this AbstractScoreItemAdapter
         */
        @Override
        public int getViewId()
        {
            return R.id.component_score_history_item;
        }

        /**
         * Override to get the view layout id of this AbstractScoreItemAdapter.
         * @return The view layout id of this AbstractScoreItemAdapter
         */
        @Override
        public int getViewLayout()
        {
            return R.layout.component_score_history_item;
        }

        /**
         * Override to initialize the given View in the array.
         *
         * @param view     The view at the given position
         * @param position The position of the view
         */
        @Override
        public void initialize(View view, int position)
        {
            Score.ScoreWrapper item = getItem(position);
            TextView textView = (TextView)view.findViewById(R.id.textview_history);

            textView.setText(item.CHORD_NAME + " - History will display here");
        }
    }

    /**
     * Abstract implementation of an ArrayAdapter containing ScoreItems.
     */
    public static abstract class AbstractScoreItemAdapter extends ArrayAdapter<Score.ScoreWrapper>
    {
        /**
         * Constructor.
         * @param context  The current context
         * @param resource The resource ID for a layout file containing a TextView to use
         */
        public AbstractScoreItemAdapter(Context context, int resource)
        {
            super(context, resource);
        }

        /**
         * Override to get the view id of this AbstractScoreItemAdapter.
         * @return The view id of this AbstractScoreItemAdapter
         */
        public abstract int getViewId();

        /**
         * Override to get the view layout id of this AbstractScoreItemAdapter.
         * @return The view layout id of this AbstractScoreItemAdapter
         */
        public abstract int getViewLayout();

        /**
         * Override to initialize the given View in the array.
         * @param view The view at the given position
         * @param position The position of the view
         */
        public abstract void initialize(View view, int position);

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
            if (convertView != null && convertView.getId() == getViewId())
                view = convertView;
            else
                view = LayoutInflater.from(getContext()).inflate(getViewLayout(), parent, false);

            // Initialize the view
            initialize(view, position);

            return view;
        }
    }
}
