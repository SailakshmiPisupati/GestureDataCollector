package touchdatacollector.mobile.com.touchdatacollector;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

/**
 * Created by Saila on 10/16/2017.
 */

public class DataCollectorService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;


    public void onCreate(){
        HandlerThread thread = new HandlerThread("DataCollectorService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();

        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
//        showToast("onStartCommand");
        collectTouchData("Starting Data Collection Now");

        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void collectTouchData(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                // write the code to collect data
                //onTouchEvent() -> collect data and send to application server
                Intent rawTouchDataIntent = new Intent(getApplicationContext(),RawTouchData.class);
                startActivity(rawTouchDataIntent);
            }
        });
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper){
            super(looper);
        }

        public void handleMessage(Message msg){
            // Well calling mServiceHandler.sendMessage(message); from onStartCommand,
            // this method will be called.
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
            // Add your cpu-blocking activity here
        }
    }
}
