package touchdatacollector.mobile.com.touchdatacollector;

import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Saila on 11/15/2017.
 */

public class DataSender {
    private static final String DEBUG_TAG = "Gestures";
    private JSONObject strokes;
    private JSONArray strokeArray = null;
    private int strokeid = 0;
    private int pointid = 0;
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
            sendDataToServer(strokeArray);
        }
        //Creating a JSONObject of all the Strokes
        strokeArray.put(addStrokeToJSONObject(ev));

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
//            strokes.put("gesture","calculated at server");
        }catch(JSONException e){
            System.out.print(e);
        }
        return strokes;
    }
    public void sendDataToServer(JSONArray strokeArray){
        Log.d(DEBUG_TAG,"added JSONArray"+ strokeArray.toString());
        new DataSender.SendDataAsyncTask().execute(strokeArray.toString());
    }


    class SendDataAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d(DEBUG_TAG,"params are:"+params[0]);
            URL url = null;
            HttpURLConnection connection = null;
            try{
                url = new URL("http://10.0.2.2:3000/saveData");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.connect();
                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                Log.d(DEBUG_TAG,"SENDING DATA TO URL AS : "+params[0]);
                writer.write(params[0]);
                writer.close();
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
}
