//package com.tehilafi.ama;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import java.util.List;
//
//public class ListAdapter extends ArrayAdapter {
//
//    private Activity mContext;
//    List<Question> questionsList;
//
//    public ListAdapter(Activity mContext, List<Question> questionsList) {
//        super( mContext, R.layout.my_listview_item, questionsList );
//        this.mContext = mContext;
//        this.questionsList = questionsList;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        LayoutInflater inflater = mContext.getLayoutInflater();
//        View listItemView = inflater.inflate(R.layout.my_listview_item, null,true );
//
//        TextView userName = listItemView.findViewById( R.id.txvLocationID );
//        TextView password = listItemView.findViewById( R.id.txvtitleID );
//
//        Question question = questionsList.get(position);
//
//        userName.setText(question.getLocation());
//        password.setText(question.getQuestion());
//
//        return listItemView;
//
//    }
//}
