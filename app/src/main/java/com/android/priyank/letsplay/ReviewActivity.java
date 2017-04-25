package com.android.priyank.letsplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by Priyank on 7/4/2016.
 */

public class ReviewActivity extends Activity {

    TextView mEmail, mFirstName, mLastName, mAddress1, mContactNumber, mEmergencyNumber,
            mRadioGender, mGamesSelected, mCheckboxValue, mAgeGroup, mReviewPageTitle, mTermsTitle;

    RadioButton rGenderFemale, rGenderMale;
    ImageView mProfile, mCertificate;

    Bitmap bitmap, bitmap1;

    Button mSubmit;

    private Firebase mFirebaseRef;
    FirebaseStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Connect to Firebase
        mFirebaseRef = new Firebase("https://letsplay-6cc97.firebaseio.com/");
        Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Fix Orientation to Portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_review);

        //Text Views
        mReviewPageTitle = (TextView) findViewById(R.id.reviewTitle);
        mEmail = (TextView) findViewById(R.id.etitle);
        mFirstName = (TextView) findViewById(R.id.fname);
        mLastName = (TextView) findViewById(R.id.lname);
        mAddress1 = (TextView) findViewById(R.id.address1);
        mContactNumber = (TextView) findViewById(R.id.number_value);
        mEmergencyNumber = (TextView) findViewById(R.id.number_value1);

        mProfile = (ImageView) findViewById(R.id.profile);
        mCertificate = (ImageView) findViewById(R.id.certificate);

        mRadioGender = (TextView) findViewById(R.id.radioGender);
        rGenderFemale = (RadioButton) findViewById(R.id.genderFemale);
        rGenderMale = (RadioButton) findViewById(R.id.genderMale);

        mGamesSelected = (TextView) findViewById(R.id.gamesSelected);

        mCheckboxValue = (TextView) findViewById(R.id.checkboxValue);
        mAgeGroup = (TextView) findViewById(R.id.SelectedAgeGroup);

        Intent intent = getIntent();

        //Retrieving text Strings
        String sEmailAddress = intent.getStringExtra("emailAddress");
        String sFirstName = intent.getStringExtra("firstName");
        String sLastName = intent.getStringExtra("lastName");
        String sHomeAddress = intent.getStringExtra("homeAddress1");
        String sHomeAddress1 = intent.getStringExtra("homeAddress2");
        String sContactNumber = intent.getStringExtra("contactNumber");
        String sEmergencyNumber = intent.getStringExtra("emergencyNumber");
        String sRadioGender = intent.getStringExtra("radioGender");
        String sCheckBox = intent.getStringExtra("checkboxValue");
        String sAgeGroup = intent.getStringExtra("ageGroup");

        Bundle extras = getIntent().getExtras();

        //Profile Image
        byte[] byteArray = extras.getByteArray("profilePicture");
        assert byteArray != null;

        //Certificate Image
        byte[] byteArray1 = extras.getByteArray("certificatePicture");
        assert byteArray1 != null;

        //Setting text
        mEmail.setText(sEmailAddress.trim());
        mFirstName.setText(sFirstName.trim());
        mLastName.setText(sLastName.trim());
        mAddress1.setText(sHomeAddress.trim() + ", " + sHomeAddress1.trim());
        mContactNumber.setText(sContactNumber.trim());
        mEmergencyNumber.setText(sEmergencyNumber.trim());
        mRadioGender.setText(sRadioGender.trim());

        mCheckboxValue.setText(sCheckBox);
        mAgeGroup.setText(sAgeGroup);

        //Fonts of TextViews
        Typeface mCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Monts.ttf");

        mEmail.setTypeface(mCustomFont);
        mFirstName.setTypeface(mCustomFont);
        mLastName.setTypeface(mCustomFont);
        mAddress1.setTypeface(mCustomFont);
        mContactNumber.setTypeface(mCustomFont);
        mEmergencyNumber.setTypeface(mCustomFont);
        mRadioGender.setTypeface(mCustomFont);
        mCheckboxValue.setTypeface(mCustomFont);

        Typeface mCustomFont1 = Typeface.createFromAsset(getAssets(), "fonts/GoodDog.otf");
        mReviewPageTitle.setTypeface(mCustomFont1);
        mReviewPageTitle.setTextSize(45);

        mEmail.setTextSize(25);
        mFirstName.setTextSize(25);
        mLastName.setTextSize(25);
        mAddress1.setTextSize(25);
        mContactNumber.setTextSize(25);
        mEmergencyNumber.setTextSize(25);
        mRadioGender.setTextSize(25);
        mCheckboxValue.setTextSize(20);

        //Set Profile Image
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mProfile.setImageBitmap(bitmap);

        //Set Certificate Image
        bitmap1 = BitmapFactory.decodeByteArray(byteArray1, 0, byteArray1.length);
        mCertificate.setImageBitmap(bitmap1);

        //Submit Button onClick
        mSubmit = (Button) findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFirebaseData();
            }
        });

        mTermsTitle = (TextView) findViewById(R.id.agree_to_terms_and_policies);

        mTermsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ReviewActivity.this, R.style.AlertDialogCustom));
                builder.setTitle(getString(R.string.alert_dialog_title)+ "\n");
                builder.setIcon(R.mipmap.terms);
                //Terms and Policies of the Tournament
                builder.setMessage(
                        "\n1. Application Forms should be accompanied by Requisite Commitment Charges, Birth Certificate and Photograph.\n"
                                + "\n2. Acceptance of forms after due verification will be at sole discretion of the screening committee.\n"
                                + "\n3. Decision of umpires will be final and should be respected. No arguments will be entertained.\n"
                                + "\n4. Forms will not be accepted beyond date stated as above\n"
                                + "\n5. We believe in SPORTSMEN SPIRIT and all participants should demonstrate the same.\n"
                                + "\n6. Games will be played as per schedule and all participants should be at the venue on time. Delay will result in loss of participation.\n"
                                + "\n7. Decisions of Organising Committee will be final for all issues relating to event.\n \n");

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                    }
                });

                AlertDialog dialog = builder.show();

                TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.FILL_HORIZONTAL | Gravity.AXIS_PULL_BEFORE);
                messageView.setTextSize(18);
            }
        });
    }

    //Store Data in Firebase Database
    public void mFirebaseData(){

        String sEmail = mEmail.getText().toString().trim();
        String sFirstName = mFirstName.getText().toString().trim();
        String sLastName = mLastName.getText().toString().trim();
        String sAddress1 = mAddress1.getText().toString().trim();
        String sContactNumber = mContactNumber.getText().toString().trim();
        String sEmergencyNumber = mEmergencyNumber.getText().toString().trim();
        String sGender = mRadioGender.getText().toString().trim();
        String sAgeGroup = mAgeGroup.getText().toString().trim();
        String sSelectedGames = mCheckboxValue.getText().toString().trim();
        //String strLastFourDi = phoneNumber.length() >= 4 ? phoneNumber.substring(phoneNumber.length() - 4): "";
        String mKey = mFirebaseRef.child(sAgeGroup).push().getKey().trim();

        Firebase participants = mFirebaseRef.child(sAgeGroup);

        Map<String, Object> participant = new HashMap<>();

        participant.put("Email ", sEmail);
        participant.put("FirstName ", sFirstName);
        participant.put("LastName", sLastName);
        participant.put("HomeAddress ", sAddress1);
        participant.put("ContactNumber ", sContactNumber);
        participant.put("EmergencyNumber ", sEmergencyNumber);
        participant.put("Gender ", sGender);
        participant.put("Games ", sSelectedGames);
        participant.put("AgeGroup ", sAgeGroup);
        participant.put("FullName", sFirstName + " " + sLastName);
        participant.put("Key ", mKey);

        Bundle extras = getIntent().getExtras();

        //Profile Image
        byte[] byteArray = extras.getByteArray("profilePicture");
        assert byteArray != null;

        //Firebase Profile Image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] bytes = baos.toByteArray();

        //Instance of Firebase Storage
        mStorage = FirebaseStorage.getInstance();
        Log.e(TAG, "Firebase Storage " + mStorage);

        // Create a storage reference from our app
        StorageReference mStorageRef = mStorage.getReferenceFromUrl("gs://letsplay-6cc97.appspot.com");
        Log.e(TAG, "Storage Reference " + mStorageRef);

        StorageReference mProfileRef = mStorageRef.child(sFirstName + " " + sLastName + " (" + sContactNumber + ")").child("profile.jpg");
        Log.e(TAG, "Profile Reference " + mProfileRef);

        StorageReference mProfileImagesRef = mStorageRef.child("images/profile.jpg");
        Log.e(TAG, "Profile Image Reference " + mProfileImagesRef);

        mProfileRef.getName().equals(mProfileImagesRef.getName());    // true
        mProfileRef.getPath().equals(mProfileImagesRef.getPath());    // false

        Log.e(TAG, "Profile Image Name " +
                mProfileRef.getName().equals(mProfileImagesRef.getName()));

        Log.e(TAG, "Profile Image Path " +
                mProfileRef.getName().equals(mProfileImagesRef.getPath()));

        //Check if Profile Image is Valid before uploading to firebase
        if (bitmap != null) {

            //Upload Profile Image to FireBase Storage
            UploadTask uploadTask = mProfileRef.putBytes(bytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Log.e(TAG, "Download URL " + downloadUrl);
                }
            });

        } else {
            Toast.makeText(getBaseContext(), "Please Add a valid Profile Picture",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //Firebase Certificate Image
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);

        byte[] bytes1 = baos1.toByteArray();

        StorageReference mCertificateRef = mStorageRef.child(sFirstName + " " + sLastName +
                " (" + sContactNumber + ")").child("Certificate.jpg");

        Log.e(TAG, "Certificate Reference " + mCertificateRef);

        StorageReference mCertificateImagesRef = mStorageRef.child("images/Certificate.jpg");
        Log.e(TAG, "Certificate Image Reference " + mCertificateImagesRef);

        mCertificateRef.getName().equals(mCertificateImagesRef.getName());    // true
        mCertificateRef.getPath().equals(mCertificateImagesRef.getPath());    // false

        Log.e(TAG, "Certificate Image Name " +
                mCertificateRef.getName().equals(mCertificateImagesRef.getName()));

        Log.e(TAG, "Profile Image Path " +
                mCertificateRef.getName().equals(mCertificateImagesRef.getPath()));

        //Check if Certificate Image is Valid before uploading to firebase
        if (bitmap1 != null) {

            //Upload Certificate Image to FireBase Storage
            UploadTask uploadTask1 = mCertificateRef.putBytes(bytes1);
            uploadTask1.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl1 = taskSnapshot.getDownloadUrl();

                    Log.e(TAG, "Certificate Download URL " + downloadUrl1);
                }
            });

            //AgeGroup>Key>[First Name + LastName + (Contact Number)]>Detailed Value
            participants.child(sFirstName + " " + sLastName).setValue(participant);

        } else {
            Toast.makeText(getBaseContext(), "Please add a valid Birth Certificate Image",
                    Toast.LENGTH_LONG).show();
            return;
        }

        closeApplication();
    }

    private void closeApplication() {
        //Log.i("APP", "Closing Application ");

        Intent intent = getIntent();
        final String mEmailAddress = intent.getStringExtra("emailAddress");
        Toast.makeText(getBaseContext(), "Submitting your Application Form", Toast.LENGTH_SHORT).show();

        //2 Seconds delay before closing Activity
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                Intent intent = new Intent(ReviewActivity.this, ApplicationCompleteActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("emailAddress", mEmailAddress);
                startActivity(intent);

            }
        }, 2000);
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }
}
