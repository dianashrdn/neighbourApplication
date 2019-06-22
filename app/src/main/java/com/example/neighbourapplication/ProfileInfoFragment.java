package com.example.neighbourapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.neighbourapplication.controller.UserController;
import com.example.neighbourapplication.controller.WatchProgrammeController;
import com.example.neighbourapplication.model.User;
import com.google.firebase.firestore.DocumentReference;

import static android.app.Activity.RESULT_OK;

public class ProfileInfoFragment extends Fragment {
    EditText edtName, edtEmail, edtAddress, edtPhone;
    ImageView imageView;
    String email;
    Uri imageURI;
    Button btnChangePhoto, btnSaveUpdate;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle SavedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("NeighbourApp", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email",null);
        edtName = rootView.findViewById(R.id.edtName);
        edtEmail = rootView.findViewById(R.id.edtEmail);
        edtAddress = rootView.findViewById(R.id.edtAddress);
        edtPhone = rootView.findViewById(R.id.edtPhone);
        imageView = rootView.findViewById(R.id.profileImage);
        btnChangePhoto = rootView.findViewById(R.id.btnChangePhoto);
        btnSaveUpdate = rootView.findViewById(R.id.btnSaveUpdate);
        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnChangePhoto(view);
            }
        });
        progressBar = rootView.findViewById(R.id.progressProfile);
        UserController userController = new UserController(getContext());
        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("NeighbourApp", Context.MODE_PRIVATE);

        userController.getUser(sharedPreferences.getString("email", null),imageView,this, progressBar);
        return rootView;

    }

    public void btnChangePhoto(View vw){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        getActivity().setTitle("User Profile");
    }

    public void setUser(User user){
        this.edtPhone.setText(user.getPhoneNumber());
        this.edtAddress.setText(user.getAddress());
        this.edtEmail.setText(user.getEmail());
        this.edtName.setText(user.getUsername());
    }

    public void setBtnSaveUpdate(View vw){
        UserController userController = new UserController(getContext());

        String username = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String address = edtAddress.getText().toString();
        String phoneNumber = edtPhone.getText().toString();
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        userController.insertUsers(user, this);



    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==  RESULT_OK){
            imageURI = data.getData();
            imageView.setImageURI(imageURI);

        }
    }

}
