/*************************************************************************************************
 * HelpPage.java
 * This class sets up the Help Page and its buttons
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpPage extends AppCompatActivity
{
    @Override
    /*************************************************************
     * Create Activity to have help page for users
     *
     */
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
                //standard ARPA startin
                startActivity(Intent.createChooser(emailIntent, "Choose your email client:"));
                //startActivityForResult(emailIntent,"Send" );
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
     * Goes back to mainActivity on Call
     * @ param Button Call
     * MainActivity Call
     */
    public void backToMain(View view)
    {
        finish();
    }
}
