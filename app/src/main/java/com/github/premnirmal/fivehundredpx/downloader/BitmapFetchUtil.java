package com.github.premnirmal.fivehundredpx.downloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by premnirmal on 12/5/14.
 */
public final class BitmapFetchUtil {

    public static Bitmap fetchBitmap(String src) {
        Bitmap bitmap = null;
        URL url = null;
        try {
            url = new URL(src);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            final InputStream is = url.openStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
