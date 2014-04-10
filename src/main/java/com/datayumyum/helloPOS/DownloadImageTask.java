package com.datayumyum.helloPOS;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "com.datayumyum.pos.DownloadImageTask";
    private ImageButton imageButton;
    private String url;

    public DownloadImageTask(ImageButton imageButton) {
        this.imageButton = imageButton;
    }

    protected Bitmap doInBackground(String... urls) {
        url = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        imageButton.setImageBitmap(result);
        Log.d(TAG, "loaded " + url);
    }
}