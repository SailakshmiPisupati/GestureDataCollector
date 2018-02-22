package touchdatacollector.mobile.com.touchdatacollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Saila on 11/6/2017.
 */

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_activity);
//        Button mStartProcess = (Button) findViewById(R.id.start_process_button);
//        mStartProcess.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "Onclick listener called" + v.getId());
//            }
//        });
//
//        Button mAddOverlay = (Button) findViewById(R.id.button_add_overlay);
//        mAddOverlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "Onclick listener called" + v.getId());
//            }
//        });
//
//        Button mButton = (Button) findViewById(R.id.button);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "Onclick listener called" + v.getId());
//            }
//        });

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.userid), Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.userid),"");

        if(username == ""){
            //call register user
            Intent registerIntent = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(registerIntent);
        }else{
            //call the activity to collect data
            Intent rawTouchDataIntent = new Intent(getApplicationContext(),RawTouchData.class);
            startActivity(rawTouchDataIntent);
        }
    }


    public String getDeviceID() {
        TelephonyManager tManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "Permission denied.";
        }
        String uid = tManager.getDeviceId();
        Utils.setDeviceID(uid);
        return uid;
    }

}
