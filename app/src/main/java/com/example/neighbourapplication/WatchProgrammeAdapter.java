package com.example.neighbourapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neighbourapplication.model.WatchProgramme;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class WatchProgrammeAdapter extends  RecyclerView.Adapter<WatchProgrammeAdapter.ViewHolder>{
    private ArrayList<WatchProgramme> watchProgrammes;
    private Context context;



    public WatchProgrammeAdapter() {
    }
    public WatchProgrammeAdapter(Context context){
        watchProgrammes = new ArrayList<>();
        this.context = context;
    }

    public void setWatchProgrammes(HashMap<String, WatchProgramme> watchProgrammes){
        this.watchProgrammes = new ArrayList<>(watchProgrammes.values());
    }
    @Override
    public WatchProgrammeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.programme_card,parent,false);
        return new WatchProgrammeAdapter.ViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WatchProgramme watchProgramme = watchProgrammes.get(position);
        holder.description.setText("Activity description: "+watchProgramme.getDescription());
        holder.programmeName.setText("Activity name: "+watchProgramme.getProgrammeName());
        holder.username.setText("Activity added by: "+watchProgramme.getUsername());
        holder.dateStart.setText("Activity start date: "+watchProgramme.getDateStart());
        holder.dateEnd.setText("Activity end date: "+watchProgramme.getDateEnd());


    }

    @Override
    public int getItemCount() {
        return watchProgrammes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView programmeName, dateStart, dateEnd,  description, username;

        public ViewHolder(View itemView) {
            super(itemView);
            programmeName = itemView.findViewById(R.id.cardProgrammeName);
            dateStart = itemView.findViewById(R.id.cardDateStart);
            dateEnd = itemView.findViewById(R.id.cardDateEnd);
            description= itemView.findViewById(R.id.cardDescriptionP);
            username= itemView.findViewById(R.id.cardUsername);
        }
    }

}

