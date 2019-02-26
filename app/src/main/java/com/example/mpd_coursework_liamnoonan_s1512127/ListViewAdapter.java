package com.example.mpd_coursework_liamnoonan_s1512127;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<Earthquake> implements View.OnClickListener{

    private ArrayList<Earthquake> dataSet;
    Context mContext;
    // View lookup cache
    private static class ViewHolder {
        RelativeLayout container;
        TextView txtName;
        TextView txtType;
        ImageView info;
    }

    public ListViewAdapter(ArrayList<Earthquake> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Earthquake dataModel= getItem(position);

        switch (v.getId())
        {
            case R.id.item_info:
                Log.e("UserEvent", "Info button clicked for item " + dataModel.getTitle());
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Earthquake dataModel = getItem(position);

       // System.out.println(dataModel.toString());

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.container = convertView.findViewById(R.id.container);
            viewHolder.txtName = convertView.findViewById(R.id.title);
            viewHolder.txtType = convertView.findViewById(R.id.pubDate);
            viewHolder.info = convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }



        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        //System.out.println(dataModel.getColour());
        if(dataModel.getColour()!=null){
            if(dataModel.getColour().equals("green")) {
                viewHolder.container.setBackgroundColor(Color.GREEN);
            }
            if(dataModel.getColour().equals("orange")) {
                viewHolder.container.setBackgroundColor(Color.YELLOW);
            }
            if(dataModel.getColour().equals("red")) {
                viewHolder.container.setBackgroundColor(Color.RED);
            }
        }

        viewHolder.txtName.setText(dataModel.getTitle());
        viewHolder.txtType.setText("Magnitude: " + String.valueOf(dataModel.getMagnitude()));
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}