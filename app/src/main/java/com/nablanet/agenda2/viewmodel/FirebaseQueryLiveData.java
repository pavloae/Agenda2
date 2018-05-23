package com.nablanet.agenda2.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {

    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private final Query query;
    private final ValueEventListener listener = new MyValueEventListener();
    private final ChildEventListener childEventListener = new MyChildEventListener();

    private Observer<DataSnapshot> observer;

    public FirebaseQueryLiveData(Query query) {
        this.query = query;
    }

    public FirebaseQueryLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(listener);
        observer = null;
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<DataSnapshot> observer) {
        super.observe(owner, observer);
        this.observer = observer;
    }

    public interface FirebaseObserver extends Observer<DataSnapshot> {

        @Override
        void onChanged(@Nullable DataSnapshot dataSnapshot);
        void onCancelled(DatabaseError databaseError);

    }

    public interface FirebaseChildsObserver extends Observer<DataSnapshot> {

        @Override
        void onChanged(@Nullable DataSnapshot dataSnapshot);
        void onChildAdded(DataSnapshot dataSnapshot, String s);
        void onChildChanged(DataSnapshot dataSnapshot, String s);
        void onChildRemoved(DataSnapshot dataSnapshot);
        void onChildMoved(DataSnapshot dataSnapshot, String s);
        void onCancelled(DatabaseError databaseError);

    }

    private void sendError(DatabaseError databaseError){

        Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());

        if (observer instanceof FirebaseObserver)
            ((FirebaseObserver) observer).onCancelled(databaseError);

        else if (observer instanceof FirebaseChildsObserver)
            ((FirebaseChildsObserver) observer).onCancelled(databaseError);
    }

    private class MyValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            sendError(databaseError);
        }
    }

    private class MyChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (observer instanceof FirebaseChildsObserver)
                ((FirebaseChildsObserver) observer).onChildAdded(dataSnapshot, s);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

            sendError(databaseError);

        }
    }



}
