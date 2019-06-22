package com.example.neighbourapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.neighbourapplication.controller.UserController;
import com.example.neighbourapplication.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button btnLogin;
    private UserController userController;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }



    public void btnLogin(View vw){
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if(loginEmail.getText().toString().equals("")|| loginPassword.getText().toString() .equals("")){
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
        else{}


        //UserController here
        UserController userController = new UserController(getApplicationContext());
        userController.getUser(loginEmail.getText().toString(), this);

    }

    public void setUser(User user){
        Intent intent = new Intent();
        if(user!=null){
            if(user.getPassword().equals(loginPassword.getText().toString())){
                intent.putExtra("email", user.getEmail());
                intent.putExtra("username", user.getUsername());
                setResult(RESULT_OK, intent);
                Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();

    }
    public void register(View vw){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
