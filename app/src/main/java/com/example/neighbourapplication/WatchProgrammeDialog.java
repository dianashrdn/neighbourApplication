package com.example.neighbourapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WatchProgrammeDialog extends AppCompatDialogFragment {
    private EditText editProgrammeName;
    private EditText editDescription;
    private EditText editDateStart;
    private EditText editDateEnd;
    private Button btnSaveActivity;
    private WatchProgrammeDialog.watchProgrammeDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_programme, null);

        builder.setView(view)
                .setTitle("Add Activity")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String programmeName = editProgrammeName.getText().toString();
                        String description = editDescription.getText().toString();
                        String dateStart = editDateStart.getText().toString();
                        String dateEnd = editDateEnd.getText().toString();

                        listener.watchProgrammeInput(programmeName, description, dateStart, dateEnd);
                    }
                });

        editProgrammeName = view.findViewById(R.id.editProgrammeName);
        editDescription = view.findViewById(R.id.editDescription);
        editDateStart = view.findViewById(R.id.editDateStart);
        editDateEnd = view.findViewById(R.id.editDateEnd);
        Date date = new Date();
        SimpleDateFormat dateFormat  = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat  = new SimpleDateFormat("hh:mm");

        editDateStart.setText(dateFormat.format(date));
        editDateEnd.setText(dateFormat.format(date));

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
            listener = (WatchProgrammeDialog.watchProgrammeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface watchProgrammeDialogListener {
        void watchProgrammeInput(String programmeName, String description, String dateStart, String dateEnd);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
