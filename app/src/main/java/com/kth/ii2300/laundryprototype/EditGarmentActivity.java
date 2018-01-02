package com.kth.ii2300.laundryprototype;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditGarmentActivity extends AppCompatActivity {

    //Class-global variables
    private DatabaseHelper dbHelper;
    //premission codes
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    //garment name
    String garmentName = "";
    //URI that will hold the imageUri (image info) for a photo
    Uri imageUri;
    //ImageView that will display a thumbnail of a photo
    ImageView imgGarmentPhoto;
    //We want to write a photo URI to database if a photo was taken,
    //but we do not want to write the same <Garment, URI> combo more than once
    Boolean hasPhotoBeenTaken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_garment);

        //instantiate class-global variables
        dbHelper = new DatabaseHelper(this);
        hasPhotoBeenTaken = false;

        //Get object representations of view items
        final EditText edtGarmentName = (EditText) findViewById(R.id.edtNewClothingName);
        final EditText edtGarmentWeight = (EditText) findViewById(R.id.edtNewClothingWeight);
        final EditText edtGarmentMaxTemp = (EditText) findViewById(R.id.edtNewClothingMaxTemp);
        final EditText edtGarmentSpinningLimit = (EditText) findViewById(R.id.edtNewClothingSpinningLimit);
        final EditText edtGarmentYarnTwist = (EditText) findViewById(R.id.edtNewClothingYarnTwist);
        final CheckBox chkGarmentColorBleed = (CheckBox) findViewById(R.id.chkNewClothingColorBleed);
        Button btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        Button btnConfirmEditGarment = (Button) findViewById(R.id.btnConfirmEditGarment);
        imgGarmentPhoto = (ImageView) findViewById(R.id.thumbnailImageView);

        //Create a collection of required fields for creating a new garment
        final ArrayList<EditText> requiredFields = new ArrayList<EditText>();
        requiredFields.add(edtGarmentName);
        requiredFields.add(edtGarmentMaxTemp);

        //If we are opening in an "edit" state,
        //load values for garment "garmentName" into editFields
        if(getIntent().getStringExtra("editstate").equals("edit")) {
            garmentName = getIntent().getStringExtra("garmentName");
            Garment garment = loadGarmentFromDb(garmentName);
            edtGarmentName.setText(garment.getGarmentClassName());
            edtGarmentWeight.setText(String.valueOf(garment.getWeight()));
            edtGarmentWeight.setText(String.valueOf(garment.getWeight()));
            edtGarmentMaxTemp.setText(String.valueOf(garment.getMaxWashTemp()));
            edtGarmentSpinningLimit.setText(String.valueOf(garment.getSpinningLimit()));
            edtGarmentYarnTwist.setText(String.valueOf(garment.getYarnTwist()));
            chkGarmentColorBleed.setChecked(garment.isColorBleedSensitive());
            loadPhotoInThumbnail();
            //Lock the garment NAme edit field. We use it for the update statement
            edtGarmentName.setEnabled(false);
            edtGarmentName.setFocusable(false);
        }

        //Implement a listener for the "take photo" button
        //If the garment name edittext is empty we return a warning to the user and do nothing
        //because we will use the value from the edittext as a filename for the photo.
        //Open a camera activity that returns the taken photo to this activity
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String garmentName = edtGarmentName.getEditableText().toString();
                if(garmentName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill out a garment name", Toast.LENGTH_LONG).show();
                    return;
                }

                openDefaultCameraApp(garmentName);
            }
        });

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
                    if(editstate.equals("new")) {
                        Garment newGarment = new Garment(99, newName, newWeight, newMaxTemp, newSpinningLimit, newYarnTwist, isColorBleedSensitive);
                        saveNewGarmentToDb(newGarment);
                        savePhotoToDB(newGarment.getGarmentClassName(), imageUri);
                    }
                    else if(editstate.equals("edit")) {
                        Garment updateGarment = new Garment(99, garmentName, newWeight, newMaxTemp, newSpinningLimit, newYarnTwist, isColorBleedSensitive);
                        updateGarmentInDb(updateGarment);
                        if(hasPhotoBeenTaken) {
                            savePhotoToDB(updateGarment.getGarmentClassName(), imageUri);
                        }
                    }
                    openMainActivity();
                }
                else {
                    //displayFieldMissingPrompt();
                }
            }
        });

        //Load photos from database for this garment
        loadPhotoInThumbnail();
    }

    private void loadPhotoInThumbnail() {
        try {
            //Add image thumbnails
            ArrayList<Uri> imgURIs = getImagesFromDb(this.garmentName);

            // Create a new ArrayAdapter
            //final GarmentPhotoThumbnailAdapter gridViewArrayAdapter = new GarmentPhotoThumbnailAdapter(this,R.layout.thumbnail_projectphotos, imgURIs);

            if(imgURIs.size() > 0) {
                imgGarmentPhoto.setImageURI(imgURIs.get(0));
            }
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(), "imagedisplay Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            Toast.makeText(getApplicationContext(), "saveNewGarmentToDb InterruptedException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (java.util.concurrent.ExecutionException e) {
            Toast.makeText(getApplicationContext(), "saveNewGarmentToDb ExecutionException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "saveNewGarmentToDb Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    //Start an asynchronous task that updates an existing garment entry to the database
    //Do error handling
    private void updateGarmentInDb(Garment garment) {
        try {
            int rowsAffected
                    = new GetUpdateDatabaseTask()
                    .execute(garment.getGarmentClassName(),
                            Integer.toString(garment.getMaxWashTemp()),
                            Integer.toString(garment.getWeight()),
                            Integer.toString(garment.getSpinningLimit()),
                            Integer.toString(garment.getYarnTwist()),
                            Boolean.toString(garment.isColorBleedSensitive())).get();

            if (rowsAffected != 1) {
                Toast.makeText(getApplicationContext(), "Expected update of 1 row, actual update of " + rowsAffected + " rows.", Toast.LENGTH_LONG).show();
            }
        } catch(java.lang.InterruptedException e) {
            Toast.makeText(getApplicationContext(), "updateGarmentInDb InterruptedException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (java.util.concurrent.ExecutionException e) {
            Toast.makeText(getApplicationContext(), "updateGarmentInDb ExecutionException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "updateGarmentInDb Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Create an asynchronous task to be done in the background (not on the main thread)
    //The task updates an existing garment entry to the database
    private class GetUpdateDatabaseTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... strings) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put("MaxWashTemp", Integer.parseInt(strings[1]));
            values.put("Weight", Integer.parseInt(strings[2]));
            values.put("SpinningLimit", Integer.parseInt(strings[3]));
            values.put("YarnTwist", Integer.parseInt(strings[4]));
            if (strings[5].equals("true")) {
                values.put("IsColorBleedSensitive", 1);
            } else {
                values.put("IsColorBleedSensitive", 0);
            }

            String whereClause = "Name = ?";
            String[] whereArgs = {strings[0]};

            // Insert the new row, returning the primary key value of the new row
            int rowsAffected = -1;
            try {
                rowsAffected = db.update("Garment", values, whereClause, whereArgs);
            } catch (SQLException e) {
                Toast.makeText(getApplicationContext(), " GetWriteableDatabaseTask SQLException: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return rowsAffected;
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

    //Open the default device camera to take a photo
    //Taken photo is returned to our application
    public void openDefaultCameraApp(String garmentName) {
        try {
            //Save the garmentname To a global variable to make it accessible
            //to the askForCameraPermission() and askForWriteExternalStoragePermission() methods
            this.garmentName = garmentName;
            if(!hasCameraPermission()) {
                askForCameraPermission();
                return;
            }
            if(!hasWriteExternalStoragePermission()) {
                askForWriteExternalStoragePermission();
                return;
            }
            //define the file-name to save photo taken by Camera activity
            String fileName = garmentName + ".jpg";
            //create parameters for Intent with filename
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            //create new Intent
            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

            //Start an image capture (camera) activity
            startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Unable to launch camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Check if the app has permission to access the camera
    protected boolean hasCameraPermission() {
        boolean result = false;
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            result = true;
        }
        return result;
    }

    //Check if the app has permission to wriite to external storage
    protected boolean hasWriteExternalStoragePermission() {
        boolean result = false;
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            result = true;
        }
        return result;
    }

    //Ask the user for permission to access the camera
    protected void askForCameraPermission() {
        // Should we show an explanation?
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            //showMessage("You need to allow access to the Camera in device settings. (Apps->LaundryPrototype->Permissions)");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

            // MY_PERMISSIONS_REQUEST_CAMERA is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    //Ask the user for permission to write to external storage
    protected void askForWriteExternalStoragePermission() {
        // Should we show an explanation?
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            //showMessage("You need to allow access for saving to disk in device settings. (Apps->LaundryPrototype->Permissions)");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    //Show an OK-dialog containing message
    private void showMessage(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton("OK", null)
                .create()
                .show();
    }

    //Get the results of asking the user for permissions necessary to take and store a photo
    //and take appropriate actions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // camera-related task you need to do.
                    Toast.makeText(getApplicationContext(), "Camera permission granted", Toast.LENGTH_LONG).show();
                    openDefaultCameraApp(garmentName);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Camera permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "Write external storage permission granted", Toast.LENGTH_LONG).show();
                    openDefaultCameraApp(garmentName);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Write external storage permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Picture was taken", Toast.LENGTH_SHORT).show();
                //savePhotoToDB(garmentName, imageUri);
                imgGarmentPhoto.setImageURI(imageUri);
                this.hasPhotoBeenTaken = true;
                //loadPhotoInThumbnail();
                //openProjectDisplay(projectTitle);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
            }
        }
    }

    private void savePhotoToDB(String garmentName, Uri imageUri) {
        try {
            new GetWriteableDatabaseForImageTask().execute(garmentName, imageUri.toString()).get();
        } catch(java.lang.InterruptedException e) {
            Toast.makeText(getApplicationContext(), "savePhotoToDB InterruptedException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (java.util.concurrent.ExecutionException e) {
            Toast.makeText(getApplicationContext(), "savePhotoToDB ExecutionException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "savePhotoToDB Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class GetWriteableDatabaseForImageTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            SQLiteDatabase mdb = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put("Name", strings[0]);
            values.put("ImageUri", strings[1]);

            // Insert the new row, returning the primary key value of the new row
            try {
                mdb.insertOrThrow("GarmentImage", null, values);
            }
            catch (SQLException e) {
                Toast.makeText(getApplicationContext(), " GetWriteableDatabaseForImagesTask SQLException: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }
    }

    private ArrayList<Uri> getImagesFromDb(String garmentName) {
        ArrayList<Uri> list = new ArrayList<Uri>();
        try {
            Cursor cursor = new GetReadableDatabaseTaskForImages().execute(garmentName).get();
            while(cursor.moveToNext()) {
                Uri uri = Uri.parse(cursor.getString(
                        cursor.getColumnIndexOrThrow("ImageUri")));
                list.add(uri);
            }
            cursor.close();
        } catch(java.lang.InterruptedException e) {
            Toast.makeText(getApplicationContext(), "getImagesFromDb InterruptedException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (java.util.concurrent.ExecutionException e) {
            Toast.makeText(getApplicationContext(), "getImagesFromDb ExecutionException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "getImagesFromDb Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return list;
    }

    private class GetReadableDatabaseTaskForImages extends AsyncTask<String, Void, Cursor> {
        protected Cursor doInBackground(String... strings) {
            SQLiteDatabase mdb = dbHelper.getReadableDatabase();
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    "ImageUri"
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = "Name" + " = ?";
            String[] selectionArgs = { strings[0] };

            String orderBy = "CreatedAt DESC";

            Cursor cursor = mdb.query(
                    "GarmentImage",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    orderBy
            );

            return cursor;
        }
    }

    private Garment loadGarmentFromDb(String garmentName) {
        Garment garment = new Garment();
        try {
            Cursor cursor = new GetReadableDatabaseTaskForGarment().execute(garmentName).get();
            while(cursor.moveToNext()) {
                garment.setRfidTagId(cursor.getInt(cursor.getColumnIndexOrThrow("Garment_Id")));
                garment.setGarmentClassName(cursor.getString(cursor.getColumnIndexOrThrow("Name")));
                garment.setMaxWashTemp(cursor.getInt(cursor.getColumnIndexOrThrow("MaxWashTemp")));
                garment.setWeight(cursor.getInt(cursor.getColumnIndexOrThrow("Weight")));
                garment.setSpinningLimit(cursor.getInt(cursor.getColumnIndexOrThrow("SpinningLimit")));
                garment.setYarnTwist(cursor.getInt(cursor.getColumnIndexOrThrow("YarnTwist")));
                int colorBleedSensitiveValue = cursor.getInt(cursor.getColumnIndexOrThrow("IsColorBleedSensitive"));
                if(colorBleedSensitiveValue == 1) {
                    garment.setColorBleedSensitive(true);
                } else {
                    garment.setColorBleedSensitive(false);
                }
            }
            cursor.close();
        } catch(java.lang.InterruptedException e) {
            Toast.makeText(getApplicationContext(), "getGarmentFromDb InterruptedException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (java.util.concurrent.ExecutionException e) {
            String test = "abc";
            Toast.makeText(getApplicationContext(), "getGarmentFromDb ExecutionException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "getGarmentFromDb Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return garment;
    }

    private class GetReadableDatabaseTaskForGarment extends AsyncTask<String, Void, Cursor> {
        protected Cursor doInBackground(String... strings) {
            SQLiteDatabase mdb = dbHelper.getReadableDatabase();
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    "Garment_Id",
                    "Name",
                    "MaxWashTemp",
                    "Weight",
                    "SpinningLimit",
                    "YarnTwist",
                    "IsColorBleedSensitive"
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = "Name" + " = ?";
            String[] selectionArgs = { strings[0] };

            Cursor cursor = mdb.query(
                    "Garment",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    "1"
            );

            return cursor;
        }
    }
}
