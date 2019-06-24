package com.example.neighbourapplication.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.neighbourapplication.IncidentAdapter;
import com.example.neighbourapplication.R;
import com.example.neighbourapplication.RegisterActivity;
import com.example.neighbourapplication.WatchProgrammeAdapter;
import com.example.neighbourapplication.model.Incident;
import com.example.neighbourapplication.model.User;
import com.example.neighbourapplication.model.WatchProgramme;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WatchProgrammeController {
    private FirebaseFirestore db;
    private Context context;
    private WatchProgramme watchProgramme;
    public WatchProgrammeController(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void setWatchProgramme(final WatchProgramme watchProgramme){

        this.watchProgramme = watchProgramme;
    }

    //get data
    public void getWatchProgrammes(final WatchProgrammeAdapter watchProgrammeAdapter){
        final HashMap<String, WatchProgramme> watchProgrammes = new HashMap<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Thread getWatchProgrammeThread = new Thread(){
            @Override
            public void run() {
                db.collection("watchProgrammes")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    WatchProgramme watchProgramme = documentSnapshot.toObject(WatchProgramme.class);
                                    watchProgramme.setWatchId(documentSnapshot.getId());
                                    watchProgrammes.put(watchProgramme.getWatchId(), watchProgramme);

                                }
                                watchProgrammeAdapter.setWatchProgrammes(watchProgrammes);
                                watchProgrammeAdapter.notifyDataSetChanged();
                            }
                        });
            }
        };
        getWatchProgrammeThread.run();
    }
    //
    public void getWatchProgrammes(){
        final HashMap<String, WatchProgramme> watchProgrammes = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("wactProgrammes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            WatchProgramme watchProgramme = documentSnapshot.toObject(WatchProgramme.class);
                            watchProgramme.setWatchId(documentSnapshot.getId());
                            watchProgrammes.put(watchProgramme.getWatchId(),watchProgramme);
                        }
                        ArrayList<WatchProgramme> watchProgrammeArr = new ArrayList<>(watchProgrammes.values());
                    }
                });
    }

    //get incident
    public void getWatchProgramme(String id, final WatchProgramme watchProgramme){
        final HashMap<String, WatchProgramme> watchProgrammes = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("watchProgrammes").document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        WatchProgramme retrievedWatchProgramme = documentSnapshot.toObject(WatchProgramme.class);
                        watchProgramme.setWatchId(documentSnapshot.getId());
                        watchProgramme.setProgrammeName(retrievedWatchProgramme.getProgrammeName());
                        watchProgramme.setUsername(retrievedWatchProgramme.getUsername());
                        watchProgramme.setDescription(retrievedWatchProgramme.getDescription());
                        watchProgramme.setDateStart(retrievedWatchProgramme.getDateStart());
                        watchProgramme.setDateEnd(retrievedWatchProgramme.getDateEnd());

                        System.out.println(retrievedWatchProgramme.getProgrammeName());
                    }
                }
            }
        });

    }

    public void insertWatchProgramme(final WatchProgramme watchProgramme, Activity activity){

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh.mm.ss");

        final ProgressBar progressBar = activity.findViewById(R.id.progressUpload);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("watchProgrammes")
                .add(watchProgramme)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Activity successfully added!", Toast.LENGTH_SHORT).show();

                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url ="https://aesuneus.000webhostapp.com/neighbourservice.php";

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        System.out.println("Notified");
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Error notification");
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String ,String> params = new HashMap<String,String>();
                                params.put("message",watchProgramme.getProgrammeName());
                                return params;
                            }
                        };

                        queue.add(stringRequest);



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Failed to add activity..", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /*public static void getPlace(String placeId, final PlaceInfoFragment placeInfoFragment) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("places");
        Query query = databaseReference.orderByChild("placeId").startAt(placeId).endAt(placeId);
        ChildEventListener childEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Incident incident = dataSnapshot.getValue(Incident.class);
                placeInfoFragment.setPlace(incident);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/


}
