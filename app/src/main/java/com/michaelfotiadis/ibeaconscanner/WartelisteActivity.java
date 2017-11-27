package com.michaelfotiadis.ibeaconscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WartelisteActivity extends AppCompatActivity {
    public ListView listView;
    public String[] itemsArray = {"Anna", "Stefanie"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warteliste);

        listView = (ListView) findViewById(R.id.lvWarteliste);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(com.michaelfotiadis.ibeaconscanner.WartelisteActivity.this,
                android.R.layout.simple_list_item_1);
        adapter.addAll(itemsArray);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
