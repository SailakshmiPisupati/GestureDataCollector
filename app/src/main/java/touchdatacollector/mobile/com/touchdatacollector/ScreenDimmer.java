package touchdatacollector.mobile.com.touchdatacollector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Saila on 11/7/2017.
 */
public class ScreenDimmer extends Service {

    public static final String TAG = "DrawOverAppsService";

    private View mOverlayView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH|
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSLUCENT);

        // An alpha value to apply to this entire window.
        // An alpha of 1.0 means fully opaque and 0.0 means fully transparent
        params.alpha = 0.1F;

        // When FLAG_DIM_BEHIND is set, this is the amount of dimming to apply.
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        params.dimAmount = 0.8F;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mOverlayView = inflater.inflate(R.layout.dimmer, null);

        wm.addView(mOverlayView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(mOverlayView);
    }

    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
//        Toast.makeText(getContext(),"onTouchEvent", Toast.LENGTH_LONG).show();
        Log.d(TAG,"Event being fired "+event.toString());
        return true;
    }
}