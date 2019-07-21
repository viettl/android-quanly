package com.example.quanli.model;

public class DBModel {

    private  int modelId;
    private String modelName;

    public DBModel() {
    }

    public DBModel(String modelName, String modelPhone, String modelAddress, int modelDebt, double modelInterest_rate, String modelDate, String modelInterest_date, String modelDescription) {
        this.modelName = modelName;
        this.modelPhone = modelPhone;
        this.modelAddress = modelAddress;
        this.modelDebt = modelDebt;
        this.modelInterest_rate = modelInterest_rate;
        this.modelDate = modelDate;
        this.modelInterest_date = modelInterest_date;
        this.modelDescription = modelDescription;
    }

    public int getModelId() {
        return modelId;
    }

    public DBModel(int modelId, String modelName, String modelPhone, String modelAddress, int modelDebt, double modelInterest_rate, String modelDate, String modelInterest_date, String modelDescription) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.modelPhone = modelPhone;
        this.modelAddress = modelAddress;
        this.modelDebt = modelDebt;
        this.modelInterest_rate = modelInterest_rate;
        this.modelDate = modelDate;
        this.modelInterest_date = modelInterest_date;
        this.modelDescription = modelDescription;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelPhone() {
        return modelPhone;
    }

    public void setModelPhone(String modelPhone) {
        this.modelPhone = modelPhone;
    }

    public String getModelAddress() {
        return modelAddress;
    }

    public void setModelAddress(String modelAddress) {
        this.modelAddress = modelAddress;
    }

    public int getModelDebt() {
        return modelDebt;
    }

    public void setModelDebt(int modelDebt) {
        this.modelDebt = modelDebt;
    }

    public double getModelInterest_rate() {
        return modelInterest_rate;
    }

    public void setModelInterest_rate(double modelInterest_rate) {
        this.modelInterest_rate = modelInterest_rate;
    }

    public String getModelDate() {
        return modelDate;
    }

    public void setModelDate(String modelDate) {
        this.modelDate = modelDate;
    }

    public String getModelInterest_date() {
        return modelInterest_date;
    }

    public void setModelInterest_date(String modelInterest_date) {
        this.modelInterest_date = modelInterest_date;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    private String modelPhone;
    private String modelAddress;
    private int modelDebt;
    private double modelInterest_rate;
    private String modelDate;
    private String modelInterest_date;
    private String modelDescription;
}
