package com.github.premnirmal.fivehundredpx.downloader;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseBooleanArray;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by premnirmal on 12/6/14.
 */
public final class DownloaderModel {

    public interface StateChangedListener {
        void downloadStateChanged(int id);
    }


    private volatile SparseBooleanArray currentlyDownloading = new SparseBooleanArray();
    private final Set<StateChangedListener> listeners = new HashSet<>();

    private final Handler handler = new Handler(Looper.getMainLooper());

    public boolean isCurrentlyBeingDownloaded(final int id) {
        return currentlyDownloading.get(id);
    }

    public void registerStateChangedListener(StateChangedListener listener) {
        listeners.add(listener);
    }

    public void unregisterStateChangedListener(StateChangedListener listener) {
        listeners.remove(listener);
    }

    void setCurrentlyDownloading(final int id) {
        currentlyDownloading.put(id, true);
        fireListeners(id);
    }

    void setDownloadFinished(final int id) {
        currentlyDownloading.put(id, false);
        fireListeners(id);
    }

    private void fireListeners(final int id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (StateChangedListener listener : listeners) {
                    listener.downloadStateChanged(id);
                }
            }
        });

    }
}
