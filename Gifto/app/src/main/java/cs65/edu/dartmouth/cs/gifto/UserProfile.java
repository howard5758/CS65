package cs65.edu.dartmouth.cs.gifto;

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Oliver on 2/25/2018.
 *
 * This is what I made for the first week! It's probably terrible! I will fix it later!
 */

public class UserProfile extends AppCompatActivity {
    /* Constants for saving settings */
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GALLERY = 2;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String NAME_INSTANCE_STATE_KEY = "saved_name";
    private static final String EMAIL_INSTANCE_STATE_KEY = "saved_email";
    private static final String PHONE_INSTANCE_STATE_KEY = "saved_phone";
    private static final String GENDER_INSTANCE_STATE_KEY = "saved_gender";
    private static final String CLASS_INSTANCE_STATE_KEY = "saved_class";
    private static final String MAJOR_INSTANCE_STATE_KEY = "saved_major";

    private FirebaseAuth firebaseAuth;

    /* variables for picture taking */
    Uri mImageCaptureUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        checkPermissions();
        setText();
        loadSnap();
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
        }

        Button button = findViewById(R.id.change_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    /* checks if user has allowed use of camera and storage, returns true if yes and false if
     * permission not allowed */
    private boolean checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return true;

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, 0);
            return false;
        }
        return true;
    }

    /* Sets the text, radio fields, and profile picture with saved data from SharedPreferences */
    private void setText() {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        EditText editText = findViewById(R.id.Name_field);
        editText.setText(settings.getString("Name", ""));

        editText = findViewById(R.id.Email_field);
        editText.setText(settings.getString("Email", ""));

        editText = findViewById(R.id.Phone_field);
        editText.setText(settings.getString("Phone", ""));

        int i = settings.getInt("gender", -1);
        if (i >= 0) {
            ((RadioButton) ((RadioGroup)findViewById(R.id.gender_radio)).getChildAt(i)).setChecked(true);
        }

        editText = findViewById(R.id.Class_field);
        editText.setText(settings.getString("Class", ""));

        editText = findViewById(R.id.Major_field);
        editText.setText(settings.getString("Major", ""));

        ImageView imageView = findViewById(R.id.profile_picture);
        imageView.setImageURI(Uri.parse(settings.getString("uri", "")));
    }

    /* Save state data on screen rotation */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);

        EditText editText = findViewById(R.id.Name_field);
        outState.putString(NAME_INSTANCE_STATE_KEY, editText.getText().toString());

        editText = findViewById(R.id.Email_field);
        outState.putString(EMAIL_INSTANCE_STATE_KEY, editText.getText().toString());

        editText = findViewById(R.id.Phone_field);
        outState.putString(PHONE_INSTANCE_STATE_KEY, editText.getText().toString());

        RadioGroup rg = findViewById(R.id.gender_radio);
        outState.putInt(GENDER_INSTANCE_STATE_KEY, rg.indexOfChild(findViewById(rg.getCheckedRadioButtonId())));

        editText = findViewById(R.id.Class_field);
        outState.putString(CLASS_INSTANCE_STATE_KEY, editText.getText().toString());

        editText = findViewById(R.id.Major_field);
        outState.putString(MAJOR_INSTANCE_STATE_KEY, editText.getText().toString());
    }

    /* restore state data after screen rotation */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        mImageCaptureUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
        ImageView imageView = findViewById(R.id.profile_picture);
        imageView.setImageURI(mImageCaptureUri);

        EditText editText = findViewById(R.id.Name_field);
        editText.setText(savedInstanceState.getString(NAME_INSTANCE_STATE_KEY, ""));

        editText = findViewById(R.id.Email_field);
        editText.setText(savedInstanceState.getString(EMAIL_INSTANCE_STATE_KEY, ""));

        editText = findViewById(R.id.Phone_field);
        editText.setText(savedInstanceState.getString(PHONE_INSTANCE_STATE_KEY, ""));

        int i = savedInstanceState.getInt(GENDER_INSTANCE_STATE_KEY, -1);
        if (i >= 0) {
            ((RadioButton) ((RadioGroup)findViewById(R.id.gender_radio)).getChildAt(i)).setChecked(true);
        }

        editText = findViewById(R.id.Class_field);
        editText.setText(savedInstanceState.getString(CLASS_INSTANCE_STATE_KEY, ""));

        editText = findViewById(R.id.Major_field);
        editText.setText(savedInstanceState.getString(MAJOR_INSTANCE_STATE_KEY, ""));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            Util.showActivity(this, LoginActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }

    /* Show dialog to choose either camera or picture from gallery */
    void showDialog() {
        DialogFragment newFragment = MyAlertDialogFragment
                .newInstance(R.string.clicked);
        newFragment.show(getFragmentManager(), "dialog");
    }

    /* Alert dialog class to handle picking camera or gallery */
    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int title) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setItems(R.array.camera_options,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((UserProfile) getActivity()).changePhoto(which);
                                }
                            }).create();
        }
    }

    /* Button to change profile picture
     * If permissions denied, then do not allow user to take picture
     * If permissions accepted and camera selected, then open camera
     * If permissions accepted and allery selected, open gallery*/
    public void changePhoto(int item) {
        if (!checkPermissions()) {
            Context context = getApplicationContext();
            Toast.makeText(context, "Permission not granted to take or store photos", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent;

        if (item == 0) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        else if (item == 1) {
            intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        else {
            return;
        }

        if (intent.resolveActivity(getPackageManager()) != null && item == 0) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            mImageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

        else if (intent.resolveActivity(getPackageManager()) != null && item == 1) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            mImageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
        }
    }

    /* When camera is finished, start crop, then put cropped image in ImageView */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("request code", Integer.toString(requestCode));
        if(resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("imageUri", mImageCaptureUri.toString());
            Crop.of(mImageCaptureUri, mImageCaptureUri).asSquare().start(this);
        }
        else if (requestCode == REQUEST_GALLERY) {
            Uri temp= data.getData();
            Crop.of(temp, mImageCaptureUri).asSquare().start(this);
        }
        else if (requestCode == Crop.REQUEST_CROP) {
            Log.d("before assigning uri", "before assigning uri");
            mImageCaptureUri = Crop.getOutput(data);

            try {
                Log.d("in try", "in try");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                ImageView imageView = findViewById(R.id.profile_picture);
                imageView.setImageURI(null);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Close app without saving data */
    public void cancel(View view) {
        finish();
    }

    /* Save all settings to SharedPreferences, then close app */
    public void saveSettings(View view) {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        EditText editText = findViewById(R.id.Name_field);
        editText.setText(editText.getText().toString());
        editor.putString("Name", editText.getText().toString());

        editText = findViewById(R.id.Email_field);
        editText.setText(editText.getText().toString());
        editor.putString("Email", editText.getText().toString());

        editText = findViewById(R.id.Phone_field);
        editText.setText(editText.getText().toString());
        editor.putString("Phone", editText.getText().toString());

        RadioGroup rg = findViewById(R.id.gender_radio);
        editor.putInt("gender", rg.indexOfChild(findViewById(rg.getCheckedRadioButtonId())));

        editText = findViewById(R.id.Class_field);
        editText.setText(editText.getText().toString());
        editor.putString("Class", editText.getText().toString());

        editText = findViewById(R.id.Major_field);
        editText.setText(editText.getText().toString());
        editor.putString("Major", editText.getText().toString());

        editor.apply();
        saveSnap();

        Context context = getApplicationContext();
        Toast.makeText(context, "Login details have been saved", Toast.LENGTH_SHORT).show();

        finish();
    }

    /* Helper functions to save and load pictures */
    private void saveSnap() {
        ImageView mImageView = findViewById(R.id.profile_picture);
        mImageView.buildDrawingCache();
        Bitmap bmap = mImageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void loadSnap() {
        ImageView mImageView = findViewById(R.id.profile_picture);

        try {
            FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mImageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            mImageView.setImageResource(R.drawable.default_profile);
        }
    }
}
