package com.kth.ii2300.laundryprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Elvar on 28.11.2017.
 *
 * Custom ArrayAdapter for displaying rows of garments in
 * a listview in MainActivity.
 *
 * See http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
 * for an explanation
 */

public class GarmentAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Garment> garments;

    public GarmentAdapter(Context context, int layoutResourceId, ArrayList<Garment> garments) {
        super(context, layoutResourceId, garments);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.garments = garments;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GarmentHolder holder = null;

        //Connect GarmentHolder object to view items
        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GarmentHolder();
            holder.txtGarmentClassName = (TextView)row.findViewById(R.id.garmentLabel);
            holder.chkIsIncluded = (CheckBox)row.findViewById(R.id.garmentCheckBox);
            holder.edtButton = (Button)row.findViewById((R.id.garmentEditbtn));

            row.setTag(holder);
        } else {
            holder = (GarmentHolder)row.getTag();
        }

        //Get display values for this GarmentHolder object from the garments collection
        final String garmentClassName = garments.get(position).getGarmentClassName();
        final boolean isIncluded = false;
        //Set the display values
        holder.txtGarmentClassName.setText(garmentClassName);
        holder.chkIsIncluded.setChecked(isIncluded);

        //Implement a listener for the check box which
        //toggles the isIncluded property of the garment
        holder.chkIsIncluded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                garments.get(position).toggleIsIncluded();
            }
        });

        //Implement a listener for the editbutton which
        //opens the editGarment activity with values for this specific garment
        holder.edtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditGarmentActivity(garments.get(position));
            }
        });

        return row;
    }

    static class GarmentHolder {
        TextView txtGarmentClassName;
        CheckBox chkIsIncluded;
        Button edtButton;
    }

    //Open the editGarment activity in "edit" mode
    //by sending an intent with editstate "edit" and
    //garmentName "name"
    private void openEditGarmentActivity(Garment garment) {
        try {
            Intent intent = new Intent(this.context, EditGarmentActivity.class);
            intent.putExtra("editstate", "edit");
            intent.putExtra("garmentName", garment.getGarmentClassName());
            this.context.startActivity(intent);
        }
        catch(Exception e) {
            Toast.makeText(this.context.getApplicationContext(), "open editgarmentactivity exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
