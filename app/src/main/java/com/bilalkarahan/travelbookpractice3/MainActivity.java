package com.bilalkarahan.travelbookpractice3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> placeList = new ArrayList<>();;
    ArrayList<Integer> idList = new ArrayList<>();
    SQLiteDatabase database;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        getData();

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, placeList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("info", "old");
                intent.putExtra("placeId", idList.get(position));
                startActivity(intent);

            }
        });

    }

    public void getData() {

        try {

            database = this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM places",null);

            int addressIx = cursor.getColumnIndex("address");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {

                String addressFromDatabase = cursor.getString(addressIx);
                Integer idFromDatabase = cursor.getInt(idIx);

                placeList.add(addressFromDatabase);
                idList.add(idFromDatabase);

            }

            arrayAdapter.notifyDataSetChanged();
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_location, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add_location_item) {

            Intent intent = new Intent(MainActivity.this,MapsActivity.class);
            intent.putExtra("info", "new");
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
}