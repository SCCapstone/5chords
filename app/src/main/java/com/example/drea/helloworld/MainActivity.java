package com.example.drea.helloworld;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    SoundPool mySound;

    //////
    // major chords
    //
    int[][] chords = {{0,4,7}, {1,5,8}, {2,6,9},          // C, C#, D
                      {3,7,10}, {4,8,11}, {5,9,12},       // Eb, E, F
                      {6,10,13}, {7,11,14}, {8,12,15},    // F#, G, Ab
                      {9,13,16}, {10,14,17}, {11,15,18}}; // A, Bb, B
    ArrayList<Integer> notes;
    int chordRoot, chordThird, chordFifth;
    int[] chord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        notes = new ArrayList<>();

        //////
        // Loop through raw folder for notes
        //
        for (Field f : R.raw.class.getFields()) {
            try {
                if (f.getName().contains("note")) {
                    notes.add(mySound.load(this, f.getInt(null), 0));
                }
            } catch (IllegalAccessException e) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //////
    // pick a random chord
    //
    public void Rando(View v) {
        //////
        // new random int from 0-11
        // or use chords.length() in future
        //
        Random rand = new Random();
        chord = chords[rand.nextInt(12)];

        //////
        // get notes in chord, play notes
        //
        chordRoot = notes.get(chord[0]);
        chordThird = notes.get(chord[1]);
        chordFifth = notes.get(chord[2]);

        mySound.play(chordRoot,1,1,1,0,1);
        mySound.play(chordThird,1,1,1,0,1);
        mySound.play(chordFifth,1,1,1,0,1);
    }

    //////
    // check chord built against random chord
    //
    public void check(View v) {
        //////
        // get the seek bars
        //
        SeekBar root = (SeekBar) findViewById(R.id.root);
        SeekBar third = (SeekBar) findViewById(R.id.third);
        SeekBar fifth = (SeekBar) findViewById(R.id.fifth);

        //////
        // get the note of each seek bar
        //
        int thisRoot = notes.get(root.getProgress());
        int thisThird = notes.get(third.getProgress());
        int thisFifth = notes.get(fifth.getProgress());

        //////
        // play the notes
        //
        mySound.play(thisRoot,1,1,1,0,1);
        mySound.play(thisThird,1,1,1,0,1);
        mySound.play(thisFifth,1,1,1,0,1);

        //////
        // get the labels
        // show correct answer on one label
        // show if the chords match on the other
        //
        TextView chordLabel = (TextView) findViewById(R.id.chord);
        TextView answerLabel = (TextView) findViewById(R.id.answer);

        chordLabel.setText(Integer.toString(chord[0]) + Integer.toString(chord[1]) + Integer.toString(chord[2]));
        if ((thisRoot == chordRoot) && (thisThird == chordThird) && (thisFifth == chordFifth)) {
            answerLabel.setText("jes");
        } else {
            answerLabel.setText("no");
        }

    }

    //////
    // replay the chord
    //
    public void chordAgain(View v) {
        mySound.play(chordRoot,1,1,1,0,1);
        mySound.play(chordThird,1,1,1,0,1);
        mySound.play(chordFifth,1,1,1,0,1);
    }
    public void ToHelpPage( View v){
        Intent intent = new Intent(this, HelpPage.class );
        startActivity(intent);
    }

}