package com.kth.ii2300.laundryprototype;

/**
 * Created by Mallu on 02-12-2017.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Logic {
    //private WashingMachine machine;
    //private WashingMachine proposition;
    private ArrayList<Garment> garments;
    private ArrayList<Garment> finalGarments;
    private HashMap<String, Boolean> warnings;

    public Logic(ArrayList<Garment> garments) {
        //this.machine = new WashingMachine(machine);
        //this.proposition = new WashingMachine(machine);
        this.garments = new ArrayList<Garment>();
        this.garments.addAll(garments);
        this.finalGarments = new ArrayList<Garment>();
        this.finalGarments.addAll(garments);
        this.warnings = new HashMap<String, Boolean>();
        this.warnings.put("temperature", false);
        this.warnings.put("spin", false);
        this.warnings.put("weight", false);
        this.warnings.put("time", false);
        this.warnings.put("centrifuging", false);
        this.warnings.put("cycles", false);
    }

    //Return an ArrayList<String> containing warnings based on general
    //principles.
    public ArrayList<String> getWarnings() {
        ArrayList<String> warnings = new ArrayList<String>();
        for(Garment g : garments) {
            //Should we generate a colour bleed warning?
            //  "garments.size() > 1" is a dummy check while we only have 1 garment type
            //  possible of generating this value. We do not want to show the warning when silk
            //  is theonly type chosen.
            if(g.isColorBleedSensitive() && garments.size() > 1) {
                warnings.add(g.getGarmentClassName() + " has low colour bleed resistance, it should be washed separately.");
            }
        }
        return warnings;
    }

    public ArrayList<Garment> getGarments() {
        return garments;
    }

    public ArrayList<Garment> getFinalGarments() {
        return finalGarments;
    }

    public void process() {

    }

    private Garment getMax(ArrayList<Garment> garments) {
        Garment gTmp = garments.get(0);
        return gTmp;
    }

    //Return the minimum "Maximum temperature" value
    //for the garment types in the garments collection
    public int getMinTemp() {
        int minTemp = 10000;
        for(Garment g : garments) {
            if(g.getMaxWashTemp() < minTemp) {
                minTemp = g.getMaxWashTemp();
            }
        }
        return minTemp;
    }

    //Return the minimum "Maximum spinning limit" value
    //for the garment types int the garments collection
    public int getMinSpin() {
        int minSpin = 10000;
        for(Garment g : garments) {
            if(g.getMaxWashTemp() < minSpin) {
                minSpin = g.getSpinningLimit();
            }
        }
        return minSpin;
    }

    //Return minimum weight allowed from the selected garments
    public int getMinWeight() {
        int minWeight = 10;
        for(Garment g: garments) {
            if(g.getWeight() < minWeight) {
                minWeight = g.getWeight();
            }
        }
        return minWeight;
    }

    //Return minimum YarnTwistValue allowed from the selected garments
    public int getMinYarnTwist() {
        int minYarnTwist = 200;
        for(Garment g: garments) {
            if(g.getWeight() < minYarnTwist) {
                minYarnTwist = g.getYarnTwist();
            }
        }
        return minYarnTwist;
    }
}
