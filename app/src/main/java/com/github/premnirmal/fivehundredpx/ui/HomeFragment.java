package com.github.premnirmal.fivehundredpx.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;

import com.github.premnirmal.fivehundredpx.FiveHundredPXApp;
import com.github.premnirmal.fivehundredpx.NetworkUtil;
import com.github.premnirmal.fivehundredpx.R;
import com.github.premnirmal.fivehundredpx.api.FiveHundredPxApi;
import com.github.premnirmal.fivehundredpx.downloader.DownloaderModel;
import com.github.premnirmal.fivehundredpx.downloader.ImageDownloadService;
import com.github.premnirmal.fivehundredpx.model.FeatureResponse;
import com.github.premnirmal.fivehundredpx.model.Photo;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by premnirmal on 12/3/14.
 */
public class HomeFragment extends Fragment {

    @Inject
    FiveHundredPxApi api;
    @Inject
    DownloaderModel downloaderModel;
    private AlertDialog alertDialog;
    private ImageAdapter imageAdapter;
    private String currentFeature;
    private Subscription subscription;

    private final DownloaderModel.StateChangedListener
            stateChangedListener = new DownloaderModel.StateChangedListener() {
        @Override
        public void downloadStateChanged(int id) {
            imageAdapter.notifyDataSetChanged();
        }
    };

    private final BroadcastReceiver downloadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Photo detail = intent.getParcelableExtra(Photo.TAG);
            if (detail.isDownloaded()) {
                imageAdapter.notifyDataSetChanged();
            }
        }
    };


    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Photo photo = imageAdapter.getItem(position);
            if (!downloaderModel.isCurrentlyBeingDownloaded(photo.hashCode())) {
                if (photo.isDownloaded()) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(photo.getFile()), "image/png");
                    startActivity(intent);
                    return;
                }

                if (NetworkUtil.haveNetworkConnection(getActivity())) {
                    final Intent intent = new Intent(getActivity(), ImageDownloadService.class);
                    intent.putExtra(Photo.TAG, photo);
                    getActivity().startService(intent);
                } else {
                    makeDialog("No network connection!");
                }
            } else {
                final Intent stopIntent = new Intent(getActivity(), ImageDownloadService.class);
                stopIntent.putExtra(ImageDownloadService.stop_id, photo.id);
                getActivity().startService(stopIntent);
            }
        }
    };
    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Photo photo = imageAdapter.getItem(position);
            if (photo.isDownloaded()) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Photo \'" + photo.name + "\'")
                        .setMessage("Delete?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                photo.getFile().delete();
                                imageAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FiveHundredPXApp) getActivity().getApplicationContext()).inject(this);

        final IntentFilter intentFilter = new IntentFilter(ImageDownloadService.BROADCAST_ACTION);
        getActivity().registerReceiver(downloadedReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.home_fragment, container, false);
        final GridView gridView = (GridView) layout.findViewById(R.id.grid);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setAdapter(imageAdapter = new ImageAdapter(new ArrayList<Photo>(), downloaderModel));
        gridView.setOnItemClickListener(onItemClickListener);
        gridView.setOnItemLongClickListener(longClickListener);

        final Spinner spinner = (Spinner) layout.findViewById(R.id.feature);
        spinner.setAdapter(new FeatureAdapter());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPhotos(currentFeature = FeatureAdapter.featureList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getPhotos(currentFeature = FeatureAdapter.featureList.get(0));

        downloaderModel.registerStateChangedListener(stateChangedListener);

        return layout;
    }

    private void getPhotos(String feature) {
        imageAdapter.refresh(new ArrayList<Photo>());
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (NetworkUtil.haveNetworkConnection(getActivity())) {
            subscription = api.getPhotos(feature).doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    makeDialog(throwable.getMessage());
                    retry();
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<FeatureResponse>() {
                        @Override
                        public void call(FeatureResponse response) {
                            if (alertDialog != null && alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                            imageAdapter.refresh(response.photos);
                        }
                    });
        } else {
            makeDialog("No network connection!");
        }
    }

    private void retry() {
        getPhotos(currentFeature);
    }

    private void makeDialog(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setTitle("Error")
                        .setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                retry();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(downloadedReceiver);
        downloaderModel.unregisterStateChangedListener(stateChangedListener);
    }
}
