/*************************************************************************************************
 * demo.java
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.five_chords.chord_builder.R;

public class demo extends AppCompatActivity
{
    @Override
    /**
     * Activity Create and checks first launch
     * If first launch, go to Tutorial, otherwise
     * Goes straight to the MainActivity
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        final ImageView imageView = (ImageView)findViewById(R.id.imageView);

        Button next_button = (Button)findViewById(R.id.next_button);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imageView.setImageResource(R.drawable.test2);
                v.setEnabled(false);
            }
        });
    }
    /**
     * Goes to mainActivity on Call
     * @ param  Button Call
     *  Initiates MainActivity
     */
    public void skip_button(View view)
    {
        finish();
    }
}
