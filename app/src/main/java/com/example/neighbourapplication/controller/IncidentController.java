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
import com.example.neighbourapplication.sqlite.IncidentDB;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.text.SimpleDateFormat;
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
//        IncidentDB localDB = new IncidentDB(context);
//        localDB.fnGetIncidents(IncidentDB.tblNameIncident, incidents);
//        localDB.fnGetIncidents(IncidentDB.tblNameIncidentToUpload, incidents);
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

    //get map location
    public void getIncidentsLocation(final GoogleMap googleMap){
        final HashMap<String, Marker> markers = new HashMap<>();
//        final HashMap<String, Incident> incidents = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        IncidentDB localDB = new IncidentDB(context);
//        localDB.fnGetIncidents(IncidentDB.tblNameIncident, incidents);
//        localDB.fnGetIncidents(IncidentDB.tblNameIncidentToUpload, incidents);

//        for (Incident incident : incidents.values()){
//            Marker marker = googleMap.addMarker(getPlaceMarker(incident));
//            marker.setTag(incident.getIncidentId());
//            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            markers.put(incident.getIncidentId(), marker);
//        }

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

    public void insertIncident(final Incident incident, final Uri bitmapUri, Activity activity) {
        storageReference = FirebaseStorage.getInstance().getReference();

        final Date date = new Date();
        final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh.mm.ss");
        final ProgressBar progressBar = activity.findViewById(R.id.progressUpload);
        progressBar.setVisibility(View.VISIBLE);
        final IncidentDB localDB = new IncidentDB(context);


        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://aesuneus.000webhostapp.com/neighbourservice.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.matches("SUCCESS")) {
                            storageReference = storageReference.child("images/" + formatter.format(date));
                            if (bitmapUri != null)
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
                                                                        String url = "https://aesuneus.000webhostapp.com/neighbourservice.php";

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
                                                                        }) {
                                                                            @Override
                                                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                                                Map<String, String> params = new HashMap<String, String>();
                                                                                params.put("message", incident.getIncidentName());
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
                                                localDB.fnDeleteIncident(incident.getIncidentId(), IncidentDB.tblNameIncidentToUpload);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(context, "Incident reported successfully", Toast.LENGTH_SHORT).show();

                                                RequestQueue queue = Volley.newRequestQueue(context);
                                                String url = "https://aesuneus.000webhostapp.com/neighbourservice.php";

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
                                                }) {
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                        Map<String, String> params = new HashMap<String, String>();
                                                        params.put("message", incident.getIncidentName());
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

                        } else {
                            localDB.fnInsertIncident(incident, bitmapUri, IncidentDB.tblNameIncidentToUpload);
                            Toast.makeText(context, "No Internet connection detected, adding to local database.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                localDB.fnInsertIncident(incident, bitmapUri, IncidentDB.tblNameIncidentToUpload);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("testConnection", "");
                return params;
            }
        };

        queue.add(stringRequest);


    }

    public void insertIncident(final HashMap<String, Incident> incidents, Activity activity) {
        storageReference = FirebaseStorage.getInstance().getReference();

        final Date date = new Date();
        final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh.mm.ss");
        final ProgressBar progressBar = activity.findViewById(R.id.progressUpload);
        progressBar.setVisibility(View.VISIBLE);
        final IncidentDB localDB = new IncidentDB(context);

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://aesuneus.000webhostapp.com/neighbourservice.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.matches("SUCCESS")) {
                            for (final Incident incident : incidents.values()) {
                                Uri bitmapUri = null;
                                if (incident.getPhoto() != null)
                                    bitmapUri = Uri.parse(incident.getPhoto());


                                storageReference = storageReference.child("images/" + formatter.format(date));

                                if (bitmapUri != null)
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
                                                                            localDB.fnDeleteIncident(incident.getIncidentId(), IncidentDB.tblNameIncidentToUpload);

                                                                            Toast.makeText(context, "Incident reported successfully", Toast.LENGTH_SHORT).show();
                                                                            RequestQueue queue = Volley.newRequestQueue(context);
                                                                            String url = "https://aesuneus.000webhostapp.com/neighbourservice.php";

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
                                                                            }) {
                                                                                @Override
                                                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                                                    Map<String, String> params = new HashMap<String, String>();
                                                                                    params.put("message", IncidentController.this.incident.getIncidentName());
                                                                                    return params;
                                                                                }
                                                                            };

                                                                            queue.add(stringRequest);


                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
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
                                                    String url = "https://aesuneus.000webhostapp.com/neighbourservice.php";

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
                                                    }) {
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("message", incident.getIncidentName());
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
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("testConnection", "");
                return params;
            }
        };
        queue.add(stringRequest);

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
