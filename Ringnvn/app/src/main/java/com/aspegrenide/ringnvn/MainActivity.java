package com.aspegrenide.ringnvn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecAdapter adapter;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    SharedPreferences prefs;
    public static final String PREFS = "mypref";
    public static final String USER = "USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check and set username
        setUserName("User Johan");
        prefs = getPreferences(Context.MODE_PRIVATE);
        String user_name = prefs.getString(USER, null);

        // load data from database, to creata testdata
        // DataManager dm = new DataManager();
        //dm.writeContactsToFirebase(dm.generateContactList(), getString(R.string.contacs_ii));

        // load data from database
        final List<Contact> contactList = new ArrayList<Contact>();
        ArrayList<CallDetails> callList = new ArrayList<CallDetails>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        databaseReference.child(getResources().
                getString(R.string.contacs_ii)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("GNR", "Contact changed and kicked back, init call?");
                // get all children at this level
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                //shake hands with all items
                for (DataSnapshot child : children) {
                    Contact contact = child.getValue(Contact.class);
                    contactList.add(contact);
                    Log.d("GNR", "loaded " + contact.toString());
                }
                adapter.notifyDataSetChanged(); //update the screen
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child(getResources().getString(R.string.calls_ii)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("GNR", "Call placed and kicked back ");
                // get all children at this level
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                //shake hands with all items
                for (DataSnapshot child : children) {
                    CallDetails callDetail = child.getValue(CallDetails.class);
                    callList.add(callDetail);
                    Log.d("GNR", "loaded " + callDetail.toString());
                }
                adapter.notifyDataSetChanged(); //update the screen
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new RecAdapter(MainActivity.this, contactList, callList);
        RecyclerView recyclerView = findViewById(R.id.recview);
        ImageView happyLadyIcon = (ImageView) findViewById(R.id.hbg_staende);
        happyLadyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the log activity
                startCallLog();
            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).
                setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);


        // set the alarm to make sure we get back
        // use a dummy intent as a trigger to restart the app
        Intent dummyIntent = new Intent(this, AlarmReciever.class);
        dummyIntent.putExtra(AlarmReciever.NOTIFICATION_ID, 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                dummyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int minutes = 45;
        int delay = minutes * 60000;
        // delay = 5000; // for testing purpose 5 seconds to notificaiton
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private void startCallLog() {
        Intent intent = new Intent(this, CallLogActivity.class);
        startActivity(intent);
    }

    private void setUserName(String user_name) {

        prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER, user_name);
        editor.commit();
    }
}