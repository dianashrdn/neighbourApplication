package com.example.neighbourapplication.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.neighbourapplication.LoginActivity;
import com.example.neighbourapplication.ProfileInfoFragment;
import com.example.neighbourapplication.R;
import com.example.neighbourapplication.RegisterActivity;
import com.example.neighbourapplication.UserAdapter;
import com.example.neighbourapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserController {
    private FirebaseFirestore db;
    private Context context;
    private User user;
    private StorageReference storageReference;
    public UserController(Context context){
        storageReference = FirebaseStorage.getInstance().getReference();
        this.context = context;
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();
    }

    public void setUser(final User user) {

        this.user = user;
    }

    //get data
    public void getUsers(final UserAdapter userAdapter){
        final HashMap<String, User> users = new HashMap<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Thread getUserThread = new Thread(){
            @Override
            public void run() {
                db.collection("users")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    User user = documentSnapshot.toObject(User.class);
                                    user.setUserId(documentSnapshot.getId());
                                    users.put(user.getUserId(), user);

                                }
                                userAdapter.setUsers(users);
                                userAdapter.notifyDataSetChanged();
                            }
                        });
            }
        };
        getUserThread.run();
    }

    public void getUser(String id, final ImageView bitmap, final ProfileInfoFragment activity, final ProgressBar progressBar){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User retrievedUser = documentSnapshot.toObject(User.class);
                        User user = new User();
                        user.setPhoto(retrievedUser.getPhoto());
                        user.setUserId(documentSnapshot.getId());
                        user.setUsername(retrievedUser.getUsername());
                        user.setEmail(retrievedUser.getEmail());
                        user.setAddress(retrievedUser.getAddress());
                        user.setPhoneNumber(retrievedUser.getPhoneNumber());
                        activity.setUser(user);
                        ImageView imageView = activity.getActivity().findViewById(R.id.profileImage);
                        //masukkan method untuk refresh view etc
                        if (retrievedUser.getPhoto() != null) {
                            Glide.with(context).load(user.getPhoto()).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            }).into(bitmap);
                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            imageView.setImageResource(R.drawable.ic_home_black_24dp);
                        }
                    }
                }
            }
        });

    }

    public void insertUsers(final User user, final ProfileInfoFragment activity){

        db.collection("users").document(user.getEmail())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "Update successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, "Failed to update..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getUser(String email, final LoginActivity activity){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(email)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User retrievedUser = documentSnapshot.toObject(User.class);
                        activity.setUser(retrievedUser);
                    }
                    else
                        activity.setUser(null);
                }
            }
        });

    }
//
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//        ProgressBar progressBar;
//
//        public DownloadImageTask(ImageView bmImage, ProgressBar progressBar) {
//            this.bmImage = bmImage;
//            this.progressBar = progressBar;
//            this.progressBar.setVisibility(View.VISIBLE);
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            System.out.println("Getting image");
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//                mIcon11 = Bitmap.createScaledBitmap(mIcon11, 150, 150, true);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            System.out.println("Image retrieved");
//            return mIcon11;
//        }
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//            progressBar.setVisibility(View.INVISIBLE);
//        }
//    }

    public void insertUser(final User user, final RegisterActivity activity){

    final ProgressBar progressBar = activity.findViewById(R.id.progressRegister);
    progressBar.setVisibility(View.VISIBLE);
                                db.collection("users").document(user.getEmail())
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(context, "Register successfully", Toast.LENGTH_SHORT).show();
                                                activity.finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(context, "Failed to register..", Toast.LENGTH_SHORT).show();
                                            }
                                        });
    }

    public void insertUser(final User user, Uri bitmapUri, final RegisterActivity activity){


        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh.mm.ss");

        storageReference = storageReference.child("images/"+formatter.format(date));
        final ProgressBar progressBar = activity.findViewById(R.id.progressRegister);
        progressBar.setVisibility(View.VISIBLE);
        if(bitmapUri!=null)
            storageReference.putFile(bitmapUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    user.setPhoto(uri.toString());
                                    db.collection("users").document(user.getEmail())
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show();
                                                    activity.finish();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(context, "Failed to register..", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(context, "Failed to register..", Toast.LENGTH_SHORT).show();
                        }
                    });
        else
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show();
                            activity.finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, "Failed to register..", Toast.LENGTH_SHORT).show();
                        }
                    });

    }

    public void checkExist(String email, final RegisterActivity activity){
        db.collection("users").document(email)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    activity.registerCallback(documentSnapshot.exists());
            }
        });
    }
}
