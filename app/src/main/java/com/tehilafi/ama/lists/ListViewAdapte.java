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

import com.tehilafi.ama.R;

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
        ImageView imageprofile = convertView.findViewById( R.id.profileuserID);
        TextView textuserName = convertView.findViewById(R.id.userNameID);
        TextView textdate = convertView.findViewById(R.id.dateID);
        TextView textquestion = convertView.findViewById(R.id.questionID);
        ImageView imageans = convertView.findViewById( R.id.with_ansID);

//        imageprofile.setImageResource(getItem(position).getImage());
        textuserName.setText(getItem(position).getUserName());
        textdate.setText(getItem(position).getDate());
        textquestion.setText(getItem(position).getQuestion());
//        imageans.setVisibility();

        return convertView;
    }
}
