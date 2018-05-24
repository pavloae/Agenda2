package com.nablanet.agenda2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nablanet.agenda2.pojos.Phone;
import com.nablanet.agenda2.pojos.User;
import com.nablanet.agenda2.viewmodel.AgendaDBViewModel;
import com.nablanet.agenda2.viewmodel.FirebaseQueryLiveData.FirebaseObserver;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    ImageView userImage;
    EditText fieldName, fieldComment;
    TextView phoneNumber;
    CheckBox shareNumber;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    User user;
    Phone phone;
    AgendaDBViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userImage = findViewById(R.id.userImage);
        fieldName = findViewById(R.id.field_name);
        fieldComment = findViewById(R.id.field_comment);
        phoneNumber = findViewById(R.id.phone_tv);
        shareNumber = findViewById(R.id.share_box);

        loadUser();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });

    }

    private void loadUser(){

        if (databaseReference == null || firebaseUser == null || firebaseUser.getPhoneNumber() == null)
            return;

        phoneNumber.setText(firebaseUser.getPhoneNumber());

        databaseReference
                .child("users")
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        if (user != null){
                            fieldName.setText(user.name);
                            fieldComment.setText(user.comment);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.toString());
                    }
                });

        databaseReference
                .child("phones")
                .child(firebaseUser.getPhoneNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        phone = dataSnapshot.getValue(Phone.class);
                        if (phone != null)
                            shareNumber.setChecked(phone.share);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.toString());
                    }
                });

    }

    private void updateUser(){

        if (fieldName.length() == 0 || fieldName.length() >= 20){
            fieldName.setError("El nombre debe tener entre 1 y 20 caracteres");
            return;
        }

        if (fieldComment.length() >= 50){
            fieldComment.setError("El comentario debe tener menos de 50 caracteres");
            return;
        }

        if (user == null)
            user = new User().setUid(firebaseUser.getUid());
        user.setName(fieldName.getText().toString()).setComment(fieldComment.getText().toString());

        if (phone == null)
            phone = new Phone().setUid(firebaseUser.getUid());
        phone.setShare(shareNumber.isChecked());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + firebaseUser.getUid(), user.toMap());
        childUpdates.put("/phones/" + firebaseUser.getPhoneNumber(), phone.toMap());

        databaseReference.updateChildren(childUpdates);

    }

}
