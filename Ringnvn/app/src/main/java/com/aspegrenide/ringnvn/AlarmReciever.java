package com.aspegrenide.ringnvn;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlarmReciever extends BroadcastReceiver {

    private static final String CHANNEL_ID = "ringnvn";
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    private final static String default_notification_channel_id = "default";

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        this.context = context;
        lookupContacts();
    }

    private void lookupContacts() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        Log.d("lookupContacts", "init.. ");

        ValueEventListener contactsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Contact contact = child.getValue(Contact.class);
                    contactList.add(contact);
                }
                lookupCalls(new GetCallsCallback() {
                    @Override
                    public void onCallback(ArrayList<CallDetails> callList) {
                        Contact oldestCallContact = getOldestCallContact(contactList, callList);
                        if (oldestCallContact == null) {
                            Log.d("ALARM", "No contact to notify about");
                            return; // nothing to do no notificaiton ot send
                        }
                        String notifcationTxt = generateNotificationText(oldestCallContact);
                        Intent startApp = new Intent(context, MainActivity.class);
                        startApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        showNotification(context, "Ring en vän!", notifcationTxt, startApp);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(context.getResources().getString(R.string.contacs_ii)).addValueEventListener(contactsListener );
    }

    private void lookupCalls(final GetCallsCallback callback) {
        ArrayList<CallDetails> callList = new ArrayList<CallDetails>();
        Log.d("lookupCalls", "init.. ");

        ValueEventListener callsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    CallDetails callDetail = child.getValue(CallDetails.class);
                    callList.add(callDetail);
                    Log.d("lookupCalls", "callDetail.. " + callDetail.getPhoneNr());
                }
                callback.onCallback(callList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(context.getResources().getString(R.string.calls_ii)).addValueEventListener(callsListener );
    }

    private Contact getOldestCallContact(ArrayList<Contact> contactList, ArrayList<CallDetails> callList) {
        Log.d("GEN", "init.. ");
        String ret = "No candicate found";
        // who have waited the longest and is in callable hours?

        // first find the latest call per person
        for(Contact c : contactList) {
            for(CallDetails cd : callList) {
                if(cd.getPhoneNr().equalsIgnoreCase(c.getPhoneNr())) {
                    if (c.getLastCallDateMs() == null) {
                        c.setLastCallDateMs(cd.getDateStartInMs());
                    }
                    if (c.getLastCallDateMs() < cd.getDateStartInMs()) {
                        c.setLastCallDateMs(cd.getDateStartInMs());
                    }
                }
            }
        }
        // then check the oldest of the newest

        Long oldestCall = java.util.Calendar.getInstance().getTime().getTime();
        Long now = java.util.Calendar.getInstance().getTime().getTime();
        CallDetails oldestCallDetails = null;
        Contact oldestContact = null;

        for(Contact c : contactList) {
            if (c.isAllowedTime()) {
                if (c.getLastCallDateMs() < oldestCall) {
                    oldestCall = c.getLastCallDateMs();
                    oldestContact = c;
                }
            }
        }

        return oldestContact;

    }


    private String generateNotificationText(Contact oldestContact) {
        Log.d("GEN", "init.. ");
        String ret = "unexpected error";


        String name = oldestContact.getName();
        Long oldestCall = oldestContact.getLastCallDateMs();
        DataManager dm = new DataManager();
        String timeSince = dm.generateSinceString(oldestCall);
        ret =  name + " sitter ensam, telefonen ringde senast för ";
        ret += timeSince + " sedan.";
        return ret;
    }

    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ringenvnii)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}
