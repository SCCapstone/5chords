/*************************************************************************************************
 * Contact_Us.java
 * This class sets up the About Us Page and its buttons
 * @version 1.0
 * @date 01 March 2016
 * @author: Drea,Zach,Kevin,Theo
 */
package com.five_chords.chord_builder.com.five_chords.chord_builder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.five_chords.chord_builder.R;

public class Contact_Us extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__us);

        Button btn = (Button) findViewById(R.id.contact_dev);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"5chordscontact@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_CC, new String[]{""});

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Of Matter");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");

                emailIntent.setType("message/rfc822");
                //standard ARPA starting
                startActivity(Intent.createChooser(emailIntent, "Email us with your preferential source"));

            }
        });


        Button button = (Button) findViewById(R.id.contact_pro);
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_us_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
