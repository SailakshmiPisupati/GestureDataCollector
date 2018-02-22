package touchdatacollector.mobile.com.touchdatacollector;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.MotionEvent.INVALID_POINTER_ID;

/**
 * Created by saila on 2/19/18.
 */

public class PinchandZoomActivity extends AppCompatActivity{
    ImageView imageview;
    Matrix matrix = new Matrix();

    Float scale = 1f;
    ScaleGestureDetector SDG;

    private JSONObject strokes;
    private JSONArray strokeArray = null;
    private int strokeid = 0;
    private int pointid = 0;
    private static final String DEBUG_TAG = "PinchZoomGesture";
    private String gestureType = "PinchZoom";

    String USERID;

    @Override
    protected void onCreate(Bundle savedInstancestate) {

        super.onCreate(savedInstancestate);
        setContentView(R.layout.pinchzoomlayout);
        imageview = (ImageView) findViewById(R.id.imageView);
        SDG = new ScaleGestureDetector(this, new ScaleListener());
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.userid), Context.MODE_PRIVATE);
        USERID = sharedPref.getString(getString(R.string.userid),"");
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector){
            scale = scale * detector.getScaleFactor();
            scale = Math.max(0.1f,Math.min(scale,5f));
            matrix.setScale(scale,scale);
            imageview.setImageMatrix(matrix);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        SDG.onTouchEvent(event);
        strokeArray = new JSONArray();
        if (event.getPointerCount() > 1) {
            Log.d(DEBUG_TAG,"Multitouch event");
            collectGestureData(event,strokeArray,event.getAction());
        }else if(event.getPointerCount() == 1 && event.getAction() == 0){
            strokeid++;
            pointid = 0;
            Log.d(DEBUG_TAG,"SingleTouch event and start stroke");
            collectGestureData(event,strokeArray,event.getAction());
        }
        return true;

    }
    void collectGestureData(MotionEvent event, JSONArray strokeArray,int action) {
        switch (action){
            case 0 :
                Log.d(DEBUG_TAG,"SingleTouch event and start stroke");
                strokeArray.put(addSingleStrokeToJSONObject(event));
                break;
            case 1:
                Log.d(DEBUG_TAG,"SingleTouch event and move stroke");
                strokeArray.put(addSingleStrokeToJSONObject(event));
                break;
            case 2:
                Log.d(DEBUG_TAG,"MultiTouch event and move stroke");
                strokeArray.put(addStrokeToJSONObject(event));
                break;
        }
    }


    private JSONObject addStrokeToJSONObject(MotionEvent event){
        JSONObject strokes = new JSONObject();
        int mActivePointerId = event.getPointerId(0);
        int pointerIndex1 = event.findPointerIndex(mActivePointerId);
        int mActivePointerId2 = event.getPointerId(1);
        int pointerIndex12 = event.findPointerIndex(mActivePointerId2);

        try{
            strokes.put("strokeid",strokeid);
            strokes.put("pointid",pointid++);
            strokes.put("deviceId",Utils.getDeviceID());
            strokes.put("time",event.getEventTime());
            strokes.put("x1",event.getX(pointerIndex1));
            strokes.put("y1",event.getY(pointerIndex1));
            strokes.put("pressure1",event.getPressure(pointerIndex1));
            strokes.put("size1",event.getSize(pointerIndex1));
            strokes.put("x2",event.getX(pointerIndex12));
            strokes.put("y2",event.getY(pointerIndex12));
            strokes.put("pressure2",event.getPressure(pointerIndex12));
            strokes.put("size2",event.getSize(pointerIndex12));
            strokes.put("action",event.getAction());
            strokes.put("userid",USERID);
            strokes.put("gesture", gestureType);
        }catch(JSONException e){
            System.out.print(e);
        }
        Log.d("PinchAndZoom",strokes.toString());
        return strokes;
    }

    private JSONObject addSingleStrokeToJSONObject(MotionEvent event){
        JSONObject strokes = new JSONObject();
        try{
            strokes.put("strokeid",strokeid);
            strokes.put("pointid",pointid++);
            strokes.put("deviceId",Utils.getDeviceID());
            strokes.put("time",event.getEventTime());
            strokes.put("x1",event.getX());
            strokes.put("y1",event.getY());
            strokes.put("pressure1",event.getPressure());
            strokes.put("size1",event.getSize());
            strokes.put("action",event.getAction());
            strokes.put("userid",USERID);
            strokes.put("gesture", gestureType);
        }catch(JSONException e){
            System.out.print(e);
        }
        Log.d("PinchAndZoom",strokes.toString());
        return strokes;
    }
}
