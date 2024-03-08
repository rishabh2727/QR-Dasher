
package com.example.qr_dasher;


import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

// zxing lib
import com.google.firebase.FirebaseApp;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

// adding firebase references
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

public class CreateEventOrganizer extends AppCompatActivity {
    public static final String EXTRA_QR_CODES = "extra_qr_codes";
    private ImageView qrImage, promotionalImage;
    private Button generateQRandCreateEvent, generatePromotionalQR, displayQRcodes, downloadButton;
    private EditText eventName, eventDetails;

    private Bitmap generatedQRCode;

    private FirebaseFirestore db;
    private CollectionReference eventsCollection;
    private SharedPreferences app_cache; // To get the userID
    private static final int REQUEST_CODE_SAVE_IMAGE = 1001;


    private Event event;
    private List<String> reuseQRCodes;

    //private CollectionReference generatedQRCodes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_organizer);
        reuseQRCodes = getIntent().getStringArrayListExtra("reuseQRCodes");


        qrImage = findViewById(R.id.qrCode); // image
        promotionalImage = findViewById(R.id.promotionalQR);
        generateQRandCreateEvent = findViewById(R.id.generateQRandCreateEvent); // button
        generatePromotionalQR = findViewById(R.id.generatePromotionalQR);
        eventName = findViewById(R.id.eventName); // event Name
        eventDetails = findViewById(R.id.details); // event Name
        downloadButton = findViewById(R.id.downloadbutton);


        displayQRcodes = findViewById(R.id.displayQRcodes);

        // Create Collection in Firebase
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        app_cache = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        int userId = app_cache.getInt("UserID", -1);



        eventsCollection = db.collection("events");

        generateQRandCreateEvent.setOnClickListener(new View.OnClickListener() { // generate QR based on the event id
            // we will generate the event ID using the event.java

            @Override
            public void onClick(View v) {
                Log.d("generateQRandCreateEvent","pressed button");
                //if (!TextUtils.isEmpty(eventName.getText()) && !TextUtils.isEmpty(eventDetails.getText())){
                    // to check if there is some text or not
                    String event_name = eventName.getText().toString();
                    String event_details = eventDetails.getText().toString();


                    // Create a new event
                    event = new Event(event_name, event_details,  userId);
                    // Generated event_id
                    Log.d("Eventid", ""+event.getEvent_id());
                    // Generating QR codes
                    event.generateQR("" + event.getEvent_id(), false);
                    //event.generateQR("" + event.getEvent_id(), true);
                    generatePromotionalQR.setVisibility(View.VISIBLE);
                    downloadButton.setVisibility(View.VISIBLE);

                // we need to get the QR code in bitmap to be able to display it
                    String qrCodeString = event.getAttendee_qr().getQrImage();
                    byte[] imageBytes = Base64.decode(qrCodeString, Base64.DEFAULT);
                    generatedQRCode = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    qrImage.setImageBitmap(generatedQRCode);
                    // Adding the event to the user's class
                    AtomicReference<User> user = new AtomicReference<>();

                    db.collection("users")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                                    user.set(documentSnapshot.toObject(User.class));
                                    user.get().addEventsCreated(""+event.getEvent_id()); // Add the event ID to the user's eventsJoined list
                                    updateFirebaseUser(documentSnapshot.getId(), user.get()); // Update the user in Firestore
                                } else {
                                    Log.d("Organizer", "No user found with UserId: " + userId);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.d("Organizer", "Failed to retrieve User from Firestore");
                                e.printStackTrace();
                            });
                    addEventToFirebase(event);

            }
        });

        generatePromotionalQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.generateQR("" + event.getEvent_id(), true);
                String qrCodeString = event.getPromotional_qr().getQrImage();
                byte[] imageBytes = Base64.decode(qrCodeString, Base64.DEFAULT);
                Bitmap qrCodeBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                promotionalImage.setImageBitmap(qrCodeBitmap);
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "qrcode.png";
                MediaStore.Images.Media.insertImage(getContentResolver(), generatedQRCode, fileName, "Image saved from your app");
                Toast.makeText(getApplicationContext(), "Image saved to Gallery", Toast.LENGTH_SHORT).show();
            }
        });
        displayQRcodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQRFromFirebase();
            }
        });


    }

    private void updateFirebaseUser(String userId, User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .update("eventsCreated", user.getEventsCreated())
                .addOnSuccessListener(aVoid -> Log.d("Organizer", "User updated successfully"))
                .addOnFailureListener(e -> {
                    Log.d("Organizer", "Failed to update user in Firestore");
                    e.printStackTrace();
                });
    }

    private void addEventToFirebase(Event event) {
        db.collection("eventsCollection")
                .document(""+event.getEvent_id())
                .set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("QR", "Successfully uploaded to firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("QR", "Couldn't be uploaded to firestore");
                        e.printStackTrace();
                    }
                });
    }

    private String convertQRtoString(Bitmap qrCode){
        // Create a new output Stream (base64 string)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // compress the qr code
        qrCode.compress(Bitmap.CompressFormat.PNG, 100,baos);
        byte[] imageBytes = baos.toByteArray();
        // return the String in base64 format
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void AddQRtoFirebase(String qrCodeString, String eventID){
        // eventID will be the ID linked with the QR code/ Content of the QR code
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Create a Hashmap to store the QR code string
        Map<String, Object> qrCodeData = new HashMap<>();
        qrCodeData.put("image", qrCodeString);

        db.collection("generatedQRCodes")
                .document(eventID)
                .set(qrCodeData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("QR", "Successfully uploaded to firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("QR", "Couldn't be uploaded to firestore");
                        e.printStackTrace();
                    }
                });
    }

    public void getQRFromFirebase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> qrCodes = new ArrayList<>(); // list of qrCodes
                    //Iterate through the list
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Map<String, Object> qrCodeData = documentSnapshot.getData();
                        String qrCodeString = (String) qrCodeData.get("image");
                        qrCodes.add(qrCodeString); // append to the list
                    }
                    // Process the retrieved QR codes, for example, display them or do further operations
                    Intent intent = new Intent(CreateEventOrganizer.this, reuseQRcodes.class);
                    intent.putStringArrayListExtra(EXTRA_QR_CODES, (ArrayList<String>) qrCodes);
                    startActivity(intent);
                })
                // Once we have all the qr codes, we can call in displayQRcodes activity
                //Intent intent = new Intent(MainActivity.this, displayQRcodes.class);
                //intent.putStringArrayListExtra(displayQRcodes.EXTRA_QR_CODES, (ArrayList<String>) qrCodes);
                // DisplayQRActivity.EXTRA_QR_CODES is the list of QRcodes we got from firebase
                //startActivity(intent);

                .addOnFailureListener(e -> {
                    Log.d("QR", "Failed to retrieve QR codes from Firestore");
                    e.printStackTrace();
                });

    }

//    private void displayQRcodes(List<String> qrCodes){
//        for (String qrCodeString: qrCodes){
//            // Converting it back to Bitmap from base 64
//            byte[] imageBytes = Base64.decode(qrCodeString, Base64.DEFAULT);
//            Bitmap qrCodeBitmap  = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//            ImageView imageView = new ImageView(this);
//            imageView.setImageBitmap(qrCodeBitmap);
//            setContentView(imageView);
//        }
//    }
//



}