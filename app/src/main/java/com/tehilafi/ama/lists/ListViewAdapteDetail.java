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

public class ListViewAdapteDetail extends ArrayAdapter<ListView_item_detail> {

    private Context context;
    private int mResource;

    public ListViewAdapteDetail(@NonNull Context context, int resource, @NonNull ArrayList<ListView_item_detail> objects) {
        super( context, resource, objects );
        this.context = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView imageViewprofile = convertView.findViewById( R.id.profileuserID);
        TextView textuserName = convertView.findViewById(R.id.userNameID);
        TextView textdate = convertView.findViewById(R.id.dateID);
        TextView textanswer = convertView.findViewById(R.id.text_answerID);
        TextView numLike =  convertView.findViewById(R.id.numLikeID);
        TextView numDislike =  convertView.findViewById(R.id.numDislikeID);
        ImageView imageViewvideo = convertView.findViewById( R.id.add_videoID);
        ImageView imageViewimage = convertView.findViewById( R.id.add_picID);


//        imageView.setImageResource(getItem(position).getImage());
        textuserName.setText(getItem(position).getUserName());
        textdate.setText(getItem(position).getDate());
        textanswer.setText(getItem(position).getContent());
        numLike.setText(getItem(position).getNumLike());
        numDislike.setText(getItem(position).getNumDislike());
//        imageViewimage.setImageResource(getItem(position).getAdd_picID());
//        imageViewvideo.setImageResource(getItem(position).getAdd_videoID());




        return convertView;
    }
}
