package com.example.demo.Model;


import java.time.LocalDate;

public class StockData {
    private LocalDate date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
    private long unadjustedVolume;
    private double adjClose;

    private double change;
    private double changePercent;
    private double vwap;
    private String label;
    private double changeOverTime;

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public double getVwap() {
        return vwap;
    }

    public void setVwap(double vwap) {
        this.vwap = vwap;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getChangeOverTime() {
        return changeOverTime;
    }

    public void setChangeOverTime(double changeOverTime) {
        this.changeOverTime = changeOverTime;
    }

    public long getUnadjustedVolume() {
        return unadjustedVolume;
    }

    public void setUnadjustedVolume(long unadjustedVolume) {
        this.unadjustedVolume = unadjustedVolume;
    }

    // Constructors, getters, and setters
    public StockData() {
    }

    public double getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(double adjClose) {
        this.adjClose = adjClose;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}
