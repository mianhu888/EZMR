package com.example.ezmr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SonglistAdapter extends ArrayAdapter<Song> {
    Context context;
    int resource;
    List<Song> songs;

    public SonglistAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource, objects);
        this.resource=resource;
        this.context=context;
        this.songs=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(context).inflate(resource,parent,false);  //创建视图

        TextView tvNameAdaptor=convertView.findViewById(R.id.tv_name_adaptor);  //把数据传进来，设置文本
        List<ImageView> starList = new ArrayList<>();
        starList.add((ImageView) convertView.findViewById(R.id.iv_star3_adaptor));
        starList.add((ImageView) convertView.findViewById(R.id.iv_star2_adaptor));
        starList.add((ImageView) convertView.findViewById(R.id.iv_star1_adaptor));

        Song song=songs.get(position);
        tvNameAdaptor.setText(song.getName());
        for (int i = 0; i<song.getStar(); i++){
            starList.get(i).setImageResource(R.drawable.star);
            starList.get(i).setVisibility(View.VISIBLE);
        }
        return convertView;
    }

}

