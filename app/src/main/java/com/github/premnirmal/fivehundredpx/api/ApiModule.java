package com.github.premnirmal.fivehundredpx.api;

import com.github.premnirmal.fivehundredpx.downloader.ImageDownloadService;
import com.github.premnirmal.fivehundredpx.ui.HomeFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

/**
 * Created by premnirmal on 12/3/14.
 */
@Module(
        library = true,
        complete = false,
        injects = {
                HomeFragment.class, ImageDownloadService.class
        }
)
public class ApiModule {

    public static final String BASE_URL = "https://api.500px.com/v1";

    @Provides @Singleton
    FiveHundredPxApi provideApi() {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .build();
        return restAdapter.create(FiveHundredPxApi.class);
    }

}
