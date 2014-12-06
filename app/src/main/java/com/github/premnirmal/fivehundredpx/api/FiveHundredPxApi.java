package com.github.premnirmal.fivehundredpx.api;

import com.github.premnirmal.fivehundredpx.model.FeatureResponse;
import com.github.premnirmal.fivehundredpx.model.PhotoResponse;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by premnirmal on 12/5/14.
 */
public interface FiveHundredPxApi {

    static final String CONSUMER_KEY = "fWWpXP01BIbIOB9sSvFIZHqHCiwvajXovpiOBGv6";
    public static String FEATURE = "editors";

    @GET("/photos/" + "?consumer_key=" + CONSUMER_KEY + "&image_size=2")
    Observable<FeatureResponse> getPhotos(@Query("feature") String feature);

    @GET("/photos/{id}" + "?consumer_key=" + CONSUMER_KEY + "&image_size=4")
    Observable<PhotoResponse> getPhotoById(@Path("id") long id);

}
