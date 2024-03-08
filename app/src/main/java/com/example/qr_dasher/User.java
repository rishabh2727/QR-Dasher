package com.example.qr_dasher;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * User class represents a user in the system.
 */
public class User {
    private String name;
    private int UserId;
    private String email;
    private String details;
    private boolean location;

    private String profile_image;
    private List<String> eventsCreated;
    private List<String> eventsJoined;
    public User(){

    }
    /**
     * Constructor for the User class.
     * @param name The name of the user.
     * @param email The email address of the user.
     * @param location The location status of the user.
     */
    public User(String name, String email, boolean location){
        Random random = new Random();
        this.name = name;
        if (email.contains("@") && email.contains(".com")){
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email");
        }
        this.location = location;
        this.UserId = random.nextInt();
        this.eventsCreated = new ArrayList<>();
        this.eventsJoined = new ArrayList<>();
    }
    /**
     * Get the name of the user.
     *
     * @return The name of the user.
     */
    public String getName(){
        return this.name;
    }
    /**
     * Get the email address of the user.
     *
     * @return The email address of the user.
     */
    public String getEmail(){
        return this.email;
    }
    /**
     * Get the details of the user.
     *
     * @return The details of the user.
     */
    public String getDetails(){
        return this.details;
    }
    /**
     * Get the location status of the user.
     *
     * @return The location status of the user.
     */
    public boolean getLocation(){
        return this.location;
    }
    /**
     * Get the unique ID of the user.
     *
     * @return The unique ID of the user.
     */
    public int getUserId() { return UserId; }
     /**
     * Get the profile image of the user.
     *
     * @return The profile image of the user.
     */
    public String getProfile_image() { return profile_image; }
     /**
     * Set the name of the user.
     *
     * @param Name The name of the user.
     */
    public void setName(String Name){
        this.name = Name;
    }
    /**
     * Set the email address of the user.
     *
     * @param email The email address of the user.
     */
    public void setEmail(String email){
        this.email = email;
    }
     /**
     * Set the details of the user.
     *
     * @param details The details of the user.
     */
    public void setDetails(String details){
        this.details = details;
    }
    /**
     * Set the location status of the user.
     *
     * @param location The location status of the user.
     */
    public void setLocation(boolean location){
        this.location = location;
    }
    /**
     * Set the profile image of the user.
     *
     * @param profile_image The profile image of the user.
     */
    public void setProfile_image(String profile_image) { this.profile_image = profile_image; }
    /**
     * Set the unique ID of the user.
     *
     * @param userId The unique ID of the user.
     */
    public void setUserId(int userId) { UserId = userId; }
    /**
     * Add an event joined by the user.
     *
     * @param qrCode The QR code of the event.
     */

    public void addEventsJoined(String qrCode){
        this.eventsJoined.add(qrCode);
    }
    /**
     * Add an event created by the user.
     *
     * @param qrCode The QR code of the event.
     */
    public void addEventsCreated(String qrCode){
        this.eventsCreated.add(qrCode);
    }
    /**
     * Get the list of events joined by the user.
     *
     * @return The list of events joined by the user.
     */
    public List<String> getEventsJoined(){
        return this.eventsJoined;
    }
     /**
     * Get the list of events created by the user.
     *
     * @return The list of events created by the user.
     */
    public List<String> getEventsCreated(){
        return this.eventsCreated;
    }
}
