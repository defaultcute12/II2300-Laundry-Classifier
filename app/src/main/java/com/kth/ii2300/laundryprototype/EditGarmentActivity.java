package com.kth.ii2300.laundryprototype;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class EditGarmentActivity extends AppCompatActivity {

    //Class-global variables
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_garment);

        //instantiate class-global variables
        dbHelper = new DatabaseHelper(this);

        //Get object representations of view items
        final EditText edtGarmentName = (EditText) findViewById(R.id.edtNewClothingName);
        final EditText edtGarmentWeight = (EditText) findViewById(R.id.edtNewClothingWeight);
        final EditText edtGarmentMaxTemp = (EditText) findViewById(R.id.edtNewClothingMaxTemp);
        final EditText edtGarmentSpinningLimit = (EditText) findViewById(R.id.edtNewClothingSpinningLimit);
        final EditText edtGarmentYarnTwist = (EditText) findViewById(R.id.edtNewClothingYarnTwist);
        final CheckBox chkGarmentColorBleed = (CheckBox) findViewById(R.id.chkNewClothingColorBleed);
        Button btnConfirmEditGarment = (Button) findViewById(R.id.btnConfirmEditGarment);

        //Create a collection of required fields for creating a new garment
        final ArrayList<EditText> requiredFields = new ArrayList<EditText>();
        requiredFields.add(edtGarmentName);
        requiredFields.add(edtGarmentMaxTemp);

        //Implement a listener for the confirm button
        //Checks if "required" fields (Garment name and max temp) are filled.
        //  Creates a new garment object and
        //  ((adds it to the global collection) or (forwards it to MainActivity))
        //  Goes to the MainActivity
        //Else
        //  displays a message to the user notifying of missing values
        btnConfirmEditGarment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(areRequiredFieldsFilled(requiredFields)) {
                    //The required fields which we have verified to be nonempty
                    String newName = edtGarmentName.getEditableText().toString();
                    int newMaxTemp = Integer.parseInt(edtGarmentMaxTemp.getEditableText().toString());
                    //The rest of the new Garment values will have the value of their corresponding
                    //EditText fields or a zero value
                    int newWeight = getIntFromEditText(edtGarmentWeight);
                    int newSpinningLimit = getIntFromEditText(edtGarmentSpinningLimit);
                    int newYarnTwist = getIntFromEditText(edtGarmentYarnTwist);
                    boolean isColorBleedSensitive = chkGarmentColorBleed.isChecked();
                    //What should we do?
                    //If the activity is in a "new" editstate
                    //  Create a new Garment object
                    //Else
                    //  Update and existing one
                    String editstate = getIntent().getStringExtra("editstate");
                    //if(getIntent().getStringExtra("editstate") == "new") {
                    if(editstate.equals("new")) {
                        Garment newGarment = new Garment(99, newName, newWeight, newMaxTemp, newSpinningLimit, newYarnTwist, isColorBleedSensitive);
                        saveNewGarmentToDb(newGarment);
                    }
                    openMainActivity();
                }
                else {
                    //displayFieldMissingPrompt();
                }
            }
        });
    }

    //Checks if "required" fields (Garment name and max temp) are filled.
    //Returns true if they are, false otherwise
    private boolean areRequiredFieldsFilled(ArrayList<EditText> requiredFields) {
        boolean areRequiredFieldsFilled = true;
        //Go through every required edittext object
        //We only need to find one that has an empty text value
        for(EditText editText : requiredFields) {
            if(editText.getEditableText().toString().length() == 0) {
                areRequiredFieldsFilled = false;
                break;
            }
        }
        return areRequiredFieldsFilled;
    }

    //Returns an int cast of the string value of editText
    //If the string value is empty, returns 0
    private int getIntFromEditText(EditText editText) {
        int integerValue = 0;
        if(editText.getEditableText().toString().length() > 0) {
            integerValue = Integer.parseInt(editText.getEditableText().toString());
        }
        return integerValue;
    }

    //Start an asynchronous task that writes a new garment entry to the database
    //Do error handling
    private void saveNewGarmentToDb(Garment garment) {
        try {
            Long newGarmentId
                    = new GetWriteableDatabaseTask()
                        .execute(garment.getGarmentClassName(),
                                    Integer.toString(garment.getMaxWashTemp()),
                                    Integer.toString(garment.getWeight()),
                                    Integer.toString(garment.getSpinningLimit()),
                                    Integer.toString(garment.getYarnTwist()),
                                    Boolean.toString(garment.isColorBleedSensitive())).get();
        } catch(java.lang.InterruptedException e) {
            Toast.makeText(getApplicationContext(), "saveNewProjectToDb InterruptedException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (java.util.concurrent.ExecutionException e) {
            Toast.makeText(getApplicationContext(), "saveNewProjectToDb ExecutionException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "saveNewProjectToDb Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Create an asynchronous task to be done in the background (not on the main thread)
    //The task writes a new garment entry to the database
    private class GetWriteableDatabaseTask extends AsyncTask<String, Void, Long> {
        protected Long doInBackground(String... strings) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put("Name", strings[0]);
            values.put("MaxWashTemp", Integer.parseInt(strings[1]));
            values.put("Weight", Integer.parseInt(strings[2]));
            values.put("SpinningLimit", Integer.parseInt(strings[3]));
            values.put("YarnTwist", Integer.parseInt(strings[4]));
            if(strings[5].equals("true")) {
                values.put("IsColorBleedSensitive", 1);
            } else {
                values.put("IsColorBleedSensitive", 0);
            }

            // Insert the new row, returning the primary key value of the new row
            Long newRowId = null;
            try {
                newRowId = db.insertOrThrow("Garment", null, values);
            }
            catch (SQLException e) {
                Toast.makeText(getApplicationContext(), " GetWriteableDatabaseTask SQLException: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return newRowId;
        }
    }

    //Go to the MainActivity
    private void openMainActivity() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "open mainactivity exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
