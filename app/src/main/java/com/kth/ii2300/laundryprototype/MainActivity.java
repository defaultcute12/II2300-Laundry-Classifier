package com.kth.ii2300.laundryprototype;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {

    //Class-global variables
    private DatabaseHelper dbHelper;
    //Collection of garments in memory
    private ArrayList<Garment> garments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate class-global variables
        dbHelper = new DatabaseHelper(this);
        //Instantiate the garment collection object and load garments
        garments = new ArrayList<Garment>();
        loadGarments();
        garments = loadGarmentsFromDb();

        //Get object representations of view items
        final ListView listview = (ListView) findViewById(R.id.listView_garments);
        final Button btnConfirmSelection = (Button) findViewById(R.id.btnConfirmSelection);
        final TextView txtSuggestion = (TextView) findViewById(R.id.txtSuggestion);
        final Button btnAddOrEditGarment = (Button) findViewById(R.id.btnAddOrEditGarment);

        //Generate row views for each garment and load in parent listview
        //See
        //https://developer.android.com/guide/topics/ui/layout/listview.html
        final GarmentAdapter adapter = new GarmentAdapter(this,
                R.layout.listitemlayout, garments);
        listview.setAdapter(adapter);

        //Implement a listener for the add/edit clothing button
        //Goes to the EditGarmentActivity
        btnAddOrEditGarment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditGarmentActivity();
            }
        });

        //Implement a listener for the confirm button
        //Displays a temporary alert (a toast) notifying
        //number of selected garments
        btnConfirmSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Load selected garment types into a Logic object
                ArrayList<Garment> selectedGarments = getSelectedGarments();
                Logic logic = new Logic(selectedGarments);
                //Get suggested temperature and spinning limit for the mix from the Logic object
                int suggestedTemperature = logic.getMinTemp();
                int spinningLimitSuggestion = logic.getMinSpin();
                int weightSuggestion = logic.getMinWeight();
                int yarnTwistSuggestion = logic.getMinYarnTwist();
                //Get warnings based on the selected washing mix
                ArrayList<String> warnings = logic.getWarnings();
                //Build a suggestion string to display in a text view to the user
                String suggestionString = generateSuggestionString(suggestedTemperature, spinningLimitSuggestion, weightSuggestion, yarnTwistSuggestion, warnings);
                txtSuggestion.setText(suggestionString);
            }
        });
    }

    //Load dummy garment data into garment collection in memory
    private void loadGarments() {
        garments.add(new Garment(1, "Cotton", 3, 60, 1800, 333, false));
        garments.add(new Garment(2, "Denim", 5, 40, 900, 99, false));
        garments.add(new Garment(3, "Nylon", 2, 30, 1200, 12, true));
        garments.add(new Garment(4, "Silk", 1, 15, 300, 134, true));
    }

    //Start an asynchronous task that loads all garments from database to an ArrayList
    //Do error handling
    private ArrayList<Garment> loadGarmentsFromDb() {
        ArrayList<Garment> list = new ArrayList<Garment>();
        try {
            Cursor cursor = new GetReadableDatabaseTask().execute("a").get();
            while(cursor.moveToNext()) {
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("Name"));
                String weightString = cursor.getString(cursor.getColumnIndexOrThrow("Weight"));
                int weight = Integer.parseInt(weightString);
                String maxWashTempString = cursor.getString(cursor.getColumnIndexOrThrow("MaxWashTemp"));
                int maxWashTemp = Integer.parseInt(maxWashTempString);
                String spinningLimitString = cursor.getString(cursor.getColumnIndexOrThrow("SpinningLimit"));
                int spinningLimit = Integer.parseInt(spinningLimitString);
                String yarnTwistString = cursor.getString(cursor.getColumnIndexOrThrow("YarnTwist"));
                int yarnTwist = Integer.parseInt(yarnTwistString);
                String isColorBleedSensitiveString = cursor.getString(cursor.getColumnIndexOrThrow("IsColorBleedSensitive"));
                boolean isColorBleedSensitive = Boolean.parseBoolean(isColorBleedSensitiveString);
                Garment garment = new Garment(0, name, weight, maxWashTemp, spinningLimit, yarnTwist, isColorBleedSensitive);
                list.add(garment);
            }
            cursor.close();
        } catch(java.lang.InterruptedException e) {
            Toast.makeText(getApplicationContext(), "loadGarmentsFromDb InterruptedException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (java.util.concurrent.ExecutionException e) {
            Toast.makeText(getApplicationContext(), "loadGarmentsFromDb ExecutionException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "loadGarmentsFromDb Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return list;
    }

    //Create an asynchronous task to be done in the background (not on the main thread)
    //The task reads garment entries from the database
    private class GetReadableDatabaseTask extends AsyncTask<String, Void, Cursor> {
        protected Cursor doInBackground(String... strings) {
            SQLiteDatabase mdb = dbHelper.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    "Name",
                    "Fabric",
                    "Weight",
                    "MaxWashTemp",
                    "SuggestedWashTemp",
                    "SpinningLimit",
                    "YarnTwist",
                    "IsColorBleedSensitive"
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder =
                    "Name ASC";

            Cursor cursor = mdb.query(
                    "Garment",
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            return cursor;
        }
    }

    //Get selected garments
    private ArrayList<Garment> getSelectedGarments() {
        ArrayList<Garment> selectedGarments = new ArrayList<Garment>();
        for(Garment g : garments) {
            if(g.isIncludedInWash()) {
                selectedGarments.add(g);
            }
        }
        return selectedGarments;
    }

    //Build and return a suggestion string suggesting
    //a recommended maximum temperature and spinning limit and containing warnings.
    private String generateSuggestionString(int temp, int spin, int weight, int yarnTwist, ArrayList<String> warnings) {
        String suggestionString = "";
        for(String w : warnings) {
            suggestionString += w;
        }
        suggestionString += "\n";
        suggestionString += "Suggested max temperature for mix: " + temp + "\n";
        suggestionString += "Suggested spinning limit for mix: " + spin+ "\n";
        suggestionString += "Suggested weight limit for mix: " + weight+ "\n";
        suggestionString += "Suggested YarnTwist limit for mix: " + yarnTwist+ "\n";

        return suggestionString;
    }

    //Go to the EditGarmentActivity
    private void openEditGarmentActivity() {
        try {
            Intent intent = new Intent(this, EditGarmentActivity.class);
            intent.putExtra("editstate", "new");
            startActivity(intent);
        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "open editgarmentactivity exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
