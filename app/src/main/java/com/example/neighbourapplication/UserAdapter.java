package com.example.neighbourapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.neighbourapplication.controller.UserController;
import com.example.neighbourapplication.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private ArrayList<User> users;
    private Context context;



    public UserAdapter() {
    }
    public UserAdapter(Context context){
        users = new ArrayList<>();
        this.context = context;
    }

    public void setUsers(HashMap<String, User> users){
        this.users = new ArrayList<>(users.values());
    }
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.homelist_card,parent,false);
        return new UserAdapter.ViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, int position) {
        final User user = users.get(position);
        UserController userController = new UserController(context);
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        holder.address.setText(user.getAddress());
        holder.phoneNumber.setText(user.getPhoneNumber());

        if (user.getPhoto() != null) {
            Glide.with(context).load(user.getPhoto()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }
            }).into(holder.photo);
        }
//            new UserAdapter.DownloadImageTask(holder.photo, holder.progressBar).execute(user.getPhoto());
        else{
            System.out.println("No picture");
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.photo.setImageResource(R.drawable.ic_home_black_24dp);
        }


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, address, email, phoneNumber;
        public ImageView photo;
        public ProgressBar progressBar;
        public ViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBarH);
            username = itemView.findViewById(R.id.cardOwnerName);
            email = itemView.findViewById(R.id.cardOwnerEmail);
            address= itemView.findViewById(R.id.cardOwnerAddress);
            phoneNumber = itemView.findViewById(R.id.cardOwnerPhone);
            photo = itemView.findViewById(R.id.cardOwnerPhoto);

        }
    }

    //download Image
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//        ProgressBar progressBar;
//
//        public DownloadImageTask(ImageView bmImage, ProgressBar progressBar) {
//            this.bmImage = bmImage;
//            this.progressBar = progressBar;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            System.out.println("Getting image");
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//                mIcon11 = Bitmap.createScaledBitmap(mIcon11,150, 150, true);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            System.out.println("Image retrieved");
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//            progressBar.setVisibility(View.GONE);
//        }
//    }
}

