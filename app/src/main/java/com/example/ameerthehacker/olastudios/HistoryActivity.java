package com.example.ameerthehacker.olastudios;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ListView lstHistory;
    private History history;
    private ArrayList<String> activities;
    private Toolbar myToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        activities = new ArrayList<>();
        history = new History(getApplicationContext());
        lstHistory = (ListView)findViewById(R.id.lstHistory);
        myToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Cursor cursor = history.getHistory();
        while(cursor.moveToNext()) {
            activities.add(cursor.getString(0));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, activities);
        lstHistory.setAdapter(adapter);
    }
    public void onBackPressed() {
        startActivity(new Intent(HistoryActivity.this, MainActivity.class));
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
