package touchdatacollector.mobile.com.touchdatacollector;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Saila on 11/6/2017.
 */

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Button mStartProcess = (Button) findViewById(R.id.start_process_button);
        mStartProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Onclick listener called" + v.getId());
            }
        });

        Button mAddOverlay = (Button) findViewById(R.id.button_add_overlay);
        mAddOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Onclick listener called" + v.getId());
            }
        });

        Button mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Onclick listener called" + v.getId());
            }
        });
    }


}
