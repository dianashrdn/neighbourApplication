package com.example.neighbourapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class AddIncidentDialog extends AppCompatDialogFragment {
    private EditText editName;
    private EditText editDate;
    private EditText editTime;
    private EditText editNote;
    private Button getImage;
    private Uri imageURI;
    private AddIncidentDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_incident, null);

        builder.setView(view)
                .setTitle("Add Incident")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = editName.getText().toString();
                        String date = editDate.getText().toString();
                        String time = editTime.getText().toString();
                        String note = editNote.getText().toString();

                        listener.applyTexts(name, date, time, note, imageURI);
                    }
                });

        editName = view.findViewById(R.id.editName);
        editDate = view.findViewById(R.id.editDate);
        editTime = view.findViewById(R.id.editTime);
        Date date = new Date();
        SimpleDateFormat dateFormat  = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat  = new SimpleDateFormat("hh:mm");

        editDate.setText(dateFormat.format(date));
        editTime.setText(timeFormat.format(date));
        editNote = view.findViewById(R.id.editNote);
        getImage = view.findViewById(R.id.btnAddPicture);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddIncidentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface AddIncidentDialogListener {
        void applyTexts(String name, String date, String time, String note, Uri imageUri);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==  RESULT_OK){
            imageURI = data.getData();
            getImage.setText("Edit Image");
        }
    }
}
