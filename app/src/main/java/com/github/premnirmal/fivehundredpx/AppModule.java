package com.github.premnirmal.fivehundredpx;

import android.app.Application;

import com.github.premnirmal.fivehundredpx.api.ApiModule;
import com.github.premnirmal.fivehundredpx.downloader.DownloaderModel;
import com.github.premnirmal.fivehundredpx.downloader.ImageDownloadService;
import com.github.premnirmal.fivehundredpx.ui.HomeFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by premnirmal on 12/2/14.
 */
@Module(
        includes = {
                ApiModule.class
        },
        injects = {
                HomeFragment.class, ImageDownloadService.class
        },
        library = true,
        complete = false
)
public class AppModule {

    private final Application app;
    private static final String TAG = "500px";

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides
    Application provideApplicationContext() {
        return app;
    }

    @Provides @Singleton
    DownloaderModel provideDownloaderModel() {
        return new DownloaderModel();
    }

}
