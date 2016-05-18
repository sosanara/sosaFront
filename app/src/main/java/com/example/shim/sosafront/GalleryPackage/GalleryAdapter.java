package com.example.shim.sosafront.GalleryPackage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shim.sosafront.R;

import java.util.ArrayList;

//Adapter class extends with BaseAdapter and implements with OnClickListener
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private Activity activity;
    private ArrayList<String> data = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();

    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public GalleryAdapter(Activity a, ArrayList<String> d, ArrayList<String> date) {
        activity = a;

        data=d;
        this.date = date;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Create ImageLoader object to download and show image in list
        // Call ImageLoader constructor to initialize FileCache
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.from(parent.getContext()).inflate(R.layout.item_history, null);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(date.get(position));

        ImageView image = holder.image;

        imageLoader.DisplayImage(data.get(position), image);
        holder.itemView.setOnClickListener(new OnItemClickListener(position));
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView text;
        public ImageView image;

        public ViewHolder(View v) {
            super(v);
            text = (TextView) v.findViewById(R.id.text);
            image=(ImageView) v.findViewById(R.id.image);
        }
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            GalleryActivity sct = (GalleryActivity)activity;
            sct.onItemClick(mPosition);
        }
    }
}
