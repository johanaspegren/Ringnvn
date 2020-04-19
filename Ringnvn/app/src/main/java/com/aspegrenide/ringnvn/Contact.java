package com.aspegrenide.ringnvn;

import android.telecom.Call;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Contact {

    private String name;
    private String interest;
    private String boende;

    private String phoneNr;
    private int imgId;
    private String isb;

    private String lastCaller;
    private Long lastCallDateMs;

    private ArrayList<Integer> allowedHours;

    private boolean expanded;

    public Contact() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Contact(String name, String phoneNr,
                   String interest, Long lastCallDateMs,
                   int imgId, String boende, String isb) {

        this.name = name;
        this.phoneNr = phoneNr;
        this.interest = interest;
        this.lastCallDateMs = lastCallDateMs;
        this.imgId = imgId;
        this.boende = boende;
        this.isb = isb;

    }

    public String getBoende() {
        return boende;
    }

    public String getName() {
        return name;
    }

    public String getInterest() {
        return interest;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }


    public Long getLastCallDateMs() {
        return lastCallDateMs;
    }

    public void setLastCallDateMs(Long lastCallDateMs) {
        this.lastCallDateMs = lastCallDateMs;
    }


    public String generateLastCallString() {
        Date now = java.util.Calendar.getInstance().getTime();
        Date then = new Date(getLastCallDateMs());

        Log.d("GNR", "now " + now.toString());

        if (then == null) {
            Log.d("GNR", "then is null");
            return "na";
        }
        Log.d("GNR", "then " + then.toString());
        long duration  = now.getTime() - then.getTime();
        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long totHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        //Log.d("CONTACT", "now = " + now.toString());
        //Log.d("CONTACT", "then = " + then.toString());

        String ret = "";
        if (diffInDays > 0) {
            ret += diffInDays + " dgr ";
            diffInHours = diffInHours - (diffInDays * 24);
        }
        if (diffInHours > 0) {
            ret += diffInHours + " tim ";
            diffInMinutes = diffInMinutes - (totHours * 60);
        }
        if (diffInMinutes >= 0) {
            ret += diffInMinutes + " min";
        }
        ret += " sedan";
        return ret;
    }

    public String getPhoneNr() {
        return phoneNr;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public String getIsb() {
        return isb;
    }

    public void setIsb(String isb) {
        this.isb = isb;
    }

    public ArrayList<Integer> getAllowedHours() {
        ArrayList<Integer> allowedHours = new ArrayList<Integer>();
        allowedHours.clear();
        for (int i = 9; i <= 20; i++) {
            allowedHours.add((Integer)i);
        }
        return allowedHours;
    }

    public void setAllowedHours(ArrayList<Integer> allowedHours) {
        this.allowedHours = allowedHours;
    }

    public boolean isAllowedTime() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        ArrayList<Integer> ah = getAllowedHours();
        if (ah.contains(hour)) {
            return true;
        }
        return false;
    }
}