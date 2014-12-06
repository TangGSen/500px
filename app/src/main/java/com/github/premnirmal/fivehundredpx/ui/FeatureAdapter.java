package com.github.premnirmal.fivehundredpx.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by premnirmal on 12/5/14.
 */
public class FeatureAdapter extends BaseAdapter {

    public static final List<String> featureList = new ArrayList<String>() {
        {
            add("popular");
            add("highest_rated");
            add("upcoming");
            add("fresh_today");
            add("fresh_yesterday");
            add("fresh_week");
        }
    };

    @Override
    public int getCount() {
        return featureList.size();
    }

    @Override
    public String getItem(int position) {
        return featureList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        if (convertView == null) {
            convertView = new TextView(context);
        }
        ((TextView) convertView).setText(getItem(position));
        convertView.setPadding(20,20,20,20);
        ((TextView) convertView).setGravity(Gravity.CENTER);
        return convertView;
    }
}
