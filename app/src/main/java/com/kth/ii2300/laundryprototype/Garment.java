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
    private int yarnTwist;
    private boolean isColorBleedSensitive;

    //Other Properties
    private boolean isIncludedInWash;

    public Garment() {};


    public Garment(int rfidTagId, String garmentClassName, int weight,
                   int maxWashTemp, int spinningLimit, int yarnTwist,
                   boolean iscolorBleedSensitive) {
        this.rfidTagId = rfidTagId;
        this.garmentClassName = garmentClassName;
        this.weight = weight;
        this.maxWashTemp = maxWashTemp;
        this.spinningLimit = spinningLimit;
        this.isColorBleedSensitive = iscolorBleedSensitive;
        this.yarnTwist = yarnTwist;

        this.isIncludedInWash = false;
    }

    public void setRfidTagId(int rfidTagId) {
        this.rfidTagId = rfidTagId;
    }

    public void setGarmentClassName(String garmentClassName) {
        this.garmentClassName = garmentClassName;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setMaxWashTemp(int maxWashTemp) {
        this.maxWashTemp = maxWashTemp;
    }

    public void setSpinningLimit(int spinningLimit) {
        this.spinningLimit = spinningLimit;
    }

    public void setYarnTwist(int yarnTwist) {
        this.yarnTwist = yarnTwist;
    }

    public void setColorBleedSensitive(Boolean isColorBleedSensitive) {
        this.isColorBleedSensitive = isColorBleedSensitive;
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

    public int getYarnTwist() {
        return yarnTwist;
    }

    public boolean isColorBleedSensitive() {
        return isColorBleedSensitive;
    }

    public boolean isIncludedInWash() {
        return isIncludedInWash;
    }

    public void toggleIsIncluded() {
        isIncludedInWash = !isIncludedInWash;
    }
}
