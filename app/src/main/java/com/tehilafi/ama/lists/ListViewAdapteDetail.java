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

    private static final String LOG_TAG = ListViewAdapteDetail.class.getSimpleName();
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
        View listItemView = convertView;
        if(listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(mResource,parent,false);
        ListView_item_detail currentDetail = getItem(position);

        TextView textuserName = listItemView.findViewById(R.id.userNameID);
        TextView textdate = listItemView.findViewById(R.id.dateID);
        TextView textanswer = listItemView.findViewById(R.id.text_answerID);
        TextView numLike =  listItemView.findViewById(R.id.numLikeID);
        TextView numDislike =  listItemView.findViewById(R.id.numDislikeID);

        textuserName.setText(currentDetail.getUserName());
        textdate.setText(currentDetail.getDate());
        textanswer.setText(currentDetail.getContent());
        numLike.setText(currentDetail.getNumLike());
        numDislike.setText(currentDetail.getNumDislike());

        ImageView imageViewprofile = (ImageView) listItemView.findViewById(R.id.profileuserID);
        imageViewprofile.setImageResource(currentDetail.getImage());

        return listItemView;

    }
}
