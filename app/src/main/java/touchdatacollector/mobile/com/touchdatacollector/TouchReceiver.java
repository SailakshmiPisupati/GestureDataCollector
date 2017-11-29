package touchdatacollector.mobile.com.touchdatacollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Saila on 11/13/2017.
 */

public class TouchReceiver extends BroadcastReceiver {
    public TouchReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "Action: " + intent.getAction(), Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals("touch_event_has_occured"))
        {
            Log.d("BroadcastReceiver", "Touch");
        }
    }
}
