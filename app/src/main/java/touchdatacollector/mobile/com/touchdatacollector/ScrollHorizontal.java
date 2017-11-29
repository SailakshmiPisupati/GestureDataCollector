package touchdatacollector.mobile.com.touchdatacollector;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Saila on 11/14/2017.
 */

public class ScrollHorizontal extends Activity {
    LinearLayout myGallery;
    private JSONArray strokeArray = null;
    private static final String DEBUG_TAG = "Gesture";
    private int strokeid = 0;
    private int pointid = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_horizontal);
        myGallery = (LinearLayout)findViewById(R.id.mygallery);

        ArrayList<String> listFile = getAllShownImagesPath(this);
        for(String s :listFile){
            File targetDirector = new File(s);
            myGallery.addView(insertPhoto(targetDirector.getAbsolutePath()));
        }

        HorizontalScrollView mScrollView = (HorizontalScrollView) findViewById(R.id.horiscroll);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(DEBUG_TAG,"onTouchEvent occurred");
//                int action = MotionEventCompat.getActionMasked(event);

                collectGestureData(event);  //Data collection function
                return false;
            }
        });
    }

    boolean collectGestureData(MotionEvent ev) {
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
            DataSender sender = new DataSender();
            sender.sendDataToServer(strokeArray);
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

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }
    View insertPhoto(String path){
        Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(1500, 1500));
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(1500, 1500));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bm);

        layout.addView(imageView);
        return layout;
    }

        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
            Bitmap bm = null;

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(path, options);

            return bm;
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float)height / (float)reqHeight);
                } else {
                    inSampleSize = Math.round((float)width / (float)reqWidth);
                }
            }

            return inSampleSize;
        }
}
