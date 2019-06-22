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
import com.example.neighbourapplication.model.Incident;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IncidentController {
    private FirebaseFirestore db;
    private Context context;
    private Incident incident;
    private StorageReference storageReference;
    public IncidentController(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void setIncident(final Incident incident){

        this.incident = incident;
    }

    //get data
    public void getIncidents(final IncidentAdapter incidentAdapter){
        final HashMap<String, Incident> incidents = new HashMap<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Thread getIncidentThread = new Thread(){
            @Override
            public void run() {
                db.collection("incidents")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Incident incident = documentSnapshot.toObject(Incident.class);
                                    incident.setIncidentId(documentSnapshot.getId());
                                    System.out.println(incident.getIncidentId());
                                    System.out.println(incident.getIncidentName());
                                    incidents.put(incident.getIncidentId(), incident);

                                }
                                incidentAdapter.setIncidents(incidents);
                                incidentAdapter.notifyDataSetChanged();
                            }
                        });
            }
        };
        getIncidentThread.run();
    }

    //
    public void getIncidents(){
        final HashMap<String, Incident> incidents = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("incidents")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Incident incident = documentSnapshot.toObject(Incident.class);
                            incident.setIncidentId(documentSnapshot.getId());
                            incidents.put(incident.getIncidentId(),incident);
                        }
                        ArrayList<Incident> incidentsArr = new ArrayList<>(incidents.values());
                        Toast.makeText(context, incidentsArr.get(0).getIncidentId(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //get incident
    public void getIncident(String id, final ImageView bitmap, final Incident incident){
        final HashMap<String, Incident> incidents = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("incidents").document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Incident retrievedIncident = documentSnapshot.toObject(Incident.class);
                        incident.setPhoto(retrievedIncident.getPhoto());
                        incident.setIncidentId(documentSnapshot.getId());
                        incident.setIncidentName(retrievedIncident.getIncidentName());
                        incident.setDescription(retrievedIncident.getDescription());
                        incident.setDate(retrievedIncident.getDate());
                        incident.setTime(retrievedIncident.getTime());

                        System.out.println(retrievedIncident.getIncidentName());
                        System.out.println(retrievedIncident.getPhoto());
                        //masukkan method untuk refresh view etc
                        new DownloadImageTask(bitmap).execute(incident.getPhoto());
                    }
                }
            }
        });

    }

    //get map location
    public void getIncidentsLocation(final GoogleMap googleMap){
        final HashMap<String, Marker> markers = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection("incidents")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Incident incident = documentSnapshot.toObject(Incident.class);
                            incident.setIncidentId(documentSnapshot.getId());
                            Marker marker = googleMap.addMarker(getPlaceMarker(incident));
                            marker.setTag(incident.getIncidentId());
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            markers.put(incident.getIncidentId(), marker);
                        }
                    }
        });
    }

    public void insertIncident(final Incident incident, Uri bitmapUri, Activity activity){
        storageReference = FirebaseStorage.getInstance().getReference();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh.mm.ss");

        storageReference = storageReference.child("images/"+formatter.format(date));
        final ProgressBar progressBar = activity.findViewById(R.id.progressUpload);
        progressBar.setVisibility(View.VISIBLE);
        if(bitmapUri!=null)
            storageReference.putFile(bitmapUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    incident.setPhoto(uri.toString());
                                    db.collection("incidents")
                                            .add(incident)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(context, "Incident reported successfully", Toast.LENGTH_SHORT).show();

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
                                                            params.put("message",incident.getIncidentName());
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
                                                    Toast.makeText(context, "Failed to add report..", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                });
        else
            db.collection("incidents")
                    .add(incident)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, "Incident reported successfully", Toast.LENGTH_SHORT).show();

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
                                    params.put("message",incident.getIncidentName());
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
                            Toast.makeText(context, "Failed to add report..", Toast.LENGTH_SHORT).show();
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


    public MarkerOptions getPlaceMarker(Incident incident){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(incident.getIncidentName());
        markerOptions.position(new LatLng(incident.getLocation().getLatitude(),incident.getLocation().getLongitude()));
        return markerOptions;
    }
    //to download image
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            System.out.println("Getting image");
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            System.out.println("Image retrieved");
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
