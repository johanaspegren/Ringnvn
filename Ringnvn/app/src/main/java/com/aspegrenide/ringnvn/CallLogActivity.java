package com.aspegrenide.ringnvn;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

public class CallLogActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);

        TextView tvWinner = (TextView) findViewById(R.id.tv_number_one);
        TextView tvSecond = (TextView) findViewById(R.id.tv_number_two);
        TextView tvThird = (TextView) findViewById(R.id.tv_number_three);

        TextView tvWinnerOcc = (TextView) findViewById(R.id.tv_winner_occ);
        TextView tvSecondOcc = (TextView) findViewById(R.id.tv_second_occ);
        TextView tvThirdOcc = (TextView) findViewById(R.id.tv_third_occ);

        final ArrayList<CallDetails> callList = new ArrayList<CallDetails>();
        final ArrayList<String> callListString = new ArrayList<String>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        databaseReference.child(getResources().getString(R.string.calls_ii)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get all children at this level
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                ArrayList<String> names = new ArrayList<String>();

                //shake hands with all items
                for (DataSnapshot child : children) {
                    CallDetails callDetail = child.getValue(CallDetails.class);
                    callList.add(callDetail);

                    Locale currentLocale = Locale.getDefault();
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE HH:mm", currentLocale);
                    String ts = formatter.format(new Date(callDetail.getDateStartInMs()));

                    String details = callDetail.getCaller();
                    details += ": " + ts;
                    details += " (" + callDetail.getPhoneNr() + ")";

                    callListString.add(details);
                    names.add(callDetail.getCaller());
                }

                // try to find the top three
                HashMap<String, Integer> map = new HashMap<String, Integer>();
                ValueComparator bvc = new ValueComparator(map);
                TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

                for (CallDetails cd : callList) {
                    String name = cd.getCaller();
                    map.put(name, Collections.frequency(names, name));
                }
                // sort in Treemap
                //Log.d("unsorted map: ", map.toString());
                sorted_map.putAll(map);
                //Log.d("SORTED map: ", sorted_map.toString());

                Set<String> setKeys = sorted_map.keySet();
                List<String> listKeys = new ArrayList<String>( setKeys );

////                Log.d("SORTED map: ", listKeys.toString());
                //            Log.d("SORTED list length: ", listKeys.size() + "");
                for (String s : listKeys) {
                    //  Log.d("SORTED list length: ", "S = " + s);
                    //Log.d("SORTED occ: ", map.get(s) + "");
                }


                if (listKeys.size() >= 1) {
                    tvWinner.setText(listKeys.get(0));
                    tvWinnerOcc.setText(map.get(listKeys.get(0)).toString());
                }
                if (listKeys.size() >= 2) {
                    tvSecond.setText(listKeys.get(1));
                    tvSecondOcc.setText(map.get(listKeys.get(1)).toString());
                }
                if (listKeys.size() >= 3) {
                    tvThird.setText(listKeys.get(2));
                    tvThirdOcc.setText(map.get(listKeys.get(2)).toString());
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        ListView lv = (ListView) findViewById(R.id.lv);

        ArrayAdapter<String> callListAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, callListString);

        lv.setAdapter(callListAdapter);

    }
}

