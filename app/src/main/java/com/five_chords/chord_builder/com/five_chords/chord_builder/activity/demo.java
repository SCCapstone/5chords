package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.five_chords.chord_builder.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * The startup demo Activity.
 * @date 4 April 2016
 * @author Drea, Steven, Zach, Kevin, Bo
 */
public class demo extends AppCompatActivity implements View.OnClickListener
{
    /** The View containing the tutorial gui. */
    private ShowcaseView showcaseView;

    /** Keeps track of the current tutorial frame. */
    private int count = 0;

    /** The targets to focus on for each tutorial frame. */
    private Target t0, t1, t2, t3, t4, t5, t6, t7;

    /**
     * Activity Create and checks first launch
     * If first launch, go to Tutorial, otherwise
     * Goes straight to the MainActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        // Get the focus points
        t0 = new ViewTarget(R.id.button, this);
        t1 = new ViewTarget(R.id.button2, this);
        t2 = new ViewTarget(R.id.button3, this);
        t3 = new ViewTarget(R.id.button4, this);
        t4 = new ViewTarget(R.id.button5, this);
        t5 = new ViewTarget(R.id.button6, this);
        t6 = new ViewTarget(R.id.button7, this);
        t7 = new ViewTarget(R.id.button8, this);

        // Create the ShowcaseView
        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(Target.NONE)
                .setOnClickListener(this)
                .setContentTitle("Tutorial!")
                .setContentText("Welcome to Chord Builder! We will walk you through a quick demo to get you used to this tool!")
                .build();
        showcaseView.setButtonText("Next");

    }

    /**
     * Called when a View has been clicked.
     * @param v The clicked View
     */
    @Override
    public void onClick(View v){
        switch(count){
            case 0:
                showcaseView.setShowcase(t0,true);
                showcaseView.setContentTitle("Play");
                showcaseView.setContentText("Hold down the Play button to hear the selected chord");
                break;
            case 1:
                showcaseView.setShowcase(t1,true);
                showcaseView.setContentTitle("Chord Select");
                showcaseView.setContentText("Select the Chord that are Available to you here, and see your level next to it as well.");
                break;
            case 2:
                showcaseView.setShowcase(t2,true);
                showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
                showcaseView.setContentTitle("Menu");
                showcaseView.setContentText("Swipe to the Right here to gain access to more options and more information about the Chord Builder. You can also check your history and adjust the difficulty in the menu.");
                break;
            case 3:
                showcaseView.setShowcase(t3,true);
                showcaseView.setContentTitle("Slider");
                showcaseView.setContentText("Hold and drag the Slider button to change the notes of the chord you are creating. You can also hold the slider down to listen to the note selected.");
                break;
            case 4:
                showcaseView.setShowcase(t5,true);
                showcaseView.setContentTitle("Preview");
                showcaseView.setContentText("Hold the Preview Button to hear the chord you have currently selected with the sliders.");
                break;
            case 5:
                showcaseView.setShowcase(t4,true);
                showcaseView.setContentTitle("Check Your Work!");
                showcaseView.setContentText("Once the selected and the desired chords sound the same. Check your work by clicking this button here! ");
                break;
            case 6:
                showcaseView.setShowcase(t6,true);
                showcaseView.setContentTitle("Randomize It!");
                showcaseView.setContentText("When you think you're ready for the challenge, press the Shuffle button for a random chord for you to try!");
                break;
            case 7:
                showcaseView.setShowcase(t7,true);
                showcaseView.setContentTitle("Enjoy!");
                showcaseView.setContentText("Thank you for trying out our application and feel free to contact us if you have any questions or comments. Thank you!");
                break;

            case 8:
                finish();
        }
        count++;
    }
}
