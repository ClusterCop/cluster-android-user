package com.cluster.taxiuser.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("request_id")
    @Expose
    private Integer requestId;
    @SerializedName("promocode_id")
    @Expose
    private Object promocodeId;
    @SerializedName("payment_id")
    @Expose
    private Object paymentId;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("fixed")
    @Expose
    private Double fixed;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("minute")
    @Expose
    private Double minute;
    @SerializedName("provider_pay")
    @Expose
    private Double providerPay;
    @SerializedName("commision")
    @Expose
    private Double commision;
    @SerializedName("discount")
    @Expose
    private Double discount;
    @SerializedName("tax")
    @Expose
    private Double tax;
    @SerializedName("wallet")
    @Expose
    private Double wallet;
    @SerializedName("surge")
    @Expose
    private Double surge;
    @SerializedName("tips")
    @Expose
    private Double tips;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("payable")
    @Expose
    private Double payable;
    @SerializedName("waiting_fare")
    @Expose
    private Double waitingFare;
    @SerializedName("provider_commission")
    @Expose
    private Double providerCommission;
    @SerializedName("hour")
    @Expose
    private Double hour;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Object getPromocodeId() {
        return promocodeId;
    }

    public void setPromocodeId(Object promocodeId) {
        this.promocodeId = promocodeId;
    }

    public Object getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Object paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Double getFixed() {
        return fixed;
    }

    public void setFixed(Double fixed) {
        this.fixed = fixed;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getCommision() {
        return commision;
    }

    public void setCommision(Double commision) {
        this.commision = commision;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getWallet() {
        return wallet;
    }

    public void setWallet(Double wallet) {
        this.wallet = wallet;
    }

    public Double getSurge() {
        return surge;
    }

    public void setSurge(Double surge) {
        this.surge = surge;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getPayable() {
        return payable;
    }

    public void setPayable(Double payable) {
        this.payable = payable;
    }

    public Double getWaitingFare() {
        return waitingFare;
    }

    public void setWaitingFare(Double waitingFare) {
        this.waitingFare = waitingFare;
    }

    public Double getProviderCommission() {
        return providerCommission;
    }

    public void setProviderCommission(Double providerCommission) {
        this.providerCommission = providerCommission;
    }

    public Double getProviderPay() {
        return providerPay;
    }

    public void setProviderPay(Double providerPay) {
        this.providerPay = providerPay;
    }

    public Double getMinute() {
        return minute;
    }

    public void setMinute(Double minute) {
        this.minute = minute;
    }

    public Double getHour() {
        return hour;
    }

    public void setHour(Double hour) {
        this.hour = hour;
    }

    public Double getTips() {
        return tips;
    }

    public void setTips(Double tips) {
        this.tips = tips;
    }
}
