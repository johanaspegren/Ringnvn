package com.aspegrenide.ringnvn;


import java.util.Date;

public class CallDetails {

    private String caller;
    private String phoneNr;
    private Long dateStartInMs;
    private Long dateStopInMs;

    public CallDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // caller - who is calling? phoneNr - who is called?
    public CallDetails(String caller, String phoneNr, Long dateStartInMs) {
        this.caller = caller;
        this.phoneNr = phoneNr;
        this.dateStartInMs = dateStartInMs;
    }

    public String getPhoneNr() {
        return phoneNr;
    }

    public void setPhoneNr(String phoneNr) {
        this.phoneNr = phoneNr;
    }

    public Long getDateStartInMs() {
        return dateStartInMs;
    }

    public void setDateStartInMs(Long dateStartInMs) {
        this.dateStartInMs = dateStartInMs;
    }

    public Long getDateStopInMs() {
        return dateStopInMs;
    }

    public void setDateStopInMs(Long dateStopInMs) {
        this.dateStopInMs = dateStopInMs;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }
}