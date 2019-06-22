package com.example.neighbourapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.neighbourapplication.controller.UserController;
import com.example.neighbourapplication.model.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtAddress;
    private EditText edtPhoneNo;
    private EditText edtPassword;
    private Button btnRegister;
    private Button btnCancel;
    private UserController userController;
    Uri imageURI;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userController = new UserController(getApplicationContext());
        edtUsername = findViewById(R.id.loginEmail);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhoneNo = findViewById(R.id.edtPhoneNo);
        edtPassword = findViewById(R.id.edtPassword);
        imageView = findViewById(R.id.pictureUpload);
    }





    public void btnCancel(View view) {
        finish();
    }

    public void btnRegister(View view) {
        if(edtUsername.getText().toString().equals("")|| edtEmail.getText().toString() .equals("")|| edtAddress.getText().toString().equals("")
        || edtPhoneNo.getText().toString().equals("") || edtPassword.getText().toString().equals("")){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("");
            alertDialog.setMessage("Please fill in all the blanks");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else
            userController.checkExist(edtEmail.getText().toString(), this);
    }

    public void registerCallback(boolean exist){
        if(exist){
            Toast.makeText(getApplicationContext(),"Email already registered", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String username = edtUsername.getText().toString();
            String email = edtEmail.getText().toString();
            String address = edtAddress.getText().toString();
            String phoneNumber = edtPhoneNo.getText().toString();
            String password = edtPassword.getText().toString();
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setAddress(address);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(password);
            userController.insertUser(user, imageURI,this);
        }
    }

    public void btnUpload(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
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
