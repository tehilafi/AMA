package com.tehilafi.ama;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListViewAdapte extends ArrayAdapter<ListView_item> {

    private Context context;
    private int mResource;

    public ListViewAdapte(@NonNull Context context, int resource, @NonNull ArrayList<ListView_item> objects) {
        super( context, resource, objects );
        this.context = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView imageView = convertView.findViewById(R.id.profileuserID);
        TextView textuserName = convertView.findViewById(R.id.userNameID);
        TextView textdate = convertView.findViewById(R.id.dateID);
        TextView textlocation = convertView.findViewById(R.id.locationID);

        imageView.setImageResource(getItem(position).getImage());
        textuserName.setText(getItem(position).getUserName());
        textdate.setText(getItem(position).getDate());
        textlocation.setText(getItem(position).getLocation());

        return convertView;
    }
}
