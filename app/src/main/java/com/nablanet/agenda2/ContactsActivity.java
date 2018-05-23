package com.nablanet.agenda2;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.nablanet.agenda2.adapters.ContactsAdapter;
import com.nablanet.agenda2.pojos.Contact;
import com.nablanet.agenda2.pojos.User;
import com.nablanet.agenda2.viewmodel.AgendaDBViewModel;
import com.nablanet.agenda2.viewmodel.FirebaseQueryLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static android.Manifest.permission.READ_CONTACTS;

public class ContactsActivity extends AppCompatActivity {

    public static final String TAG = "ContactsActivity";

    AgendaDBViewModel viewModel;

    private RecyclerView recyclerView;
    public ContactsAdapter contactsAdapter;

    HashMap<String, User> contacts;

    private static final int REQUEST_READ_CONTACTS = 444;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    ArrayList<String> contactList;
    Cursor cursor;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactsAdapter = new ContactsAdapter(this);
        contactsAdapter.setHasStableIds(true);
        recyclerView.setAdapter(contactsAdapter);

        viewModel = ViewModelProviders.of(this).get(AgendaDBViewModel.class);
        viewModel.getOwnContacts().observe(this, new FirebaseQueryLiveData.FirebaseObserver() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {

                if (dataSnapshot == null || dataSnapshot.getChildren() == null)
                    return;

                if (contacts == null)
                    contacts = new HashMap<>();

                Log.d(TAG, dataSnapshot.getValue().toString());

                Contact contact;
                User user;
                for (DataSnapshot dataSnapShotChild : dataSnapshot.getChildren()){

                    Log.d(TAG, "Child" + dataSnapShotChild.getKey() + " : " + dataSnapShotChild.getValue().toString());

                    if ((contact = dataSnapShotChild.getValue(Contact.class)) != null){
                        user = new User(contact);
                        user.uid = dataSnapShotChild.getKey();
                        contacts.put(
                                dataSnapShotChild.getKey(),
                                new User(contact)
                        );
                        contactsAdapter.addContact(user);
                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Operacion cancelada: " + databaseError.toString());
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                startDialog();

                // Since reading contacts takes more time, let's run it on a separate thread.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateContacts();
                    }
                }).start();


            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS &&
                grantResults.length == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
                )
                updateContacts();
    }

    private void startDialog() {
        pDialog = new ProgressDialog(ContactsActivity.this);
        pDialog.setMessage("Actualizando contactos...");
        pDialog.setCancelable(false);
        pDialog.show();
        updateBarHandler = new Handler();
    }

    public void updateContacts() {

        if (!mayRequestContacts())
            return;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        String[] mProjection = new String[]
                {
                        _ID,
                        DISPLAY_NAME,
                        HAS_PHONE_NUMBER
                };

        String selection = "has_phone_number != '0'";

        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(
                CONTENT_URI,
                mProjection,
                selection,
                null,
                null
        );

        if (cursor == null || cursor.getCount() == 0)
            return;

        // Iterate every contactValues in the phone
        counter = 0;

        HashMap<String, String> mapPhoneNumber = new HashMap<>();

        String phoneNumber;
        while (cursor.moveToNext()) {


            // Update the progress message
            updateBarHandler.post(new Runnable() {
                public void run() {
                    pDialog.setMessage("Verificando contacto: " + counter++ + "/" + cursor.getCount());
                }
            });

            String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
            String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

            //This is to read multiple phone numbers associated with the same contactValues
            Cursor phoneCursor = contentResolver.query(
                    PhoneCONTENT_URI,
                    null,
                    Phone_CONTACT_ID + " = ?",
                    new String[]{contact_id},
                    null
            );

            mapPhoneNumber.clear();
            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    phoneNumber = getValidNumber(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
                    if (phoneNumber != null)
                        mapPhoneNumber.put(phoneNumber, name);
                }
                phoneCursor.close();
            }
        }

        contactList = new ArrayList<>();

        // Dismiss the progressbar after 500 millisecondds
        updateBarHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pDialog.cancel();
            }
        }, 500);

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
