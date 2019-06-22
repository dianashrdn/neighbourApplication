package com.example.neighbourapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neighbourapplication.controller.IncidentController;
import com.example.neighbourapplication.model.Incident;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.ViewHolder>{
    private ArrayList<Incident> incidents;
    private Context context;



    public IncidentAdapter() {
    }
    public IncidentAdapter(Context context){
        incidents = new ArrayList<>();
        this.context = context;
    }

    public void setIncidents(HashMap<String, Incident> incidents){
        this.incidents = new ArrayList<>(incidents.values());
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.incident_card,parent,false);
        return new ViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Incident incident = incidents.get(position);
        IncidentController incidentController = new IncidentController(context);
        holder.progressBar.setVisibility(View.VISIBLE);
        System.out.println("From adapter: "+incident.getIncidentId());
        System.out.println("From adapter: "+incident.getIncidentName());
        holder.description.setText(incident.getDescription());
        holder.incidentName.setText(incident.getIncidentName());
        //holder.photo.set

        holder.dateTime.setText(incident.getTime()+","+
                                incident.getDate());
        holder.showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapDialog mapDialog = new MapDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("incident", incident);
                mapDialog.setArguments(bundle);
                mapDialog.show(((FragmentActivity)context).getSupportFragmentManager(),"Map Dialog");
            }
        });
        //FireStore only stores image url, get image url, then download and display
        new DownloadImageTask(holder.photo, holder.progressBar).execute(incident.getPhoto());

    }

    @Override
    public int getItemCount() {
        return incidents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView incidentName, dateTime, description;
        public ImageView photo;
        public ProgressBar progressBar;
        public Button showMap;
        public ViewHolder(View itemView) {
            super(itemView);
            showMap = itemView.findViewById(R.id.btnShowLocation);
            progressBar = itemView.findViewById(R.id.progressBar);
            incidentName = itemView.findViewById(R.id.cardIncidentName);
            dateTime = itemView.findViewById(R.id.cardDateTime);
            description= itemView.findViewById(R.id.cardDescription);
            photo = itemView.findViewById(R.id.cardOwnerPhoto);
        }
    }

    //download Image
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressBar progressBar;

        public DownloadImageTask(ImageView bmImage, ProgressBar progressBar) {
            this.bmImage = bmImage;
            this.progressBar = progressBar;
        }

        protected Bitmap doInBackground(String... urls) {
            System.out.println("Getting image");
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                mIcon11 = Bitmap.createScaledBitmap(mIcon11,150, 150, true);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            System.out.println("Image retrieved");
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            progressBar.setVisibility(View.GONE);
        }
    }

}
