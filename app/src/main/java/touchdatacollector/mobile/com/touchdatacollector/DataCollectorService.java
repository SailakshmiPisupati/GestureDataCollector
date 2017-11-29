package touchdatacollector.mobile.com.touchdatacollector;

import android.app.Service;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;
import android.os.Process;

import static android.content.ContentValues.TAG;

/**
 * Created by Saila on 10/16/2017.
 */

public class DataCollectorService extends Service implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, GestureOverlayView.OnGesturePerformedListener {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private static final String DEBUG_TAG ="Gesture";
    private GestureDetectorCompat mDetector;

    public void onCreate(){
        HandlerThread thread = new HandlerThread("DataCollectorService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        collectTouchData("Starting Data Collection Now");
        // call a new service handler. The service ID can be used to identify the service
        mDetector = new GestureDetectorCompat(getApplicationContext(),this);
        // Set the gesture detector as the double tap listener.
        mDetector.setOnDoubleTapListener(this);

        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    protected void collectTouchData(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                // run this code in the main thread
                // write the code to collect data
                //onTouchEvent() -> collect data and send to application server
                Intent rawTouchDataIntent = new Intent(getApplicationContext(),RawTouchData.class);
                startActivity(rawTouchDataIntent);
                Log.d(DEBUG_TAG,"Service running in the background.");
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.d(DEBUG_TAG,"Service destroyed in the background.");
        collectTouchData("Collect data");
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Intent rawTouchDataIntent = new Intent(getApplicationContext(),RawTouchData.class);
        startActivity(rawTouchDataIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        Log.d(DEBUG_TAG,"Gesture Performed"+gesture.toString());
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(DEBUG_TAG,"DoubltTap Performed"+e.toString());
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d(DEBUG_TAG,"DoubltTapEvent Performed"+e.toString());
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(DEBUG_TAG,"on scroll Performed"+e1.toString()+" event 2"+e2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(DEBUG_TAG,"on fling Performed"+e1.toString()+" event 2"+e2.toString());
        return true;
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
