package com.cluster.taxiuser.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EstimateFare implements Serializable {
    @SerializedName("estimated_fare")
    @Expose
    private Double estimatedFare;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("surge")
    @Expose
    private Integer surge;
    @SerializedName("surge_value")
    @Expose
    private String surgeValue;
    @SerializedName("tax_price")
    @Expose
    private Double taxPrice;
    @SerializedName("base_price")
    @Expose
    private Double basePrice;
    @SerializedName("wallet_balance")
    @Expose
    private Double walletBalance;

    public Double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(Double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getSurge() {
        return surge;
    }

    public void setSurge(Integer surge) {
        this.surge = surge;
    }

    public String getSurgeValue() {
        return surgeValue;
    }

    public void setSurgeValue(String surgeValue) {
        this.surgeValue = surgeValue;
    }

    public Double getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(Double taxPrice) {
        this.taxPrice = taxPrice;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }
}
