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

public class HelpPage extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);
    }

    public void BackToMain(View view)
    {
        Intent intent = new Intent(this, MainActivity.class );
        startActivity(intent);
    }

    public void goToAboutPage(View view)
    {
        Intent intent = new Intent(this, AboutPage.class);
        startActivity(intent);
    }

    public void goToScores(View view)
    {
        Intent intent = new Intent(this, Score.ScoreActivity.class);
        startActivity(intent);
    }

    public void BackToHelp(View view)
    {
        Intent intent = new Intent(this, Score.ScoreActivity.class);
        startActivity(intent);
    }

}
