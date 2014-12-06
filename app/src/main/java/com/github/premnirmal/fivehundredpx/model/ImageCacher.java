package com.github.premnirmal.fivehundredpx.model;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.github.premnirmal.fivehundredpx.downloader.BitmapFetchUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by premnirmal on 12/5/14.
 */
public class ImageCacher {

    Map<String,ImageLoader> requestCache = new HashMap<String, ImageLoader>();

    public void loadImage(ImageView view, String url) {
        fetchImage(url,view);
    }

    private void fetchImage(final String url, final ImageView imageView) {
        if (imageView.getTag() != null) {
            final ImageLoadedListener loadedListener = (ImageLoadedListener) imageView.getTag();
            if (requestCache.containsKey(url)) {
                requestCache.get(url).removeListener(loadedListener);
            }
        }

        imageView.setTag(new ImageLoadedListener() {
            @Override
            public void imageLoaded(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                imageView.setTag(null);
            }
        });

        if (!requestCache.containsKey(url)) {
            final ImageLoader imageLoader = new ImageLoader();
            imageLoader.addListener((ImageLoadedListener) imageView.getTag());
            imageLoader.execute(url);
            requestCache.put(url, imageLoader);
        } else {
            requestCache.get(url).addListener((ImageLoadedListener) imageView.getTag());
        }
    }


    class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        private final Set<ImageLoadedListener> listeners = new HashSet<>();

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            for (ImageLoadedListener listner : listeners) {
                listner.imageLoaded(bitmap);
            }
            listeners.clear();
        }

        void addListener(ImageLoadedListener listener) {
            listeners.add(listener);
        }

        void removeListener(ImageLoadedListener listener) {
            listeners.remove(listener);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapFetchUtil.fetchBitmap(params[0]);
        }
    }
}
