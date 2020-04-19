package com.aspegrenide.ringnvn;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class DataManager {

    DatabaseReference mDatabase;
    ArrayList<Contact> contacts = new ArrayList<Contact>();

    public void writeContactsToFirebase(ArrayList<Contact> mContacts, String dataBaseSet) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        for (Contact c : mContacts) {
            mDatabase.child(dataBaseSet).child(c.getPhoneNr()).setValue(c);
        }
    }

    public void writeCallListToFirebase(ArrayList<CallDetails> mCalls, String dataBaseSet) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        for (CallDetails cd : mCalls) {
            mDatabase.child(dataBaseSet).child(cd.getDateStartInMs().toString()).setValue(cd);
            Log.d("DATAMGR", "Wrote call to firebase" + cd.getPhoneNr());
        }
    }

    public ArrayList<Contact> generateContactList() {
        ArrayList<Contact> contactList = new ArrayList<>();

        String isb = "Är mycket intresserad av golfhistoria och har en samling av antika träklubbor";
        Long dummyDat = null;
        Contact contact =
                new Contact("John", "0736509716", "Golf", null,
                         R.drawable.golf, "Stattena", isb);
        contactList.add(contact);

        isb = "Var nära att sänka segelbåten under en storm på Vättern";
        contact = new Contact("Ulf", "0702945759",
                "segling", dummyDat, R.drawable.yacht,
                "Hässleholm", isb);
        contactList.add(contact);

/*
        isb = "Har skakat hand med Barack Obama när hon jobbade på Nasdaq en sommar";
        contact = new Contact("Maria", "0730785267",
                "Blommor", null,R.drawable.flower, 1931, "Bredgatan", isb);
        contactList.add(contact);

        isb = "Har två barn, flickor båda två. Mycket magi, enhörningar och prinsessor.";
        contact = new Contact("Fabian", "0730736449","Naturen", null,R.drawable.forest, 1931, "LSS", isb);
        contactList.add(contact);

        isb = "Är släkt med Sveriges mest berömda konstnär.... Carl Larsson";
        contact = new Contact("Robert", "0733668185","Mat och Dryck", null,R.drawable.dining, 1931, "NA", isb);
        contactList.add(contact);

        isb = "ipsum lorem ...";
        contact = new Contact("Lars-Inge", "07365097165","Fågelskådning",null, R.drawable.bird, 1931, "Dyrebäck", isb);
        contactList.add(contact);

        isb = "ipsum lorem ...";
        contact = new Contact("Stina", "07365097166","Odla",null, R.drawable.farm, 1931, "Kometen", isb);
        contactList.add(contact);

        isb = "ipsum lorem ...";
        contact = new Contact("Louise", "07365097167","Naturen", null,R.drawable.forest, 1931, "NA", isb);
        contactList.add(contact);
*/
        return contactList;
    }

    public String generateSinceString(Long lastCallDateInMs) {
        Long now = java.util.Calendar.getInstance().getTime().getTime();
        Long then = lastCallDateInMs;
        Log.d("GNR", "now " + now.toString());

        if (then == null) {
            Log.d("GNR", "then is null");
            return "na";
        }
        Log.d("GNR", "then " + then.toString());
        long duration  = now - then;
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

}