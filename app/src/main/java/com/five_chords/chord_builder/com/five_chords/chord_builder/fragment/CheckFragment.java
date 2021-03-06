package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.util.ChordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.com.five_chords.chord_builder.util.SoundHandler;

/**
 * A Fragment containing the chord check buttons.
 * @author tstone95
 */
public class CheckFragment extends Fragment
{
    /** The MainFragment to which this CheckFragment is attached. */
    private MainFragment mainFragment;

    /** The Button for playing the built chord. */
    private Button playBuiltChordButton;

    /** The soundHandler for the play built chord button. **/
    private SoundHandler soundHandler;

    /**
     * Required empty public constructor.
     */
    public CheckFragment()
    {   }

    /**
     * Called to play the build chord programmatically.
     * @param play Whether or not to play the chord
     */
    public void playBuiltChord(boolean play)
    {
        try
        {
            MainActivity.pressButton(playBuiltChordButton, play);
        }
        catch (Exception e)
        {
            silenceButtons();
        }
    }

    /**
     * Silence sound from all buttons
     */
    public void silenceButtons()
    {
        if (soundHandler != null)
            soundHandler.stopSound();
    }

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
        View view =  inflater.inflate(R.layout.fragment_check, container, false);

        // Initialize Buttons
        final Button checkChord = (Button) view.findViewById(R.id.button_check_user_chord);
        final Button playBuiltChord = (Button) view.findViewById(R.id.button_playback_slider_chord);
        playBuiltChordButton = playBuiltChord;

        soundHandler = new SoundHandler(getActivity(), "playButton");

        // Set the preview button function
        playBuiltChord.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
//                    if (mainFragment != null)
//                    {
//                        mainFragment.stopAllSound();
//                        mainFragment.blockAllSound(true);
//                    }

                    soundHandler.stopSound();

                    if (mainFragment != null)
                        ChordHandler.buildCurrentChord(mainFragment);

                    soundHandler.playChord(getActivity(), ChordHandler.getCurrentBuiltChordSpelling(),
                            ChordHandler.getCurrentSelectedChord().getNumNotes());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
//                    if (mainFragment != null)
//                        mainFragment.blockAllSound(false);

                    soundHandler.stopSound();
                }
                return false;
            }
        });

        // Set the check button function
        checkChord.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
//                    if (mainFragment != null)
//                        mainFragment.blockAllSound(false);

                    silenceButtons();

                    // Momentarily disable the button
                    v.setEnabled(false);
                    v.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            v.setEnabled(true);
                        }
                    }, 1000L);

                    // Check the result
                    if (mainFragment != null)
                        ChordHandler.checkCurrentChord(mainFragment);
                }
                return false;
            }
        });

        // Return the view
        return view;
    }

    /**
     * Called when the view of this Fragment is destroyed.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        soundHandler.stopSound();
    }

    /**
     * Called when the view state of this Fragment is restored.
     * @param savedInstanceState The Bundle from which to restore the view state
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);

        // Stop sounds
        silenceButtons();
    }

    /**
     * Called when the Fragment is Paused.
     */
    @Override
    public void onPause()
    {
        super.onPause();

        // Stop sounds
        silenceButtons();
    }

    /**
     * Called when the Fragment is resumed.
     */
    @Override
    public void onResume()
    {
        super.onResume();

        // Stop sounds
        silenceButtons();
    }

    /**
     * Sets the MainFragment reference in this CheckFragment.
     * @param mainFragment The new MainFragment reference
     */
    protected void setMainFragment(MainFragment mainFragment)
    {
        this.mainFragment = mainFragment;
    }
}
