package com.tehilafi.ama.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.tehilafi.ama.R;

import java.util.ArrayList;

public class ListViewAdapteMy extends ArrayAdapter<ListView_item_my> {

    ArrayList<ListView_item_my> arrayList = null;
    private Context context;
    private int mResource;


    public ListViewAdapteMy(@NonNull Context context, int resource, @NonNull ArrayList<ListView_item_my> arrayList) {
        super( context, resource, arrayList );
        this.context = context;
        this.mResource = resource;
        this.arrayList = arrayList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListView_item_my listView_item_my = arrayList.get(position);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView imageprofile = convertView.findViewById( R.id.profileuserID);
        TextView textuserName = convertView.findViewById(R.id.userNameID);
        TextView textdate = convertView.findViewById(R.id.dateID);
        TextView textnumQ = convertView.findViewById(R.id.numQID);
        TextView textlocation = convertView.findViewById(R.id.locationID);
        ImageView imageans = convertView.findViewById( R.id.with_ansID);
        ImageView imagestar = convertView.findViewById( R.id.starID);
        TextView textmark = convertView.findViewById(R.id.markID);

        Picasso.with(context).load(listView_item_my.Image).into(imageprofile);
        textuserName.setText(getItem(position).getUserName());
        textdate.setText(getItem(position).getDate());
        textnumQ.setText(getItem(position).getNumQ());
        textlocation.setText(getItem(position).getLocation());
        imageans.setImageResource(getItem(position).getWith_ans());
        imagestar.setImageResource(getItem(position).getStar());
        textmark.setText(getItem(position).getMark());

        return convertView;
    }
}
