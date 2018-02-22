//package touchdatacollector.mobile.com.touchdatacollector;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.PixelFormat;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Toast;
//import android.view.View.OnTouchListener;
///**
// * Created by Saila on 11/6/2017.
// */
//
//public class HUD extends Service {
//    HUDView mView;
//    private static final String TAG = "Gesture";
//
//    @Override
//    public int onStartCommand(Intent intent, int flag, int startId){
//        Log.i(TAG, "Adding overlay. onStart method");
//        mView = new HUDView(this);
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//                PixelFormat.OPAQUE);
//        params.gravity = Gravity.RIGHT | Gravity.TOP;
//        params.setTitle("Load Average");
//        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
//        wm.addView(mView, params);
//        mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "Removing overlay. onclick method");
//                wm.removeView(mView);
//                mView = null;
//                onDestroy();
//            }
//        });
//        return START_STICKY;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
////    @Override
////    public void onCreate() {
////        super.onCreate();
////        Log.i(TAG, "Adding overlay. oncreate method");
////        Toast.makeText(getBaseContext(),"onCreate", Toast.LENGTH_LONG).show();
////        mView = new HUDView(this);
////        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
////                WindowManager.LayoutParams.WRAP_CONTENT,
////                WindowManager.LayoutParams.WRAP_CONTENT,
////                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
////                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
////                PixelFormat.OPAQUE);
////        params.gravity = Gravity.RIGHT | Gravity.TOP;
////        params.setTitle("Load Average");
////        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
////        wm.addView(mView, params);
////    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.i(TAG, "Adding overlay. onDestroy method");
//        if(mView != null)
//        {
//            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
//            mView = null;
//        }
//        Intent hudIntent = new Intent(getApplicationContext(),HUD.class);
//        startService(hudIntent);
//    }
//}
//
//class HUDView extends ViewGroup {
//    private Paint mLoadPaint;
//    private static final String TAG = "Gesture";
//
//
//    public HUDView(Context context) {
//        super(context);
//        Toast.makeText(getContext(),"HUDView", Toast.LENGTH_LONG).show();
//
//        mLoadPaint = new Paint();
//        mLoadPaint.setAntiAlias(true);
//        mLoadPaint.setTextSize(10);
//        mLoadPaint.setARGB(123, 123, 123, 0);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.drawText("Hello World", 5, 15, mLoadPaint);
//    }
//
//    @Override
//    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG,"Event being fired "+event.toString());
//        super.onTouchEvent(event);
//        return true;
//    }
//}