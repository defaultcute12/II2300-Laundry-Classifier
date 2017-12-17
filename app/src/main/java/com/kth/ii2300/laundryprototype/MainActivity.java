package com.kth.ii2300.laundryprototype;

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
        final TextView txtSuggestion = (TextView) findViewById(R.id.txtSuggestion);

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
        garments.add(new Garment(1, "Cotton", 3, 60, 1800, 3, 333));
        garments.add(new Garment(2, "Denim", 5, 40, 900, 3, 99));
        garments.add(new Garment(3, "Nylon", 2, 30, 1200, 2, 12));
        garments.add(new Garment(4, "Silk", 1, 15, 300, 1, 134));
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
}
