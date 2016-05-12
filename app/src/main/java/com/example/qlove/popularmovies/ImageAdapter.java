package com.example.qlove.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by qlove on 06.04.2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<MovieDataHolder> data;

    public ImageAdapter(Context context, List<MovieDataHolder> data){
        this.mContext = context;
        this.data = data;
    }



    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        View layout;
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = layoutInflater.inflate(R.layout.grid_item, null);
        }else{
            layout = convertView;
        }
        imageView = (ImageView) layout.findViewById(R.id.picture);
        Picasso.with(mContext).load(data.get(position).getPosterPath()).into(imageView);

        return layout;
    }
}
