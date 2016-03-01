/***************************************************************************************************
 * StartPage.java
 * This class sets up the Start Page and its buttons
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 **/
package com.five_chords.chord_builder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.five_chords.chord_builder.com.five_chords.chord_builder.Contact_Us;

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
    }

    public void backToMain(View view)
    {
        Intent intent = new Intent(this, MainActivity.class );
        startActivity(intent);
    }
    public void toHelp(View view)
    {
        Intent intent = new Intent(this, HelpPage.class );
        startActivity(intent);
    }
    public void toContact_Us(View view)
    {
        Intent intent = new Intent(this, Contact_Us.class );
        startActivity(intent);
    }
}
