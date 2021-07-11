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

public class ListViewAdapte extends ArrayAdapter<ListView_item> {

    ArrayList<ListView_item> arrayList;
    private Context context;
    private int mResource;

    public ListViewAdapte(@NonNull Context context, int resource, @NonNull ArrayList<ListView_item> arrayList) {
        super( context, resource, arrayList );
        this.context = context;
        this.mResource = resource;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListView_item listView_item = arrayList.get(position);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView imageprofile = convertView.findViewById( R.id.profileuserID);
        TextView textuserName = convertView.findViewById(R.id.userNameID);
        TextView textdate = convertView.findViewById(R.id.dateID);
        TextView textnumQ = convertView.findViewById(R.id.numQID);
        TextView textquestion = convertView.findViewById(R.id.questionID);
        ImageView imageans = convertView.findViewById( R.id.with_ansID);
        ImageView imagestar = convertView.findViewById( R.id.starID);

        Picasso.with(context).load(listView_item.Image).into(imageprofile);
        textuserName.setText(getItem(position).getUserName());
        textdate.setText(getItem(position).getDate());
        textnumQ.setText(getItem(position).getNumQ());
        textquestion.setText(getItem(position).getQuestion());
        imageans.setImageResource(getItem(position).getWith_ans());
        imagestar.setImageResource(getItem(position).getStar());




        return convertView;
    }
}
