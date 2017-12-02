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

    public Logic(Set<Garment> garments) {
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

    public HashMap<String, Boolean> getWarnings() {
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
}
