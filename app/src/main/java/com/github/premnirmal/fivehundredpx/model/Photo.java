package com.github.premnirmal.fivehundredpx.model;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;

/**
 * Created by premnirmal on 12/5/14.
 */
public class Photo implements Parcelable {

    public static final Parcelable.Creator<Photo> CREATOR
            = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public static final String TAG = "IMAGE";
    private static File directory;

    public long id;
    public String name;
    public String description;
    public String image_url;
    private transient File mFile;

    public Photo() {

    }

    public Photo(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        image_url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(image_url);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Photo && this.id == ((Photo) o).id;
    }

    @Override
    public int hashCode() {
        return (int) (id - 999);
    }

    private static File storageDirectory() {
        if (directory == null) {
            directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "500px");
            if (!directory.mkdirs()) {
                Log.e(TAG, directory.getPath() + " directory exists or cannot be created!");
            }
        }
        return directory;
    }

    private String getFileName() {
        return name + "_" + id + ".png";
    }

    public File getFile() {
        if (mFile == null) {
            mFile = new File(storageDirectory(), getFileName());
        }
        return mFile;
    }

    public boolean isDownloaded() {
        return getFile().exists();
    }
}
