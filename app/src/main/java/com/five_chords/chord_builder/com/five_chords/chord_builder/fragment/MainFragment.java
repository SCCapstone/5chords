package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.five_chords.chord_builder.ChordHandler;
import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.SliderHintView;

/**
 * Fragment containing the main GUI components of the application, including the chord sliders, the chord
 * select spinner, and the buttons for playing, previewing, getting random chords, and checking chords.
 * @date 31 March 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class MainFragment extends Fragment implements ChordHandler.OnChordSelectedListener, View.OnClickListener
{
    /** The main content View of this Fragment. */
    private View view;

    /** The Fragment containing the chord build sliders attached to this Activity. */
    private SliderFragment sliderFragment;

    /** The Fragment containing the chord check button. */
    private CheckFragment checkFragment;

    /** The Fragment containing the chord selection interface. */
    private ChordSelectFragment chordSelectFragment;

    /** Thread to use for chord playback when the user guesses incorrectly. */
    private Thread playbackThread;

    /** The MediaPlayer to use for playing the correct sound. */
    private MediaPlayer correctSoundPlayer;

    /** The MediaPlayer to use for playing the wrong sound. */
    private MediaPlayer wrongSoundPlayer;

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
        Log.w("MAIN_FRAGMENT", "onCreateView");

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        // Create Fragments
        chordSelectFragment = new ChordSelectFragment();
        sliderFragment = new SliderFragment();
        checkFragment = new CheckFragment();

        // Set references
        chordSelectFragment.setArguments(getArguments());
        sliderFragment.setArguments(getArguments());
        checkFragment.setMainFragment(this);

        // Add Fragments to layout
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_chord_select, chordSelectFragment)
                .add(R.id.fragment_sliders, sliderFragment)
                .add(R.id.fragment_chord_check, checkFragment).commit();

        // Create the sound player
        correctSoundPlayer = MediaPlayer.create(getActivity(), R.raw.correct);
        wrongSoundPlayer = MediaPlayer.create(getActivity(), R.raw.wrong);

        // Add listener to main layout to use for stopping the playback sequence
        view.setOnClickListener(this);

        // Add Listener to ChordHandler
        ChordHandler.setOnChordSelectedListener(this);

        return view;
    }

    /**
     * Called when this Fragment's view is destroyed.
     */
    @Override
    public void onDestroyView()
    {
        // Set Fragments to null
        checkFragment = null;
        sliderFragment = null;
        chordSelectFragment = null;

        // Clean up sound players
        correctSoundPlayer.release();
        wrongSoundPlayer.release();

        // Remove listener from ChordHandler
        ChordHandler.setOnChordSelectedListener(null);

        // Remove listener from main layout uses for stopping the playback sequence
        view.setOnClickListener(null);

        super.onDestroyView();
    }

    /**
     * Called when this Fragment is paused.
     */
    @Override
    public void onPause()
    {
        super.onPause();

        // Destroy Fragments
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (checkFragment != null)
        {
            checkFragment.silenceButtons();
            transaction.detach(checkFragment);
            transaction.remove(checkFragment);
        }

        if (sliderFragment != null)
        {
            sliderFragment.silenceSliders();
            transaction.detach(sliderFragment);
            transaction.remove(sliderFragment);
        }

        if (chordSelectFragment != null)
        {
            chordSelectFragment.silenceButtons();
            transaction.detach(chordSelectFragment);
            transaction.remove(chordSelectFragment);
        }

        try
        {
            transaction.commit();
        }
        catch (Exception e)
        {/* Ignore */}

        // Stop the playback thread if needed
        if (playbackThread != null && playbackThread.isAlive())
            playbackThread.interrupt();
    }

    /**
     * Called when this Fragment is resumed.
     */
    @Override
    public void onResume()
    {
        super.onResume();

        if (sliderFragment != null)
            sliderFragment.loadSliderPositions();
    }

    /**
     * Gets the SliderFragment within this MainFragment.
     * @return The SliderFragment within this MainFragment
     */
    public SliderFragment getSliderFragment() {
        return sliderFragment;
    }

    /**
     * Gets the SliderFragment within this CheckFragment.
     * @return The SliderFragment within this CheckFragment
     */
    public CheckFragment getCheckFragment()
    {
        return checkFragment;
    }

    /**
     * Gets the ChordSelectFragment within this CheckFragment.
     * @return The ChordSelectFragment within this CheckFragment
     */
    public ChordSelectFragment getChordSelectFragment()
    {
        return chordSelectFragment;
    }

    /**
     * Called to update the displayed score.
     */
    public void updateDisplayedScore()
    {
        // Get the TextView
        TextView currentProgress = (TextView) view.findViewById(R.id.textview_score_display);

        // Get the current Score
        Score current = Score.getCurrentScore();

        // Set the Views
        if (currentProgress != null)
            currentProgress.setText(current.getCurrentValue().getDisplayString());
    }

    /**
     * Shows the chord sequence.
     */
    public void showChordSequence()
    {
        // Stop the playback thread if needed
        if (playbackThread != null && playbackThread.isAlive())
            playbackThread.interrupt();

        playbackThread = new PlaybackSequence();
        playbackThread.start();
    }

    /**
     * Shows the chord check result dialog.
     */
    public void showChordCheckResult()
    {
        // Make sure the current Chord is built
        ChordHandler.buildCurrentChord(this);

        // Test correctness
        boolean isCorrect = ChordHandler.testCurrentChords();

        // Set the score
        Score.getCurrentScore().update(getActivity(), isCorrect);
        updateDisplayedScore();

        // Handle result
        if (isCorrect)
        {
            // Play sound
            correctSoundPlayer.start();

            // Launch dialog
            AlertFragment alert = AlertFragment.newInstance(R.string.thats_correct, R.string.correct_dialog_message);

            alert.setNoAction(new Runnable()
            {
                @Override
                public void run()
                {
                    ChordHandler.resetCurrentWrongStreak();
                    getSliderFragment().resetChordSliders();
                }
            });
            alert.setYesAction(new Runnable()
            {
                @Override
                public void run()
                {
                    stopAllSound();
                    ChordHandler.getRandomChord();
                    getSliderFragment().resetChordSliders();
                }
            });

            alert.setDismissAction(new Runnable()
            {
                @Override
                public void run()
                {
                    stopAllSound();
                    getSliderFragment().resetChordSliders();
                }
            });

            alert.show(getFragmentManager(), "alert");
        }
        else
        {
            // Play sound
            wrongSoundPlayer.start();

            // Show hints if needed
            ChordHandler.makeHints(this);

            // Show toast
            Toast toast = Toast.makeText(getActivity(), getString(R.string.thats_incorrect), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * Generates one instance of the given type of hint.
     *
     * @param type The Hint type
     */
    public void makeHint(final byte type)
    {
        sliderFragment.makeHint(type);
//        // Calculate the chord differences
//        final Note[] builtChord = ChordHandler.getCurrentBuiltChordSpelling();
//        final Note[] selectedChord = ChordHandler.getCurrentSelectedChordSpelling();
//
//        // Add hints
//        SliderHintView view;
//
//        // Root slider
//        view = (SliderHintView) this.view.findViewById(R.id.slider_root_layout);
//        view.setHint(type, builtChord[0], selectedChord[0], sliderFragment, 500L);
//
//        // Third slider
//        view = (SliderHintView) this.view.findViewById(R.id.slider_third_layout);
//        view.setHint(type, builtChord[1], selectedChord[1], sliderFragment, 500L);
//
//        // Fifth slider
//        view = (SliderHintView) this.view.findViewById(R.id.slider_fifth_layout);
//        view.setHint(type, builtChord[2], selectedChord[2], sliderFragment, 500L);
//
//        // Option slider
//        if (ChordHandler.getCurrentSelectedChord().TYPE.offsets.length == 4)
//        {
//            view = (SliderHintView) this.view.findViewById(R.id.slider_option_layout);
//            view.setHint(type, builtChord[3], selectedChord[3], sliderFragment, 500L);
//        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v)
    {
        if (playbackThread != null && !playbackThread.isInterrupted())
            playbackThread.interrupt();
    }

    /**
     * Called when a new chord is selected.
     */
    @Override
    public void onChordSelected(boolean random)
    {
        // Update current chord score
        updateDisplayedScore();

        // Update slider bounds
        sliderFragment.setSliderBoundsToFitChord(ChordHandler.getCurrentSelectedChordSpelling());

        // Update ChordInstrumentSelectFragment
        chordSelectFragment.setDisplayedChord(ChordHandler.getCurrentSelectedChord(), random);

        // Hide fourth slider if needed
        try
        {
            sliderFragment.showFourthSlider(ChordHandler.getCurrentSelectedChord().getNumNotes() == 4);
        }
        catch (Exception e)
        {/* Ignore */}
    }

    /**
     * Called to stop all playback
     */
    public void stopAllSound()
    {
        sliderFragment.silenceSliders();
        checkFragment.silenceButtons();
        chordSelectFragment.silenceButtons();
    }

//    /**
//     * Send signals to block playbask
//     * @param blocked is the sound blocked (true) or allowed (false)?
//     */
//    public void blockAllSound(boolean blocked) {
//        sliderFragment.setIsBlocked(blocked);
//    }

    /**
     * A Thread to handle the playback sequence.
     */
    private class PlaybackSequence extends Thread
    {
        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run()
        {
            // Sleep
            try {Thread.sleep(50L);} catch (InterruptedException e)
            {
                onEnd();
                return;
            }

            // Play Built chord
            checkFragment.playBuiltChord(true);

            // Sleep
            try {Thread.sleep(1000L);} catch (InterruptedException e)
            {
                checkFragment.playBuiltChord(false);
                onEnd();
                return;
            }

            // Stop Built chord
            checkFragment.playBuiltChord(false);

            // Sleep
            try {Thread.sleep(250L);} catch (InterruptedException e)
            {
                onEnd();
                return;
            }

            // Play Correct chord
            chordSelectFragment.playSelectedChord(true);

            // Sleep
            try {Thread.sleep(1000L);} catch (InterruptedException e)
            {
                chordSelectFragment.playSelectedChord(false);
                onEnd();
                return;
            }

            // Stop Correct chord
            chordSelectFragment.playSelectedChord(false);

            onEnd();
        }

        /**
         * Called when this PlaybackSequence ends.
         */
        public void onEnd()
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    showChordCheckResult();
                }
            });
        }
    }
}