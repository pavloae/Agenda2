package com.nablanet.agenda2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.nablanet.agenda2.pojos.Phone;
import com.nablanet.agenda2.pojos.User;
import com.nablanet.agenda2.viewmodel.AgendaDBViewModel;
import com.nablanet.agenda2.viewmodel.FirebaseQueryLiveData.FirebaseObserver;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    ImageView userImage;
    EditText fieldName, fieldComment;
    TextView phoneNumber;
    CheckBox shareNumber;

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

        userImage = findViewById(R.id.userImage);
        fieldName = findViewById(R.id.field_name);
        fieldComment = findViewById(R.id.field_comment);
        phoneNumber = findViewById(R.id.phone_tv);
        shareNumber = findViewById(R.id.share_box);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null
                && firebaseAuth.getCurrentUser().getPhoneNumber() != null)
            phoneNumber.setText(firebaseAuth.getCurrentUser().getPhoneNumber());


        viewModel = ViewModelProviders.of(this).get(AgendaDBViewModel.class);

        viewModel.getOwnUser().observe(this, new FirebaseObserver() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                Log.v(TAG, "getOwnUser().onChanged!!!");
                if (dataSnapshot != null && dataSnapshot.getValue() != null){
                    user = dataSnapshot.getValue(User.class);
                    loadUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                viewModel.createOwnUser(
                        new User(
                                fieldName.getText().toString(),
                                fieldComment.getText().toString(),
                                null, // TODO: Implementar la imagen
                                false
                        )
                );
            }
        });

        viewModel.getPhone(null).observe(this, new FirebaseObserver() {

            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null){
                    phone = dataSnapshot.getValue(Phone.class);

                    if (user != null && phone != null)
                        user.share = phone.share;

                    loadUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                viewModel.createOwnPhone();
            }

        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });

    }

    private void loadUser(){

        if (user != null){
            fieldName.setText(user.name);
            fieldComment.setText(user.comment);
            shareNumber.setChecked(user.share);
        }

    }

    private void updateUser(){

        if (fieldName.length() == 0 || fieldName.length() >= 20){
            fieldName.setError("El nombre debe tener entre 1 y 20 caracteres");
            return;
        }

        if (fieldComment.length() >= 20){
            fieldComment.setError("El comentario debe tener menos de 20 caracteres");
            return;
        }

        user = new User(
                fieldName.getText().toString(),
                fieldComment.getText().toString(),
                null, //TODO: Implementar la imagen
                shareNumber.isChecked()
        );

        if (phone == null)
            phone = new Phone(FirebaseAuth.getInstance().getUid(), false);

        phone.share = user.share;
        viewModel.updateOwnUser(user);
        viewModel.updateOwnPhone(phone);
    }

}
