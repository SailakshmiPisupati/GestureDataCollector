package touchdatacollector.mobile.com.touchdatacollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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
    private String gestureType = "Vertical";

    String USERID;
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_test_layout);
        ScrollView mScrollView = (ScrollView) findViewById(R.id.scrollView);
        Button scrollHorizontal = (Button) findViewById(R.id.scrollHorizontalTask);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.userid), Context.MODE_PRIVATE);
        TextView userid_tv = (TextView)findViewById(R.id.userid);
        USERID = sharedPref.getString(getString(R.string.userid),"");
        userid_tv.setText(USERID);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if(event.getAction() == 0){
                    strokeArray = new JSONArray();
                }
                //sendBroadcast(new Intent("touch_event_has_occured"));
                collectGestureData(event,strokeArray);  //Data collection function
                return false;
            }
        });

        scrollHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scrollHorizontal = new Intent(getApplicationContext(),ScrollHorizontal.class);
                startActivity(scrollHorizontal);
            }
        });
    }
    // This example shows an Activity, but you would use the same approach if
    // you were subclassing a View.

    private MotionEvent lastEvent;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(DEBUG_TAG,"onTouchEvent occureed");
        int action = MotionEventCompat.getActionMasked(event);
        if(event.getAction() == 0){
            strokeArray = new JSONArray();
        }
        //sendBroadcast(new Intent("touch_event_has_occured"));
        collectGestureData(event,strokeArray);  //Data collection function
        return true;
    }

    boolean collectGestureData(MotionEvent ev, JSONArray strokeArray) {

        if(ev.getAction() == 0) {                //0 => Action_DOWN (Maintain a property file)
            Log.d(DEBUG_TAG, "Start stroke.");
            strokeid++;
            pointid = 0;
            strokeArray = new JSONArray();
            strokeArray.put(addStrokeToJSONObject(ev));
        }else if(ev.getAction() == 1){
            Log.d(DEBUG_TAG,"End stroke.");
            strokeArray.put(addStrokeToJSONObject(ev));

            //Send JSONObject to server
//            sendDataToServer();

            DataSender sender = new DataSender();
            sender.sendDataToServer(strokeArray);
        }
        //Creating a JSONObject of all the Strokes
        strokeArray.put(addStrokeToJSONObject(ev));

        return true;
    }

    private void sendDataToServer()  {
        Log.d(DEBUG_TAG,"added JSONArray"+ strokeArray);
//        JSONObject jsonObject = new JSONObject();
//        try{
//            jsonObject.put("data",strokeArray);
//        }catch(Exception e){
//            Log.d(DEBUG_TAG,"unable to add JSONArray");
//        }
        new SendDataAsyncTask().execute();
//        new SendDataAsyncTask().execute(strokeArray);
    }

    private JSONObject addStrokeToJSONObject(MotionEvent event){
        JSONObject strokes = new JSONObject();
        try{
            strokes.put("strokeid",strokeid);
            strokes.put("pointid",pointid++);
            strokes.put("deviceId",Utils.getDeviceID());
            strokes.put("time",event.getEventTime());
            strokes.put("x",event.getX());
            strokes.put("y",event.getY());
            strokes.put("pressure",event.getPressure());
//            strokes.put("size",event.getSize());
            strokes.put("size",event.getSize());
            strokes.put("action",event.getAction());
            strokes.put("userid",USERID);
            strokes.put("gesture", gestureType);
        }catch(JSONException e){
            System.out.print(e);
        }
        return strokes;
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

    }

    class SendDataAsyncTask extends AsyncTask<Void, String, String>{

        @Override
//        protected String doInBackground(String... params) {
        protected String doInBackground(Void... params) {

            Log.d(DEBUG_TAG,"params are:"+ params.toString());
            URL url = null;
            HttpURLConnection connection = null;
            try{
                String IP_address = Utils.getIPADDRESS();
                String port = Utils.getPortNumber();
                url = new URL("http://"+IP_address+":"+port+"/saveData");
//                url = new URL("http://10.0.2.2:3000/saveData");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.connect();
                OutputStream os = null;
                os = new BufferedOutputStream(
                        connection.getOutputStream());
                os.write(strokeArray.toString().getBytes());
                os.flush();// writing your data which you post
//                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
//                Log.d(DEBUG_TAG,"SENDING DATA TO URL AS : "+params[0]);
//                writer.write(params[0]);
//                writer.close();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // OK
                    Log.d(DEBUG_TAG,"Done ok ");
                    // otherwise, if any other status code is returned, or no status
                    // code is returned, do stuff in the else block
                } else {
                    Log.d(DEBUG_TAG,"Server returned error. ");
                    // Server returned HTTP error code.
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(DEBUG_TAG,"Unable to connect to server: ");
            }finally {
                connection.disconnect();
            }
            return null;
        }
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

}
