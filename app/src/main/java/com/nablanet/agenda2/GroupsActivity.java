package com.nablanet.agenda2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nablanet.agenda2.dialogs.GroupDialogFragment;
import com.nablanet.agenda2.interfaces.ImageManagerInterface;
import com.nablanet.agenda2.pojos.Group;
import com.nablanet.agenda2.utils.ImageManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GroupsActivity extends AppCompatActivity implements GroupDialogFragment.NoticeDialogListener, ImageManagerInterface {

    public static final String TAG = "GroupsActivity";


    private int PICK_IMAGE_REQUEST = 0;
    private int UPLOAD_IMAGE = 1;

    FirebaseDatabase database;
    DatabaseReference myRef;
    ListView listView;

    FirebaseAuth firebaseAuth;

    GroupDialogFragment groupDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.groupsLV);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("groups");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDialog(null);
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void launchDialog(Group group) {

        String title = (group == null) ? "Nuevo grupo..." : "Editar grupo...";

        groupDialogFragment = GroupDialogFragment.newInstance(title, group);
        groupDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        groupDialogFragment.show(getSupportFragmentManager(), "fragment_edit_name");

    }

    @Override
    public void onDialogPositiveClick(GroupDialogFragment dialog) {

        String groupName = dialog.name;
        String groupComment = dialog.comment;
        boolean publicGroup = dialog.publicGroup;

        String keyGroup = myRef.push().getKey();

        Group group = new Group(groupName, groupComment, "http://image.agenda2.com.ar/"+keyGroup+".png", firebaseAuth.getUid(), publicGroup);
        Log.d(TAG, "group: " + group.toMap().toString());
        myRef.child(keyGroup).setValue(group.toMap());
        Log.d(TAG, "keyGroup: " + keyGroup);
    }

    @Override
    public void getImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                groupDialogFragment.groupImage.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                groupDialogFragment.groupImage.setImageBitmap(bitmap);
                ImageManager.uploadImage(this, UPLOAD_IMAGE, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onCompleteOperation(int code, boolean status) {

    }
}
