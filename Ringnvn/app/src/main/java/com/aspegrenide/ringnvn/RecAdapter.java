package com.aspegrenide.ringnvn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.RecViewHolder> {
    private List<Contact> contactList;
    private ArrayList<CallDetails> callList;
    private Activity mainActivity; //needed to fire off the callintent

    // save username in prefs
    SharedPreferences sharedPref;
    public static final String PREFS = "mypref";
    public static final String USER = "USER";

    public RecAdapter(MainActivity mainActivity, List<Contact> contactList, ArrayList<CallDetails> callList) {
        this.mainActivity = mainActivity;
        this.contactList = contactList;
        this.callList = callList;

        sharedPref = mainActivity.getPreferences(Context.MODE_PRIVATE);
        String user_name = sharedPref.getString(USER, "Default");
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.bind(contact);
        holder.itemView.setOnClickListener(v -> {

            boolean expanded = contact.isExpanded();
            contact.setExpanded(!expanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return contactList == null ? 0 : contactList.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvInterest;
        private TextView tvCaller;
        private TextView tvLastCall;
        private TextView genre;
        private View subItem;
        private ImageView badge;
        private ImageView makeCall;

        public RecViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.item_title);
            tvInterest = itemView.findViewById(R.id.interest);
            tvCaller = itemView.findViewById(R.id.caller);
            tvLastCall= itemView.findViewById(R.id.last_call);
            genre = itemView.findViewById(R.id.sub_item_genre);
            subItem = itemView.findViewById(R.id.sub_item);
            badge = itemView.findViewById((R.id.imageView));
            makeCall = itemView.findViewById((R.id.imageViewMakeCall));
        }

        private void bind(Contact contact) {
            boolean expanded = contact.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);
            badge.setImageResource(contact.getImgId());
            tvName.setText(contact.getName());
            tvName.setTypeface(null, Typeface.BOLD);
            tvInterest.setText(contact.getInterest());

            // need to do some crosschecking here
            CallDetails lastCall = getLastCall(contact, callList);
            String lastCaller = "unknown";
            String timeSince = "unknown";
            if(lastCall != null) {
                lastCaller = lastCall.getCaller();
                Log.d("REC", lastCall.getCaller());
                Long lastCallDateInMs = lastCall.getDateStartInMs();
                DataManager dm = new DataManager();
                timeSince = dm.generateSinceString(lastCallDateInMs);
            }
            tvCaller.setText(lastCaller);
            tvLastCall.setText(timeSince);

            makeCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {

                        // get user
                        String user_name = sharedPref.getString(USER, "Default");

                        // log that a call was initiated
                        Date now = java.util.Calendar.getInstance().getTime();
                        Long curMillis = now.getTime();
                        String caller = user_name;
                        callList.add(new CallDetails(caller, contact.getPhoneNr(), curMillis));

                        // write to database
                        DataManager dm = new DataManager();
                        dm.writeCallListToFirebase(callList, mainActivity.getResources().getString(R.string.calls_ii));
                        Log.d("GNR", "Start the call");

                        // start the call
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + contact.getPhoneNr()));
                        mainActivity.startActivity(callIntent);
                    }
                }
            });

            String genericDescription = contact.getIsb();
            genre.setText(genericDescription);
        }


        private CallDetails getLastCall(Contact contact, ArrayList<CallDetails> callList) {
            CallDetails ret = null;
            for(CallDetails ct : callList) {
                if (ct.getPhoneNr().equalsIgnoreCase(contact.getPhoneNr())) {
                    // match
                    ret = ct;
                }
            }
            return ret;
        }
    }
}