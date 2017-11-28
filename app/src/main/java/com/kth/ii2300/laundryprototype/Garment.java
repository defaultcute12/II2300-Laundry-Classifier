package com.kth.ii2300.laundryprototype;

/**
 * Created by Elvar on 28.11.2017.
 */

public class Garment {
    //Descriptive properties
    private int rfidTagId;
    private String garmentClassName;
    private int weight;
    private int maxWashTemp;
    private int spinningLimit;
    private int colourBleedResist;
    private int yarnTwist;

    //Other Properties
    private boolean isIncludedInWash;


    public Garment(int rfidTagId, String garmentClassName, int weight,
                   int maxWashTemp, int spinningLimit, int colourBleedResist,
                   int yarnTwist) {
        this.rfidTagId = rfidTagId;
        this.garmentClassName = garmentClassName;
        this.weight = weight;
        this.maxWashTemp = maxWashTemp;
        this.spinningLimit = spinningLimit;
        this.colourBleedResist = colourBleedResist;
        this.yarnTwist = yarnTwist;

        this.isIncludedInWash = false;
    }

    public int getRfidTagId() {
        return rfidTagId;
    }

    public String getGarmentClassName() {
        return garmentClassName;
    }

    public int getWeight() {
        return weight;
    }

    public int getMaxWashTemp() {
        return maxWashTemp;
    }

    public int getSpinningLimit() {
        return spinningLimit;
    }

    public int getColourBleedResist() {
        return colourBleedResist;
    }

    public String getColourBleedResistAsString() {
        String resistanceValue = "";
        switch(colourBleedResist) {
            case 1: resistanceValue = "Low";
                    break;
            case 2: resistanceValue = "Medium";
                    break;
            case 3: resistanceValue = "High";
                    break;
            default: resistanceValue = "N/A";
        }
        return resistanceValue;
    }

    public int getYarnTwist() {
        return yarnTwist;
    }

    public boolean isIncludedInWash() {
        return isIncludedInWash;
    }

    public void toggleIsIncluded() {
        isIncludedInWash = !isIncludedInWash;
    }
}
