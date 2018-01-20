package com.visual.android.arkangel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.List;

/**
 * Created by RamiK on 1/20/2018.
 */

public class HomeAdapter extends ArrayAdapter<Path> {

    private List<Path> paths;
    private Context context;

    public HomeAdapter(Context context, List<Path> paths) {
        super(context, 0, paths);
        this.paths = paths;
        this.context = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_home, parent, false);
        }

        final Path path = paths.get(position);

        if (path != null) {

            TextView mHomeText = convertView.findViewById(R.id.home_text);
            TextView mDestText = convertView.findViewById(R.id.dest_text);

            mHomeText.setText(path.getHome().getName());
            mDestText.setText(path.getDestination().getName());

        }

        return convertView;
    }

}
