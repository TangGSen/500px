package com.github.premnirmal.fivehundredpx.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.premnirmal.fivehundredpx.R;
import com.github.premnirmal.fivehundredpx.downloader.DownloaderModel;
import com.github.premnirmal.fivehundredpx.model.ImageCacher;
import com.github.premnirmal.fivehundredpx.model.Photo;

import java.util.List;

/**
 * Created by premnirmal on 12/3/14.
 */
public class ImageAdapter extends android.widget.BaseAdapter {

    private List<Photo> photoList;
    private final ImageCacher imageCacher;
    private final DownloaderModel downloaderModel;

    public ImageAdapter(List<Photo> photoList, DownloaderModel downloaderModel) {
        this.photoList = photoList;
        this.downloaderModel = downloaderModel;
        this.imageCacher = new ImageCacher();
    }

    public void refresh(List<Photo> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Photo getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        final Photo photo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.thumbnail_view,null);
        }

        final ImageView image = (ImageView) convertView.findViewById(R.id.image);
        final ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        final ProgressBar progress = (ProgressBar) convertView.findViewById(R.id.progress);

        if(downloaderModel.isCurrentlyBeingDownloaded(photo.hashCode())) {
            progress.setVisibility(View.VISIBLE);
            icon.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            if (photo.isDownloaded()) {
                icon.setVisibility(View.VISIBLE);
            } else {
                icon.setVisibility(View.GONE);
            }
        }

        imageCacher.loadImage(image, photo.image_url);

        return convertView;
    }
}
