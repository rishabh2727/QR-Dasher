package com.example.qr_dasher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.util.Collections;
import java.util.Date;


import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;


public class BrowseEvents extends AppCompatActivity {

    private ListView browseEventList;
    private FirebaseFirestore db;
    private List<String> eventNames;
    private List<String> eventIds;
    private List<String> eventDetails;
    private List<Timestamp> eventTimestamps;

    private SharedPreferences app_cache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_events);

        browseEventList = findViewById(R.id.browseEventList);

        db = FirebaseFirestore.getInstance();
        // getting the UserID from the cache
        app_cache = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        int userId = app_cache.getInt("UserID", -1);
        retrieveEventsFromFirestore(userId);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when the activity is resumed
        int userId = app_cache.getInt("UserID", -1);  // Retrieve UserID again
        retrieveEventsFromFirestore(userId);
    }

    private void retrieveEventsFromFirestore(int userId) {
        // We only want to retrieve the events (1) which will take place in future
        // and (2) the attendee has not signed up/checked in yet

        eventNames = new ArrayList<>();
        eventIds = new ArrayList<>();
        eventDetails = new ArrayList<>();
        eventTimestamps = new ArrayList<>();


        // Getting the present date
        Date currentTime = new Date();
        Timestamp currentTimestamp = new Timestamp(currentTime);
        String userid_str = String.valueOf(userId);

        db.collection("eventsCollection")
                //.whereEqualTo("attendee_qr.userID",userId)
                .whereGreaterThan("timestamp", currentTimestamp)
                //.whereNotIn("signup_list", Collections.singletonList(userId))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    List<DocumentSnapshot> filteredEvents = new ArrayList<>();

                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle errors
                            browseEventList.setVisibility(View.GONE);
                            e.printStackTrace();
                            return;
                        }

                        // Update the list with new data
                        eventNames.clear();
                        eventIds.clear();
                        eventDetails.clear();
                        eventTimestamps.clear();

                        // Iterate through the query results
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            String eventName = documentSnapshot.getString("name");

                            if (documentSnapshot.contains("event_id")) {
                                Long eventIdLong = documentSnapshot.getLong("event_id");
                                if (eventIdLong != null) {
                                    // removing events where the user has signed up/ checked in
                                    String eventId = String.valueOf(eventIdLong);
                                    String eventDetail = documentSnapshot.getString("details");
                                    Timestamp eventTime = documentSnapshot.getTimestamp("timestamp");
                                    List<String> attendeeList = (List<String>) documentSnapshot.get("attendee_list");
                                    List<String> signupList = (List<String>) documentSnapshot.get("signup_list");
                                    if ((attendeeList == null  || !attendeeList.contains(userid_str)) && (signupList == null|| !signupList.contains(userid_str))){
                                        eventNames.add(eventName);
                                        eventIds.add(eventId);
                                        eventDetails.add(eventDetail);
                                        eventTimestamps.add(eventTime);
                                        // TO-DO: for poster get the poster in an array then put it in the bundle
                                    }
                                }
                            }
                        }
                        // Display the list of event names in the ListView
                        displayEventList(eventNames,eventIds);
                    }
                });

    }
    private void displayEventList(List<String> eventNames, List<String> eventIds) {
        // Create an ArrayAdapter to display the event names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.mytextview, eventNames);
        adapter.notifyDataSetChanged();
        // Set the adapter to the ListView
        browseEventList.setAdapter(adapter);

        // Set item click listener
        browseEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked event name
                // Create a bundle with all the required data needed for the sign up page
                Bundle bundle = new Bundle();

                String eventName = eventNames.get(position);
                bundle.putString("eventName", eventName);

                String detail = eventDetails.get(position);
                bundle.putString("eventDetail",detail);

                String eventId = eventIds.get(position);
                bundle.putString("eventId", eventId);

                // Converting timeStamp to date to put in bundle
                Timestamp eventTimestamp = eventTimestamps.get(position);
                Date date = eventTimestamp.toDate();
                bundle.putSerializable("timestamp",date);



                //Integer eventId = Integer.parseInt(eventIdStr);
                // Start new activity with the event name
                Intent intent = new Intent(BrowseEvents.this, EventSignUpPage.class);
                //intent.putExtra("eventName", eventName);
                //intent.putExtra("event_id", eventId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }
}