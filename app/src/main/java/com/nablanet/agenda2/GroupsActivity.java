package com.nablanet.agenda2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GroupsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDialog();
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void launchDialog() {

        GroupDialogFragment groupDialogFragment = new GroupDialogFragment();
        groupDialogFragment.show(getSupportFragmentManager(), null);


    }

    public static class GroupDialogFragment extends DialogFragment {



        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            assert getActivity() != null;

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            final ViewGroup nullParent = null;
            builder.setView(inflater.inflate(R.layout.dialog_group, nullParent))
                    // Add action buttons
                    .setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            addGroup(null, null);
                        }
                    })
                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GroupDialogFragment.this.getDialog().cancel();
                        }
                    });

            return builder.create();

        }

        private void addGroup(Bitmap groupImage, String groupName) {

        }

    }

}
