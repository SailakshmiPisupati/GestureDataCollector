package touchdatacollector.mobile.com.touchdatacollector;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


/**
 * Created by Saila on 10/24/2017.
 */

public class RawTouchData extends Activity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, GestureOverlayView.OnGesturePerformedListener{
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private JSONObject strokes;
    private JSONArray strokeArray = null;
    private int strokeid = 0;
    private int pointid = 0;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);
        super.onCreate(savedInstanceState);
//        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
//        View inflate = getLayoutInflater().inflate(R.layout.activity_login, null);
//        gestureOverlayView.addView(inflate);
//        gestureOverlayView.addOnGesturePerformedListener(this);
//        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
//        if (!gestureLib.load()) {
//            finish();
//        }
//        setContentView(gestureOverlayView);
    }
    // This example shows an Activity, but you would use the same approach if
    // you were subclassing a View.

    private MotionEvent lastEvent;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);
        if(event.getAction() == 0){
            strokeArray = new JSONArray();
        }
        collectGestureData(event,strokeArray);  //Data collection function
        return true;
    }

    boolean collectGestureData(MotionEvent ev, JSONArray strokeArray) {

        if(ev.getAction() == 0) {                //0 => Action_DOWN (Maintain a property file)
            Log.d(DEBUG_TAG, "Start stroke.");
            //Creating a JSONObject of all the Strokes
            strokeid++;
            strokeArray = new JSONArray();
        }else if(ev.getAction() == 1){
            Log.d(DEBUG_TAG,"End stroke.");
            //Creating a JSONObject of the last press.
            Log.d(DEBUG_TAG,"added JSONArray"+ strokeArray.toString());

            //Send JSONObject to server
        }
        strokeArray.put(addStrokeToJSONObject(ev));
//        final int pointerCount = ev.getPointerCount();
//        Log.d(DEBUG_TAG,"At time "+ev.getEventTime());
//        for (int p = 0; p < pointerCount; p++) {
//            Log.d(DEBUG_TAG, "At time " + ev.getEventTime() + " " + ev.getDeviceId() + " " + ev.getRawX() + " " + ev.getRawY() + " " + ev.getPointerId(p) + " " + ev.getX(p) + " " + ev.getY(p) + " " + ev.getPressure() + " " + ev.getSize() + " " + ev.getAction() + " " + ev.toString());
//
//        }
        return true;
    }

    private JSONObject addStrokeToJSONObject(MotionEvent event){
        JSONObject strokes = new JSONObject();
        try{
            strokes.put("strokeid",strokeid);
            strokes.put("pointid",pointid++);
            strokes.put("deviceId",event.getDeviceId());
            strokes.put("time",event.getEventTime());
            strokes.put("x",event.getX());
            strokes.put("y",event.getY());
            strokes.put("pressure",event.getPressure());
            strokes.put("size",event.getSize());
            strokes.put("action",event.getAction());
            strokes.put("gesture","enter the swipe or tap or zoom event");
        }catch(JSONException e){
            System.out.print(e);
        }
        return strokes;
    }
    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }
    private GestureLibrary gestureLib;
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        for (Prediction prediction : predictions) {
            if (prediction.score > 1.0) {
                Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}
