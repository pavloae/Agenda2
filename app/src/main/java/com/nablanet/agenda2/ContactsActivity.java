package com.nablanet.agenda2;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nablanet.agenda2.adapters.ContactsAdapter;
import com.nablanet.agenda2.pojos.Contact;
import com.nablanet.agenda2.pojos.User;
import com.nablanet.agenda2.viewmodel.AgendaDBViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static android.Manifest.permission.READ_CONTACTS;

public class ContactsActivity extends AppCompatActivity {

    public static final String TAG = "ContactsActivity";

    private static final int REQUEST_READ_CONTACTS = 0;

    public ContactsAdapter contactsAdapter;

    HashMap<String, String> localContacts; // <phoneNumber, name>
    HashMap<String, Object> remoteContacts;

    int counter;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactsAdapter = new ContactsAdapter(this);
        contactsAdapter.setHasStableIds(true);
        recyclerView.setAdapter(contactsAdapter);

        loadRemoteContacts();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSync();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS &&
                grantResults.length == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
                )
                startSync();
    }

    private void loadRemoteContacts() {

        String ownUid = FirebaseAuth.getInstance().getUid();

        if (ownUid == null)
            return;

        final Query query = FirebaseDatabase.getInstance().getReference("contacts").child(ownUid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getChildren() == null)
                    return;

                Contact contact;
                for (DataSnapshot dataSnapShotChild : dataSnapshot.getChildren()){
                    if ((contact = dataSnapShotChild.getValue(Contact.class)) != null){
                        loadUserProfile(
                                new User()
                                        .setUid(dataSnapShotChild.getKey())
                                        .setContactValues(contact)
                        );
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error on " + query + " : " + databaseError.toString());
            }
        });

    }

    private void loadUserProfile(@NonNull final User user) {

        if (user.uid == null)
            return;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(user.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getChildren() == null)
                    return;

                user.name = dataSnapshot.child("name").getValue(String.class);
                contactsAdapter.addContact(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });






    }

    private void startSync() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Actualizando contactos...");
        pDialog.setCancelable(false);
        pDialog.show();
        updateBarHandler = new Handler();

        // Since reading databaseContacts takes more time, let's run it on a separate thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Cargamos los contactos locales
                localContacts = getAllLocalContacts();

                updateBarHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.cancel();

                        // Buscamos los teléfonos en el servidor
                        queryPhonesNumbers();

                    }
                }, 500);
            }
        }).start();

    }

    private HashMap<String, String> getAllLocalContacts() {

        if (!mayRequestContacts())
            return null;

        ContentResolver contentResolver = getContentResolver();

        // Pedimos todos los contactos locales que tienen número de teléfono
        final Cursor contactsCursor = contentResolver.query(
                Contacts.CONTENT_URI,
                new String[]{Contacts._ID, Contacts.DISPLAY_NAME},
                String.format("%s != ?", Contacts.HAS_PHONE_NUMBER),
                new String[]{"0"},
                null
        );

        HashMap<String, String> localContacts = new HashMap<>();
        if (contactsCursor != null) {

            counter = 0;
            while (contactsCursor.moveToNext()) {
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Verificando contacto: " + counter++ + "/" + contactsCursor.getCount());
                    }
                });

                String _id = contactsCursor.getString(contactsCursor.getColumnIndex(Contacts._ID));
                String name = contactsCursor.getString(contactsCursor.getColumnIndex(Contacts.DISPLAY_NAME));

                // Pedimos todos los teléfonos asociados a este contacto {_id}
                Cursor phonesCursor = contentResolver.query(
                        Phone.CONTENT_URI,
                        new String[]{Contacts._ID, Phone.NUMBER},
                        String.format("%s = ?", Phone.CONTACT_ID),
                        new String[]{_id},
                        null
                );

                // Para cada teléfono encontrado verificamos si es válido y lo guardamos junto al nombre
                if (phonesCursor != null) {
                    while (phonesCursor.moveToNext()) {
                        String phoneNumber = getValidNumber(
                                phonesCursor.getString(phonesCursor.getColumnIndex(Phone.NUMBER))
                        );
                        if (phoneNumber != null)
                            localContacts.put(phoneNumber, name);
                    }
                    phonesCursor.close();
                }
            }
            contactsCursor.close();
        }

        return localContacts;

    }

    private void queryPhonesNumbers() {

        if (localContacts == null || localContacts.isEmpty())
            return;

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final String ownUid = FirebaseAuth.getInstance().getUid();

        if (ownUid == null)
            return;

        remoteContacts = new HashMap<>();

        for (final String phone : localContacts.keySet()){
            Log.d(TAG, String.format("Buscando el teléfono %s", phone));
            db.getReference("phones").child(phone).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        HashMap<String, String> newContact;
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot == null || !dataSnapshot.exists()){
                                Log.d(TAG, "No hay mensaje para este teléfono");
                                return;
                            }
                            String uid = dataSnapshot.child("uid").getValue(String.class);
                            Log.d(TAG, "OnDataChange - " + String.format("El teléfono %s es de %s", phone, uid));

                            if (uid == null)
                                return;

                            newContact = new HashMap<>();
                            newContact.put("phone", phone);
                            newContact.put("name", localContacts.get(phone));

                            db.getReference("contacts").child(ownUid).child(uid).setValue(newContact);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled - " + "El telefono " + phone + " devolvió: " + databaseError.toString());
                        }
                    }
            );
        }

    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            return true;

        if (shouldShowRequestPermissionRationale(READ_CONTACTS))
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        else
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);

        return false;
    }

    private String getValidNumber(String phoneNumber) {

        if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches())
            return null;

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(this);

        try {
            Phonenumber.PhoneNumber argNumberProto = phoneUtil.parse(phoneNumber, "AR");
            if (phoneUtil.isValidNumberForRegion(argNumberProto, "AR")){

                String validPhone = phoneUtil.format(argNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);

                if (validPhone.matches("\\+54[0-9]{10}"))
                    return validPhone;

                if (validPhone.matches("\\+549[0-9]{10}"))
                    return validPhone.substring(0, 3) + validPhone.substring(4);

            }
            return null;
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
            return null;
        }

    }

}
