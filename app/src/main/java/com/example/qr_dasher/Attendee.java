package com.example.qr_dasher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
/**
 * Activity for attendees of an event. Allows attendees to view notifications, edit their profile,
 * and scan a QR code to join an event.
 */
public class Attendee extends AppCompatActivity {
    private Button notificationButton, editProfileButton, qrCodeButton, browseEvents;
    private SharedPreferences app_cache;
    /**
     * Initializes the activity and sets up UI components and listeners.
     *
     * @param savedInstanceState Saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee);

        app_cache = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        notificationButton = findViewById(R.id.notification_button);
        editProfileButton = findViewById(R.id.edit_profile_button);
        qrCodeButton = findViewById(R.id.qr_button);
        browseEvents = findViewById(R.id.browseEvents);
        browseEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Attendee.this, BrowseEvents.class);
                startActivity(intent);
            }
        });
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle attendee role selection
                // For example, start a new activity for attendee tasks
                Intent intent = new Intent(Attendee.this, Notifications.class);
                startActivity(intent);
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Attendee.this, EditProfile.class);
                startActivity(intent);
            }
        });

        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle attendee role selection
                // For example, start a new activity for attendee tasks
                Intent intent = new Intent(Attendee.this, ScanQR.class);
                startActivityForResult(intent, 1);
            }
        });


    }
    /**
     * Handles the result of the QR code scanning activity.
     *
     * @param requestCode The request code passed to startActivityForResult()
     * @param resultCode  The result code returned by the child activity
     * @param data        The Intent containing the result data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Check if the result is from ScanQR activity
            if (resultCode == RESULT_OK) {
                String scannedText = data.getStringExtra("scannedText");
                Toast.makeText(this, "Scanning successful " + scannedText, Toast.LENGTH_SHORT).show();

                // Determine if it is promotional or checkin QR
                if (scannedText!=null) {
                    if (scannedText.charAt(0) == 'p') {
                        // Promotional QR
                        displayEventSignUpPage(scannedText);
                    } else {
                        // Checkin QR
                        updateFirebase(scannedText);
                    }
                }

                // Implement firestore check
            } else {
                // Handle case where scanning was canceled or failed
                Toast.makeText(this, "Scanning failed or canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void displayEventSignUpPage(String pQRcontent){
        // Remove "p" and get the event info from firebase
        // Start EventSignUpPage Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String eventIdPromo = pQRcontent.substring(1);
        db.collection("eventsCollection")
                .document(eventIdPromo)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        // Create bundle and start activity
                        Bundle bundle = new Bundle();
                        String eventName = event.getName();
                        bundle.putString("eventName", eventName);

                        String detail = event.getDetails();
                        bundle.putString("eventDetail",detail);

                        String eventId = String.valueOf(event.getEvent_id());
                        bundle.putString("eventId", eventId);

                        // Converting timeStamp to date to put in bundle
                        Timestamp eventTimestamp = event.getTimestamp();
                        Date date = eventTimestamp.toDate();
                        bundle.putSerializable("timestamp",date);


                        //Integer eventId = Integer.parseInt(eventIdStr);
                        // Start new activity with the event name
                        Intent intent = new Intent(Attendee.this, EventSignUpPage.class);
                        //intent.putExtra("eventName", eventName);
                        //intent.putExtra("event_id", eventId);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Log.d("Attendee", "No event found with EventId: ");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Attendee", "Failed to retrieve Event from Firestore");
                    e.printStackTrace();
                });
    }


    private void updateFirebase(String event_id){
        int userId = app_cache.getInt("UserID", -1);
        String eventID = event_id; // Assuming event_id is already the document ID

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update user document
        db.collection("users")
                .document(String.valueOf(userId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        user.addEventsJoined(eventID); // Add the event ID to the user's eventsJoined list
                        updateFirebaseUser(String.valueOf(userId), user); // Update the user in Firestore
                    } else {
                        Log.d("Attendee", "No user found with UserId: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Attendee", "Failed to retrieve User from Firestore");
                    e.printStackTrace();
                });

        // Update event document
        db.collection("eventsCollection")
                .document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        Integer user_id = userId;
                        String userId_str = String.valueOf(user_id);
                        event.addAttendee(userId_str); // Add the user ID to the event's attendee list
                        updateFirebaseEvent(eventID, event); // Update the event in Firestore
                    } else {
                        Log.d("Attendee", "No event found with EventId: " + eventID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Attendee", "Failed to retrieve Event from Firestore");
                    e.printStackTrace();
                });
    }
    /**
     * Update user document in Firebase.
     *
     * @param userId The user ID
     * @param user   The User object to update
     */


    private void updateFirebaseUser(String userId, User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .update("eventsJoined", user.getEventsJoined())
                .addOnSuccessListener(aVoid -> Log.d("Attendee", "User updated successfully"))
                .addOnFailureListener(e -> {
                    Log.d("Attendee", "Failed to update user in Firestore");
                    e.printStackTrace();
                });
    }
    /**
     * Update event document in Firebase.
     *
     * @param eventId The event ID
     * @param event   The Event object to update
     */
    private void updateFirebaseEvent(String eventId, Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("eventsCollection")
                .document(eventId)
                .update("attendee_list", event.getAttendee_list())
                .addOnSuccessListener(aVoid -> Log.d("Attendee", "Event updated successfully"))
                .addOnFailureListener(e -> {
                    Log.d("Attendee", "Failed to update event in Firestore");
                    e.printStackTrace();
                });
    }

}
