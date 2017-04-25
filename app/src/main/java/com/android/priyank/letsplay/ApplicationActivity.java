package com.android.priyank.letsplay;

/**
 * Created by Priyank on 7/4/2016.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.android.priyank.letsplay.R.drawable.ic_documents;
import static com.android.priyank.letsplay.R.drawable.ic_profile_image;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class ApplicationActivity extends Activity {

    private static final String TAG = "ApplicationActivity";

    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    private static final int CAMERA_REQUEST_1 = 3;
    private static final int GALLERY_REQUEST_1 = 4;

    private CameraManager mCameraManager;
    private PackageManager mPackageManager;
    private RadioGroup mRadioGenderGroup;

    //Rect holds four integer coordinates for a rectangle. The rectangle is
    //represented by the coordinates of its 4 edges (left, top, right bottom).
    private Rect mRect = new Rect();

    //ImageViews
    private ImageView mProfileImage, mCertificateImage;
    private byte[] byteArray, byteArray1;

    String selectedType = "";

    //Detail of Participant editText
    private EditText mFirstName, mEmailAddress, mLastName, mHomeAddress,
            mHomeAddress1, mContactNumber, mEmergencyNumber;

    //Game Selection CheckBoxes
    private CheckBox mCheckboxGame1, mCheckboxGame2, mCheckboxGame3,
            mCheckboxGame4, mTermsConditions;

    private RadioButton mFemaleGender, mMaleGender; //Gender Radio Buttons

    private TextView mContactNumberCounter, mEmergencyNumberCounter; //Counter TextView

    //Title TextView
    TextView mApplicationFormTitle, mUploadProfileTitle,
            mUploadCertificateTitle, mSelectGamesTitle, mGenderTitle, mTermsTitle, mAgeGroup;

    private TextView mAutoFill;
    private String sPossibleEmail = "";

    //Buttons
    Button mClear, mReview;
    Bitmap profileBitmap, certificateBitmap;
    Firebase mFirebaseRef;

    ImagePicker mImagePicker; //ImagePicker class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase Instance
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase("https://leysplayproject.firebaseio.com/");

        ApplicationActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Age Group Layout selections
        Bundle parameters = getIntent().getExtras();

        if (parameters != null && parameters.containsKey("layout")) {
            setContentView(parameters.getInt("layout"));
        }

        else if (parameters != null && parameters.containsKey("layout1")) {
            setContentView(parameters.getInt("layout1"));
        }

        else if (parameters != null && parameters.containsKey("layout2")) {
            setContentView(parameters.getInt("layout2"));
        }

        else if (parameters != null && parameters.containsKey("layout3")) {
            setContentView(parameters.getInt("layout3"));
        }

        mImagePicker = new ImagePicker();

        mCameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        assertNotNull("Can't connect to camera manager", mCameraManager);
        mPackageManager = getPackageManager();
        assertNotNull("Can't get package manager", mPackageManager);

        //Profile Picture Camera or Gallery selection
        mProfileImage = (ImageView) findViewById(R.id.profilePicture);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileImageSelection();
            }
        });

        //Certificate Image Camera or Gallery selection
        mCertificateImage = (ImageView) findViewById(R.id.certificateImage);
        mCertificateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCertificateImageSelection();
            }
        });

        //First Name and Last Name
        mFirstName = (EditText) findViewById(R.id.firstName);
        String sFirstName = mFirstName.getText().toString().trim();
        mFirstName.setText(sFirstName);

        mLastName = (EditText) findViewById(R.id.lastName);
        String sLastName = mLastName.getText().toString().trim();
        mLastName.setText(sLastName);

        //Email Address
        mEmailAddress = (EditText) findViewById(R.id.emailTitle);
        String sEmail = mEmailAddress.getText().toString().trim();
        mEmailAddress.setText(sEmail);

        //Home Address
        mHomeAddress = (EditText) findViewById(R.id.homeAddress1);
        String sAddress1 = mHomeAddress.getText().toString().trim();
        mHomeAddress.setText(sAddress1);

        mHomeAddress1 = (EditText) findViewById(R.id.homeAddress2);
        String sAddress2 = mHomeAddress1.getText().toString().trim();
        mHomeAddress1.setText(sAddress2);

        //Phone Number
        mContactNumber = (EditText) findViewById(R.id.contactNumber);
        String sContactNumber = mContactNumber.getText().toString().trim();
        mContactNumber.setText(sContactNumber);

        //Contact Number Counter
        mContactNumberCounter = (TextView) findViewById(R.id.contact_counter);
        mContactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // this will show characters remaining
                mContactNumberCounter.setText(10 - s.toString().length() + "");
            }
        });

        //Emergency Contact Number
        mEmergencyNumber = (EditText) findViewById(R.id.emergencyContactNumber);
        String sEmergencyNumber = mEmergencyNumber.getText().toString().trim();
        mEmergencyNumber.setText(sEmergencyNumber);

        //Emergency Number Counter
        mEmergencyNumberCounter = (TextView) findViewById(R.id.emergency_counter);
        mEmergencyNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // this will show characters remaining
                mEmergencyNumberCounter.setText(10 - s.toString().length() + "");
            }
        });

        //Game selection Checkboxes
        mCheckboxGame1 = (CheckBox) findViewById(R.id.game1);
        mCheckboxGame2 = (CheckBox) findViewById(R.id.game2);
        mCheckboxGame3 = (CheckBox) findViewById(R.id.game3);
        mCheckboxGame4 = (CheckBox) findViewById(R.id.game4);

        //Selected Age Group Title
        mAgeGroup = (TextView) findViewById(R.id.age_group_selected);
        final String sAgeGroup = mAgeGroup.getText().toString().trim();

        //Terms and Condition Check box
        mTermsConditions = (CheckBox) findViewById(R.id.TermsConditions);

        //Gender RadioButtons
        mFemaleGender = (RadioButton) findViewById(R.id.genderFemale);
        mMaleGender = (RadioButton) findViewById(R.id.genderMale);

        //Gender Selection
        mRadioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);
        mRadioGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.genderFemale)
                    selectedType = mFemaleGender.getText().toString();
                else if (checkedId == R.id.genderMale)
                    selectedType = mMaleGender.getText().toString();
            }
        });

        //Email Address AutoFill
        mAutoFill = (TextView) findViewById(R.id.email_autofill);
        mAutoFill.setVisibility(View.VISIBLE);
        mAutoFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Account Manager Permission request
                if (ActivityCompat.checkSelfPermission(ApplicationActivity.this,
                        android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                Account[] mAccounts = AccountManager.get(ApplicationActivity.this)
                        .getAccounts(); //.getAccountsByType("com.google");
                for (Account account : mAccounts) {

                    // account.name as an email address only for certain account.type values.
                    sPossibleEmail = account.name;
                    Log.i(TAG, "Accounts: " + account);
                }
                //hiding the autofill link once Email is set
                mEmailAddress.setText(sPossibleEmail);
                mAutoFill.setVisibility(View.INVISIBLE);
            }
        });

        //Terms and Policies Inflate
        final TextView mTermsPolicies = (TextView) findViewById(R.id.terms_policies_title);
        mTermsPolicies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ApplicationActivity.this, R.style.AlertDialogCustom));
                builder.setTitle(getString(R.string.alert_dialog_title)+ "\n");
                builder.setIcon(R.mipmap.terms);
                //Terms and Policies of the Tournament
                builder.setMessage(
                        "1. Application Forms should be accompanied by Requisite Commitment Charges, Birth Certificate and Photograph.\n"
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

        //Title Font
        Typeface mCustomFont2 = Typeface.createFromAsset(getAssets(), "fonts/GoodDog.otf");

        mApplicationFormTitle = (TextView) findViewById(R.id.application_form_title);
        mApplicationFormTitle.setTypeface(mCustomFont2);
        mApplicationFormTitle.setTextSize(45);

        mUploadProfileTitle = (TextView) findViewById(R.id.upload_picture_title);
        mUploadProfileTitle.setTypeface(mCustomFont2);
        mUploadProfileTitle.setTextSize(30);

        mUploadCertificateTitle = (TextView) findViewById(R.id.upload_certificate_title);
        mUploadCertificateTitle.setTypeface(mCustomFont2);
        mUploadCertificateTitle.setTextSize(30);

        mSelectGamesTitle = (TextView) findViewById(R.id.gamesTitle);
        mSelectGamesTitle.setTypeface(mCustomFont2);
        mSelectGamesTitle.setTextSize(30);

        //Edit Text Font
        Typeface mCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Watchword.otf");
        Typeface mCustomFont1 = Typeface.createFromAsset(getAssets(), "fonts/Monts.ttf");

        mGenderTitle = (TextView) findViewById(R.id.gender_title);
        mGenderTitle.setTextSize(25);
        mGenderTitle.setTypeface(mCustomFont);

        mFirstName.setTypeface(mCustomFont1);
        mLastName.setTypeface(mCustomFont1);
        mEmailAddress.setTypeface(mCustomFont1);
        mHomeAddress.setTypeface(mCustomFont1);
        mHomeAddress1.setTypeface(mCustomFont1);
        mContactNumber.setTypeface(mCustomFont1);
        mEmergencyNumber.setTypeface(mCustomFont1);
        mCheckboxGame1.setTypeface(mCustomFont);
        mCheckboxGame2.setTypeface(mCustomFont);
        mCheckboxGame3.setTypeface(mCustomFont);
        mCheckboxGame4.setTypeface(mCustomFont);
        mFemaleGender.setTypeface(mCustomFont1);
        mMaleGender.setTypeface(mCustomFont1);

        mAgeGroup.setTypeface(mCustomFont1);
        mAgeGroup.setTextSize(25);

        mFirstName.setTextSize(25);
        mLastName.setTextSize(25);
        mEmailAddress.setTextSize(25);
        mHomeAddress.setTextSize(25);
        mHomeAddress1.setTextSize(25);
        mContactNumber.setTextSize(30);
        mEmergencyNumber.setTextSize(30);
        mCheckboxGame1.setTextSize(20);
        mCheckboxGame2.setTextSize(20);
        mCheckboxGame3.setTextSize(20);
        mCheckboxGame4.setTextSize(20);
        mFemaleGender.setTextSize(20);
        mMaleGender.setTextSize(20);

        mTermsTitle = (TextView) findViewById(R.id.terms_policies_title);
        mTermsTitle.setTextSize(17);
        mTermsTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTermsTitle.setTypeface(mCustomFont1);

        //Email Address Autofill link Hide/Show
        mEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //AutoFill visibility "Visible" when 0 text in Edit text field
                if (mEmailAddress.length() == 0) {
                    mAutoFill.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //AutoFill visibility "Invisible" when more than 0 text in Edit text field
                if (mEmailAddress.length() > 0) {
                    mAutoFill.setVisibility(View.INVISIBLE);
                } else {
                    //Visible again when text is deleted
                    mAutoFill.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //AutoFill visibility "Visible" after text is entered
                if (mEmailAddress.length() > 0) {
                    mAutoFill.setVisibility(View.INVISIBLE);
                } else {
                    mAutoFill.setVisibility(View.VISIBLE);
                }
            }
        });

        //Clear all Fields Button
        mClear = (Button) findViewById(R.id.clear_button);
        mClear.setTypeface(mCustomFont);
        mClear.setTextSize(20);
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                        (ApplicationActivity.this, R.style.AlertDialogCustom));

                builder.setTitle("Confirmation"); //Confirmation Title

                //Confirmation Message
                builder.setMessage("Are you sure you want to clear all the fields?");
                builder.setIcon(R.mipmap.question);

                //Negative Button
                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                //Positive Button
                builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        fieldClear();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                TextView textView = (TextView) alert.findViewById(android.R.id.message);
                assert textView != null;
                textView.setTextSize(20);
            }
        });

        //Review Button
        mReview = (Button) findViewById(R.id.review_button);
        mReview.setTypeface(mCustomFont);
        mReview.setTextSize(20);
        mReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validation for Blank EditText Fields
                if (mFirstName.getText().toString().length() <= 2) {
                    Toast.makeText(getApplicationContext(), "Name field cannot be left Blank",
                            Toast.LENGTH_SHORT).show();
                    mFirstName.setError("At least 3 Characters");
                    return;

                } else if (mLastName.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Last Name field cannot be left Blank",
                            Toast.LENGTH_SHORT).show();
                    mLastName.setError("At least 1 Character");
                    return;

                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddress.getText().
                        toString()).matches()) {
                    Toast.makeText(getApplicationContext(), "Invalid Email Address",
                            Toast.LENGTH_SHORT).show();
                    mEmailAddress.setError("Enter a Valid Email Address");
                    return;

                } else if (mHomeAddress.getText().toString().length() <= 5) {
                    Toast.makeText(getApplicationContext(), "Home Address cannot be left Blank",
                            Toast.LENGTH_SHORT).show();
                    mHomeAddress.setError("Enter your Home Address");
                    return;

                } else if (mHomeAddress1.getText().toString().length() <= 5) {
                    Toast.makeText(getApplicationContext(), "Street Address cannot be left Blank",
                            Toast.LENGTH_SHORT).show();
                    mHomeAddress1.setError("Enter your Street Address");
                    return;

                } else if (mContactNumber.getText().toString().length() <= 7) {
                    Toast.makeText(getApplicationContext(), "Invalid Phone Number",
                            Toast.LENGTH_SHORT).show();
                    mContactNumber.setError("At least 8 digits");
                    return;

                } else if (mEmergencyNumber.getText().toString().length() <= 7) {
                    Toast.makeText(getApplicationContext(), "Invalid Emergency Contact Number",
                            Toast.LENGTH_SHORT).show();
                    mEmergencyNumber.setError("At least 8 digits");
                    return;

                } else if ((!(mCheckboxGame1.isChecked()))
                        && (!(mCheckboxGame2.isChecked()))
                        && (!(mCheckboxGame3.isChecked()))
                        && (!(mCheckboxGame4.isChecked()))) {
                    Toast.makeText(getBaseContext(), "No games Selected",
                            Toast.LENGTH_SHORT).show();
                    return;

                } else if (!(mTermsConditions.isChecked())) {
                    Toast.makeText(getBaseContext(), "Please Accept the terms " +
                                    "and Policies of the Tournament",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Checkbox Selections
                String text = "";

                if (mCheckboxGame1.isChecked()) {
                    text = text + mCheckboxGame1.getText() + "\n";
                }

                if (mCheckboxGame2.isChecked()) {
                    text = text + mCheckboxGame2.getText() + "\n";
                }

                if (mCheckboxGame3.isChecked()) {
                    text = text + mCheckboxGame3.getText() + "\n";
                }

                if (mCheckboxGame4.isChecked()) {
                    text = text + mCheckboxGame4.getText() + "\n";
                }

                //EditText Intents
                Intent intent = new Intent(ApplicationActivity.this,
                        ReviewActivity.class);

                intent.putExtra("emailAddress", mEmailAddress.getText().toString().trim());
                intent.putExtra("firstName", mFirstName.getText().toString().trim());
                intent.putExtra("lastName", mLastName.getText().toString().trim());
                intent.putExtra("homeAddress1", mHomeAddress.getText().toString().trim());
                intent.putExtra("homeAddress2", mHomeAddress1.getText().toString().trim());
                intent.putExtra("contactNumber", mContactNumber.getText().toString().trim());
                intent.putExtra("emergencyNumber", mEmergencyNumber.getText().toString().trim());
                intent.putExtra("checkboxValue", text);
                intent.putExtra("ageGroup", sAgeGroup);

                int id = mRadioGenderGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(id);
                intent.putExtra("radioGender", radioButton.getText().toString());

                //Profile picture to bytes
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //Certificate picture to bytes
                ByteArrayOutputStream bytes1 = new ByteArrayOutputStream();

                //Null ImageViews' Validation

                if (profileBitmap != null) {
                    profileBitmap.compress(Bitmap.CompressFormat.JPEG, 65, bytes);
                    byteArray = bytes.toByteArray();
                    intent.putExtra("profilePicture", byteArray);

                } else {
                    Toast.makeText(getBaseContext(), "Please Add your Profile Picture",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (certificateBitmap != null) {
                    certificateBitmap.compress(Bitmap.CompressFormat.JPEG, 65, bytes1);
                    byteArray1 = bytes1.toByteArray();
                    intent.putExtra("certificatePicture", byteArray1);

                } else {
                    Toast.makeText(getBaseContext(), "Please Add your Birth Certificate Image",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                startActivity(intent);
            }
        });
    }

    //Profile Image Camera or Gallery selection Method Dialog
    protected void mProfileImageSelection() {

        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                (ApplicationActivity.this, R.style.AlertDialogCustom));

        builder.setIcon(R.mipmap.profile_icon);
        builder.setTitle("Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                //Camera Option
                if (options[item].equals("Camera")) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST);
                }

                //Gallery Option
                else if (options[item].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST);
                }

                //Cancel Option
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    //Certificate Image Camera or Gallery selection Method
    protected void mCertificateImageSelection() {
        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                (ApplicationActivity.this, R.style.AlertDialogCustom));

        builder.setIcon(R.mipmap.birth_icon);
        builder.setTitle("Birth Certificate");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                //Camera Option
                if (options[item].equals("Camera")) {
                   Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                   startActivityForResult(intent, CAMERA_REQUEST_1);
                }

                //Gallery Option
                else if (options[item].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST_1);
                }

                //Cancel Option
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.gc();

        if (resultCode == RESULT_OK) {
            //Request Camera
            if (requestCode == CAMERA_REQUEST) {

                try {
                    setProfileCameraImage(intent, resultCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            //Request Gallery
            } else if (requestCode == GALLERY_REQUEST) {

                profileBitmap = ImagePicker.getImageFromResult(this, resultCode, intent);
                mProfileImage.setImageBitmap(profileBitmap);
            }
        }

        if (resultCode == RESULT_OK) {

            //Request Camera
            if (requestCode == CAMERA_REQUEST_1) {

                setCertificateCameraImage(intent, resultCode);

            //Request Gallery
            } else if (requestCode == GALLERY_REQUEST_1) {

                certificateBitmap = ImagePicker.getImageFromResult(this, resultCode, intent);
                mCertificateImage.setImageBitmap(certificateBitmap);
            }
        }
    }

    //Set Profile Image with Camera
    protected void setProfileCameraImage(Intent intent, int resultCode) {

        mImagePicker = new ImagePicker();
        profileBitmap = (Bitmap) intent.getExtras().get("data");
        mProfileImage = (ImageView) findViewById(R.id.profilePicture);

        //Uri mCapturedImageUri = intent.getData();

        try {
            String[] ids = mCameraManager.getCameraIdList();
            //Log.e(TAG, "Ids of Camera " + Arrays.toString(ids));

            for (String id : ids) {
                CameraCharacteristics props = mCameraManager.getCameraCharacteristics(id);
                assertNotNull("Can't get camera characteristics for camera " + id, props);

                Integer lensFacing = props.get(CameraCharacteristics.LENS_FACING);
                assertNotNull("Can't get lens facing info", lensFacing);

                //Camera device faces the opposite direction as the device's screen
                if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    assertTrue("System doesn't have back camera feature",
                            mPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS));
                    try {
                        //Bitmap bp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCapturedImageUri);
                        Bitmap bp = ImagePicker.getImageFromResult(this, resultCode, intent);
                        assert bp != null;
                        mProfileImage.setImageBitmap(bp);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                //Camera device faces the same direction as the device's screen
                } else if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    assertTrue("System doesn't have front camera feature",
                            mPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT));
                    try {
                        //Bitmap bp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCapturedImageUri);
                        Bitmap bp = ImagePicker.getImageFromResult(this, resultCode, intent);
                        assert bp != null;
                        mProfileImage.setImageBitmap(bp);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    fail("Unknown camera lens facing " + lensFacing.toString());
                }
            }

        }catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //Set Certificate Image with Camera
    protected void setCertificateCameraImage(Intent intent, int resultCode) {

        mImagePicker = new ImagePicker();
        certificateBitmap = (Bitmap) intent.getExtras().get("data");
        mCertificateImage = (ImageView) findViewById(R.id.certificateImage);

        //Uri mCapturedImageUri = intent.getData();

        try {
            String[] ids = mCameraManager.getCameraIdList();
            //Log.e(TAG, "Ids of Camera " + Arrays.toString(ids));

            for (String id : ids) {
                CameraCharacteristics props = mCameraManager.getCameraCharacteristics(id);
                assertNotNull("Can't get camera characteristics for camera " + id, props);

                Integer lensFacing = props.get(CameraCharacteristics.LENS_FACING);
                assertNotNull("Can't get lens facing info", lensFacing);

                //Camera device faces the opposite direction as the device's screen
                if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    assertTrue("System doesn't have back camera feature",
                            mPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS));
                    try {
                        //Bitmap bp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCapturedImageUri);
                        Bitmap bp = ImagePicker.getImageFromResult(this, resultCode, intent);
                        assert bp != null;
                        mCertificateImage.setImageBitmap(bp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                //Camera device faces the same direction as the device's screen
                } else if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    assertTrue("System doesn't have front camera feature",
                            mPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT));
                    try {
                        //Bitmap bp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCapturedImageUri);
                        Bitmap bp = ImagePicker.getImageFromResult(this, resultCode, intent);
                        assert bp !=null;
                        mCertificateImage.setImageBitmap(bp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    fail("Unknown camera lens facing " + lensFacing.toString());
                }
            }
        }catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //Clear all fields
    public void fieldClear() {

        System.out.println("Clear Fields");

        mFirstName.setText("");
        mEmailAddress.setText("");
        mLastName.setText("");
        mHomeAddress.setText("");
        mHomeAddress1.setText("");
        mContactNumber.setText("");
        mEmergencyNumber.setText("");

        mProfileImage.setImageResource(ic_profile_image);
        mCertificateImage.setImageResource(ic_documents);

        mCheckboxGame1.setChecked(false);
        mCheckboxGame2.setChecked(false);
        mCheckboxGame3.setChecked(false);
        mCheckboxGame4.setChecked(false);
    }

    //Hide keyboard when touched anywhere else on the screen
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        int[] location = new int[2];
        mFirstName.getLocationOnScreen(location);
        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mFirstName.getWidth();
        mRect.bottom = location[1] + mFirstName.getHeight();

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN && !mRect.contains(x, y)) {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(mFirstName.getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "++ ON START ++");
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.v(TAG, "- ON RESTART -");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "+ ON RESUME +");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "- ON DESTROY -");
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }
}