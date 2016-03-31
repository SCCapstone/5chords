package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.five_chords.chord_builder.R;


/**
 * The Help page Activity.
 * @date 31 March 2016
 * @author Drea,Steven,Zach,Kevin,Bo
 */
public class HelpPage extends AppCompatActivity
{
    /**
     * Called when this Activity is created.
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);

        // Set up Buttons
        Button button = (Button) findViewById(R.id.contact_dev);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"5chordscontact@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_CC, new String[]{""});

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Of Matter");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");

                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Choose your email client:"));
            }
        });

        button = (Button) findViewById(R.id.contact_pro);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Prohelpcontact123@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_CC, new String[]{""});

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Of Matter");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");

                emailIntent.setType("message/rfc822");
                //standard ARPA starting
                startActivity(Intent.createChooser(emailIntent, "Email us with your preferential source"));

            }
        });
    }

    /**
     * Called to return to the MainActivity.
     * @param view The calling View
     */
    public void backToMain(View view)
    {
        finish();
    }
}
