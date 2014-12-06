package com.github.premnirmal.fivehundredpx;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Created by premnirmal on 12/2/14.
 */
public class FiveHundredPXApp extends Application {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(new AppModule(this));
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}
