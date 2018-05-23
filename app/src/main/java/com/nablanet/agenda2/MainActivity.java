package com.nablanet.agenda2;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nablanet.agenda2.views.CustomCalendarView;

public class MainActivity extends AppCompatActivity {

    private static final int SIGNUP = 0;
    private static final int PROFILE = 1;
    private static final int CONTACTS = 2;

    private FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference myRef;

    ImageView groupImage;
    TextView groupName;
    Button groupButton;
    CustomCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            TextView title = new TextView(getApplicationContext());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            title.setLayoutParams(lp);
            title.setText(R.string.title_action_bar);
            title.setTextSize(24);
            title.setTextColor(Color.parseColor("#FFFFFF"));
            Typeface tf = Typeface.createFromAsset(getAssets(), "comic_relief.ttf");
            title.setTypeface(tf);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(title);
        }

        groupImage = findViewById(R.id.groupImage);
        groupName = findViewById(R.id.groupName);
        groupButton = findViewById(R.id.groupButton);
        calendarView = findViewById(R.id.calendarView);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("users");

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

                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null)
            startActivityForResult(new Intent(this, PhoneAuthActivity.class), SIGNUP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.profile_menu:
                startActivityForResult(new Intent(this, UserActivity.class), PROFILE);
                return false;
            case R.id.contacts_menu:
                startActivityForResult(new Intent(this, ContactsActivity.class), CONTACTS);
                return false;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SIGNUP:
                Intent intent = new Intent(this, UserActivity.class);
                startActivityForResult(intent, PROFILE);
                break;

            case PROFILE:
                break;

            case CONTACTS:
                break;

                default:
        }

    }
}
