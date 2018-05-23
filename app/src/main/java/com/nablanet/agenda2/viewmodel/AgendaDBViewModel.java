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

    private static final DatabaseReference OWN_USER =
            FirebaseDatabase.getInstance().getReference("users").child(getUid());
    private static final DatabaseReference USER =
            FirebaseDatabase.getInstance().getReference("users");

    private static final DatabaseReference OWN_PHONE =
            FirebaseDatabase.getInstance().getReference("phones").child(getPhoneNumber());
    private static final DatabaseReference PHONE =
            FirebaseDatabase.getInstance().getReference("phones");

    private static final DatabaseReference OWN_CONTACTS =
            FirebaseDatabase.getInstance().getReference("contacts").child(getUid());

    private final FirebaseQueryLiveData ownUser = new FirebaseQueryLiveData(OWN_USER);
    private final FirebaseQueryLiveData ownPhone = new FirebaseQueryLiveData(OWN_PHONE);
    private final FirebaseQueryLiveData ownContacts = new FirebaseQueryLiveData(OWN_CONTACTS);

    @NonNull
    public LiveData<DataSnapshot> getOwnUser() {
        return ownUser;
    }

    @NonNull
    public LiveData<DataSnapshot> getUser(String uid) {
        return new FirebaseQueryLiveData(USER.child(uid));
    }

    @NonNull
    public LiveData<DataSnapshot> getPhone(String phoneNumber) {
        return (phoneNumber == null) ? ownPhone : new FirebaseQueryLiveData(PHONE.child(phoneNumber));
    }

    @NonNull
    public LiveData<DataSnapshot> getOwnContacts() {
        return ownContacts;
    }

    public void createOwnUser(User user){
        USER.child(getUid()).setValue(user);
    }

    public void updateOwnUser(User user){
        OWN_USER.updateChildren(user.toMap());
    }

    public void createOwnPhone(){
        PHONE.child(getPhoneNumber()).setValue(new Phone(getUid(), false).toMap());
    }

    public void updateOwnPhone(Phone phone) {
        phone.uid = getUid();
        OWN_PHONE.updateChildren(phone.toMap());
    }

    private static String getUid() {
        return FirebaseAuth.getInstance().getUid();
    }

    private static String getPhoneNumber(){
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }

}
