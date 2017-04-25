package com.android.priyank.letsplay;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class WelcomeActivity extends Activity {

    TextView mHeading, mAgeSelection, mVenueOnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fix Orientation to Portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_welcome);

        final Button age6 = (Button)findViewById(R.id.age_button1);
        final Button age12 = (Button)findViewById(R.id.age_button2);
        final Button age16 = (Button)findViewById(R.id.age_button3);
        final Button age40 = (Button)findViewById(R.id.age_button4);

        //Button Fonts
        Typeface mCustomFont2 = Typeface.createFromAsset(getAssets(), "fonts/Pamela.ttf");

        age6.setTypeface(mCustomFont2);
        age12.setTypeface(mCustomFont2);
        age16.setTypeface(mCustomFont2);
        age40.setTypeface(mCustomFont2);

        //Tournament Venue on map location
        mVenueOnMap = (TextView) findViewById(R.id.tournamentVenue);

        mVenueOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = 23.035118;
                double longitude = 72.563395;
                String label = "K. J. Chhatralaya";
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=20";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //Title Fonts
        mHeading = (TextView) findViewById(R.id.welcome_title);
        Typeface mCustomFont = Typeface.createFromAsset(getAssets(), "fonts/FFF.ttf");
        mHeading.setTypeface(mCustomFont);

        mAgeSelection = (TextView) findViewById(R.id.age_selection);
        Typeface mCustomFont1 = Typeface.createFromAsset(getAssets(), "fonts/GoodDog.otf");
        mAgeSelection.setTypeface(mCustomFont1);

        //Age Group Button onClick
        //Layout1
        age6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent a = new Intent(view.getContext(),ApplicationActivity.class);
                a.putExtra("layout", R.layout.activity_application_a);
                startActivity(a);
            }
        });

        //Layout2
        age12.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent b = new Intent(view.getContext(),ApplicationActivity.class);
                b.putExtra("layout1", R.layout.activity_application_b);
                startActivity(b);
            }
        });

        //Layout3
        age16.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent c = new Intent(view.getContext(),ApplicationActivity.class);
                c.putExtra("layout2", R.layout.activity_application_c);
                startActivity(c);
            }
        });

        //Layout4
        age40.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent d = new Intent(view.getContext(),ApplicationActivity.class);
                d.putExtra("layout3", R.layout.activity_application_d);
                startActivity(d);
            }
        });
    }

    //Override Single back press
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}

