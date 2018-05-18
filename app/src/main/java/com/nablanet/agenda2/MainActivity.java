package com.nablanet.agenda2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nablanet.agenda2.views.CustomCalendarView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final String USER = "userPreference";

    private FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference myRef;

    ImageView groupImage;
    TextView groupName;
    Button groupButton;
    CustomCalendarView calendarView;

    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupImage = findViewById(R.id.groupImage);
        groupName = findViewById(R.id.groupName);
        groupButton = findViewById(R.id.groupButton);
        calendarView = findViewById(R.id.calendarView);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("message");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                groupName.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, GroupsActivity.class);
                startActivity(intent);

            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Intent intent = new Intent(MainActivity.this, DayActivity.class);
                intent.putExtra(DayActivity.DAY_OF_MONTH, dayOfMonth);
                intent.putExtra(DayActivity.MONTH, month);
                intent.putExtra(DayActivity.YEAR, year);
                intent.putExtra(DayActivity.GROUP_ID, groupId);

                startActivity(intent);

                Log.d(TAG, "dayOfMonth: " + dayOfMonth);

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, PhoneAuthActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "userId: " + currentUser.getUid());
        }

    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER, MODE_PRIVATE);
        sharedPreferences.getString(DayActivity.GROUP_ID, "");
    }

}
