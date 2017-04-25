package com.android.priyank.letsplay;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by priyank on 23/2/17.
 */

public class ApplicationCompleteActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "ApplicationComplete";
    private Button mClose, mHome;
    TextView mEmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_complete);

        mClose = (Button) findViewById(R.id.close);
        mHome = (Button) findViewById(R.id.home);
        mEmailAddress = (TextView) findViewById(R.id.email_address_completed);

        Intent intent = getIntent();
        String mEmail = intent.getStringExtra("emailAddress");

        mEmailAddress.setText(mEmail);

        mClose.setOnClickListener(this);
        mHome.setOnClickListener(this);
    }

    public void onClick(View view){

        if(view == mHome){

            Log.i(TAG, "Navigate to Welcome Activity ");

            Intent intent = new Intent(ApplicationCompleteActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }

        if (view == mClose) {
            Log.i(TAG, "Closing Application ");

            finish();
        }
    }
}