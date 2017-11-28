package com.kth.ii2300.laundryprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {

    //Collection of garments in memory
    private ArrayList<Garment> garments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate the garment collection object and load dummy garments
        garments = new ArrayList<Garment>();
        loadGarments();

        //Get object representations of view items
        final ListView listview = (ListView) findViewById(R.id.listView_garments);
        final Button btnConfirmSelection = (Button) findViewById(R.id.btnConfirmSelection);

        //Generate row views for each garment and load in parent listview
        //See
        //https://developer.android.com/guide/topics/ui/layout/listview.html
        final GarmentAdapter adapter = new GarmentAdapter(this,
                R.layout.listitemlayout, garments);
        listview.setAdapter(adapter);

        //Implement a listener for the confirm button
        //Displays a temporary alert (a toast) notifying
        //number of selected garments
        btnConfirmSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numSelectedGarments = getNumberOfSelectedGarments();
                Toast.makeText(getApplicationContext(), "Number of selected garments: " + numSelectedGarments, Toast.LENGTH_LONG).show();
            }
        });
    }

    //Load dummy garment data into garment collection in memory
    private void loadGarments() {
        garments.add(new Garment(1, "Don't know", 3, 22, 5555, 3, 333));
        garments.add(new Garment(2, "Don't know", 5, 30, 55, 1, 99));
        garments.add(new Garment(3, "Don't know", 2, 20, 43, 2, 12));
        garments.add(new Garment(4, "Don't know", 1, 15, 78, 3, 134));
        garments.add(new Garment(5, "Don't know", 6, 40, 54, 2, 367));
        garments.add(new Garment(6, "Don't know", 6, 55, 33, 1, 100));
        garments.add(new Garment(7, "Don't know", 9, 10, 28, 2, 233));
        garments.add(new Garment(8, "Don't know", 7, 15, 43, 1, 212));
        garments.add(new Garment(9, "Don't know", 1, 25, 61, 3, 432));
        garments.add(new Garment(10, "Don't know", 5, 30, 22, 2, 222));
    }

    //Dummy method to test effect of selecting/deselecting garments
    private int getNumberOfSelectedGarments() {
        ArrayList<Garment> selectedGarments = new ArrayList<Garment>();
        for(Garment g : garments) {
            if(g.isIncludedInWash()) {
                selectedGarments.add(g);
            }
        }
        return selectedGarments.size();
    }
}
