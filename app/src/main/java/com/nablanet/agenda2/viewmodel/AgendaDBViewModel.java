package com.nablanet.agenda2.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nablanet.agenda2.pojos.Phone;
import com.nablanet.agenda2.pojos.User;

public class AgendaDBViewModel extends ViewModel {

    private static final DatabaseReference GROUPS =
            FirebaseDatabase.getInstance().getReference("groups");

    private final FirebaseQueryLiveData groups = new FirebaseQueryLiveData(GROUPS);

    @NonNull
    public LiveData<DataSnapshot> getGroups() {
        return groups;
    }

}
