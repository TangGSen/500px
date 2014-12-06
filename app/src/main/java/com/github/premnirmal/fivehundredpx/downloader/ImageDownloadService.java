package com.github.premnirmal.fivehundredpx.downloader;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;

import com.github.premnirmal.fivehundredpx.FiveHundredPXApp;
import com.github.premnirmal.fivehundredpx.api.FiveHundredPxApi;
import com.github.premnirmal.fivehundredpx.model.Photo;
import com.github.premnirmal.fivehundredpx.model.PhotoResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by premnirmal on 12/5/14.
 */
public class ImageDownloadService extends IntentService {

    public static final String BROADCAST_ACTION =
            "com.github.premnirmal.fivehundredpx.downloader.BROADCAST";

    public static final String stop_id = "STOP_ID";

    private static volatile Set<Long> stopList = new HashSet<>();

    @Inject
    FiveHundredPxApi api;
    @Inject
    DownloaderModel downloaderModel;

    public ImageDownloadService() {
        super("ImageDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ((FiveHundredPXApp) getApplicationContext()).inject(this);
        final Photo photo = intent.getParcelableExtra(Photo.TAG);
        if(photo != null) {
            if (!photo.isDownloaded()) {
                downloaderModel.setCurrentlyDownloading(photo.hashCode());
                downloadPhotoToDisk(photo);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final long id = intent.getExtras().getLong(stop_id);
        if (id != 0) {
            stopList.add(id);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadPhotoToDisk(Photo photo) {
        api.getPhotoById(photo.id).subscribe(new Action1<PhotoResponse>() {
            @Override
            public void call(PhotoResponse photoResponse) {
                final Photo photoDetail = photoResponse.photo;
                final Bitmap bitmap = BitmapFetchUtil.fetchBitmap(photoDetail.image_url);
                final File imageFile = photoDetail.getFile();
                if (bitmap != null) {
                    if (!imageFile.exists()) {
                        try {
                            final OutputStream os = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                            os.close();
                            sendPhotoBroadcast(photoDetail);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        sendPhotoBroadcast(photoDetail);
                    }
                }
            }
        });
    }

    private void sendPhotoBroadcast(Photo photoDetail) {
        if (stopList.contains(photoDetail.id)) {
            photoDetail.getFile().delete();
            stopList.remove(photoDetail.id);
        }
        downloaderModel.setDownloadFinished(photoDetail.hashCode());
        final Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(Photo.TAG, photoDetail);
        sendBroadcast(intent);
    }
}
