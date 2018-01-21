package com.visual.android.arkangel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.suke.widget.SwitchButton;

import java.util.List;

/**
 * Created by RamiK on 1/20/2018.
 */

public class HomeAdapter extends ArrayAdapter<Path> {

    private List<Path> paths;
    private Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public HomeAdapter(Context context, List<Path> paths) {
        super(context, 0, paths);
        this.paths = paths;
        this.context = context;
        sharedPref = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
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
            TextView mCodeText = convertView.findViewById(R.id.code_text);
            SwitchButton mSwitchButton = convertView.findViewById(R.id.switch_button);

            mHomeText.setText(path.getHome().getName());
            mDestText.setText(path.getDestination().getName());
            mCodeText.setText(path.getId());

            String activePath = sharedPref.getString("active-path", null);
            if (activePath != null && activePath.equals(path.getId())) {
                mSwitchButton.setChecked(true);
            } else {
                mSwitchButton.setChecked(false);
            }

            mSwitchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if (isChecked) {
                        editor.putString("active-path", path.getId());
                        editor.commit();
                    } else {
                        editor.remove("active-path");
                        editor.commit();
                    }
                    notifyDataSetChanged();
                }
            });

        }

        return convertView;
    }

}
