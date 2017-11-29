package touchdatacollector.mobile.com.touchdatacollector;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity  implements LoaderCallbacks<Cursor> {

    private static final String TAG = "Gesture";
    private static int REQUEST_PERMISSION = 201;
    private static final String[] MY_PERMISSIONS_REQUEST_READ_CONTACTS = {};
    private String[] permissions = {Manifest.permission.INTERNET,Manifest.permission.SYSTEM_ALERT_WINDOW};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            return;
        }

        Button mStartProcess = (Button) findViewById(R.id.start_process_button);
        mStartProcess.setOnClickListener(onClickListener);

        Button mAddOverlay = (Button) findViewById(R.id.button_add_overlay);
        mAddOverlay.setOnClickListener(onClickListener);

        Button mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(onClickListener);

        Button mHorizontalButtonButton = (Button) findViewById(R.id.scrollHorizontalButton);
        mHorizontalButtonButton.setOnClickListener(onClickListener);
    }

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_process_button: {
                    Intent rawTouchDataIntent = new Intent(getApplicationContext(),RawTouchData.class);
                    startActivity(rawTouchDataIntent);
                    break;
                }
                case R.id.scrollHorizontalButton: {
                    Log.d(TAG, "Clicked new task");
                    Intent scrollHorizontal = new Intent(getApplicationContext(),ScrollHorizontal.class);
                    startActivity(scrollHorizontal);
                    break;
                }
                case R.id.button_add_overlay: {
                    Log.d(TAG, "Adding overlay. calling method");
                    addWindowOverlay();
                    break;
                }

                case R.id.button: {
                    Log.d(TAG, "Adding overlay. calling button");
//                    Intent screenDimmerIntent = new Intent(getApplicationContext(),ScreenDimmer.class);
//                    startService(screenDimmerIntent);
                    IntentFilter filter = new IntentFilter();
                    TouchReceiver touchReceiver = new TouchReceiver();
                    registerReceiver(touchReceiver,filter);
                    break;
                }
            }
    }};

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            } else {
                // User refused to grant permission.
            }
        }
    }
    private void getRunTimePermissions(String[] permissions){
        int MY_PERMISSIONS_REQUEST_READ_CONTACTS =0;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, permissions,MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void startCollectionProcess() {
        //code for calling the background service to start implicit authentication
        Log.i(TAG, "Authenticated and now starting the background process");
        Intent dataCollector = new Intent(this, DataCollectorService.class);
        startService(dataCollector);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(getTitle()+"Touch occured");
    }

    private void addWindowOverlay() {
        //code to add a Window Overlay on top for all screens.
        Log.i(TAG, "Adding overlay. inside method");
//        Intent intent = new Intent(this, WindowAddOverlay.class);
        //startActivity(intent);

        Intent startOverlay = new Intent(LoginActivity.this, HUD.class);
        startService(startOverlay);
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}



