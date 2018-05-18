package com.nablanet.agenda2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DayActivity extends AppCompatActivity {

    public static final String DAY_OF_MONTH = "dayOfMonth";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String GROUP_ID = "groupId";

    int dayOfMonth, month, year;
    String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dayOfMonth = getIntent().getIntExtra(DAY_OF_MONTH, 1);
        month = getIntent().getIntExtra(MONTH, 1);
        year = getIntent().getIntExtra(YEAR, 2018);
        groupId = getIntent().getStringExtra(GROUP_ID);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
