package com.example.qlove.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by qlove on 09.04.2016.
 */
public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private List<Trailer> data;

    public CustomAdapter(Context context, List<Trailer> data){
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
        TextView textview;
        View layout;
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = layoutInflater.inflate(R.layout.detail_list_item, null);
        }else{
            layout = convertView;
        }
        textview = (TextView) layout.findViewById(R.id.trailer_textview);
        textview.setText(data.get(position).getName());
        return layout;
    }
}
