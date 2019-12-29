package com.cegis.deltaplan2100;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.mapbox.mapboxsdk.maps.MapView;

public class ClimateActivity extends AppCompatActivity {
    private MapView mapView;
    ListView list_view;
    String[][] items = {
            {"Chart Items", "1"},
            {"Map Items", "2"},
            {"Tabular Items", "3"},
            {"Textual Items", "4"},
            {"Items", "5"},
            {"Items", "6"},
    };

    //chart: f080, map: f5a0, table: f0ce, text: f036, right arrow: f101
    String[] icon = {"\uf080", "\uf5a0", "\uf0ce", "\uf036", "\uf101"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate);

        //list_view = findViewById(R.id.list_view);
        //list_view.setAdapter(new ListAdapter(this, items));
    }
}
