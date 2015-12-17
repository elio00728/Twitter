package com.example.jonathanplay.twitter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Tweet> {
    int layoutId;
    public MyAdapter(Context context, int resource, List<Tweet> objects) {
        super(context, resource, objects);
        layoutId = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet item = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(layoutId, parent, false);
        }
        TextView date = (TextView) convertView.findViewById(R.id.item_date);
        TextView contenu = (TextView) convertView.findViewById(R.id.item_contenu);
        TextView userName = (TextView) convertView.findViewById(R.id.item_userName);
        date.setText(item.getDate());
        contenu.setText(item.getContenu());
        userName.setText(item.getUserName());
        return convertView;
    }
}