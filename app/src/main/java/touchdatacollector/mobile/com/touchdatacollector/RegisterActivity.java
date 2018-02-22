package touchdatacollector.mobile.com.touchdatacollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by saila on 11/30/17.
 */

public class RegisterActivity extends Activity {
    public static final String TAG = "Gesture / RegisterForm";
    AutoCompleteTextView username;
    AutoCompleteTextView emailid;
    Spinner gender;
    Spinner dominating_hand;
    JSONObject userData;
    CheckBox agree;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String UserName = "nameKey";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_form_layout);

        intializeformdata();
    }

    public void intializeformdata(){
        Button registerDataButton = (Button) findViewById(R.id.register_button);
        Button startTasks = (Button) findViewById(R.id.start_tasks);
        username = (AutoCompleteTextView)findViewById(R.id.username);
        emailid = (AutoCompleteTextView)findViewById(R.id.emailid);
        gender = (Spinner) findViewById(R.id.gender);
        dominating_hand = (Spinner) findViewById(R.id.dominating_hand);
        agree = (CheckBox) findViewById(R.id.terms_and_conditions_checkbox);

        registerDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData(username,emailid,gender,dominating_hand)){
                    try{
                        Log.d(TAG,"username "+username.getText().toString());
                        userData = new JSONObject();
                        userData.put("username",username.getText().toString());
                        userData.put("emailid",emailid.getText().toString());
                        userData.put("gender",gender.getSelectedItem().toString());
                        userData.put("dominating_hand",dominating_hand.getSelectedItem().toString());
                        createAccount(userData);
                    }catch(JSONException e){
                        Log.d(TAG,"Error in creating json object");
                    }
                }
            }
        });

        startTasks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent startTasks = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(startTasks);
            }
        });
    }

    public boolean validateData(AutoCompleteTextView username,AutoCompleteTextView emailid, Spinner gender,Spinner dominating_hand){

        if(agree.isChecked()){
            //authentications
            return true;
        }else{
            Toast.makeText(this,"Please agree to proceed.",Toast.LENGTH_SHORT);
            return false;
        }

    }

    public void createAccount(JSONObject userData){
        // connect to server
        // send data
        Log.d(TAG,"added JSONArray"+ userData.toString());
        SendDataAsyncTask sendDataAsyncTask = new SendDataAsyncTask();
        sendDataAsyncTask.execute(userData.toString());
    }

    class SendDataAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG,"params are:"+params[0]);
            URL url = null;
            HttpURLConnection connection = null;
            try{
                String IP_address = Utils.getIPADDRESS();
                String port = Utils.getPortNumber();
                url = new URL("http://"+IP_address+":"+port+"/registerUser");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.connect();
                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                Log.d(TAG,"SENDING DATA TO URL AS : "+params[0]);
                writer.write(params[0]);
                writer.close();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // OK
                    Log.d(TAG,"Done ok with content as "+connection.getContent());
                    BufferedReader in=new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    Log.d(TAG,"result is "+sb.toString());
                    return sb.toString();

                    // otherwise, if any other status code is returned, or no status
                    // code is returned, do stuff in the else block
                } else {
                    Log.d(TAG,"Server returned error. ");
                    // Server returned HTTP error code.
                    return new String("false: "+connection.getResponseCode());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"Unable to connect to server: ");
            }finally {
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){

            String userid = null;
            try {
                JSONObject json = new JSONObject(result);
                userid =  json.get("userid").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

            Log.d(TAG,"USER ID before saving is "+getString(R.string.userid));
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.userid), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.putString(getString(R.string.userid),userid);
            editor.commit();
            Log.d(TAG,"USER ID after saving is "+sharedPref.getString(getString(R.string.userid),""));

            Intent rawTouchDataIntent = new Intent(getApplicationContext(),RawTouchData.class);
            startActivity(rawTouchDataIntent);
        }

    }
}
