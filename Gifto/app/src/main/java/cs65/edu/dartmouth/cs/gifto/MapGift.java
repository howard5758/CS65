package cs65.edu.dartmouth.cs.gifto;

import java.io.Serializable;

/**
 * Created by Oliver on 2/26/2018.
 *
 * Special type of gift specifically for display on the map, includes some user data
 * This has to store a lot of information because it is how we communicate between users
 *
 *      id: The Firebase ID of the gift. This is the same across all devices
 *      giftName: the name of the gift in the box
 *      userName: the name of the person sending the gift
 *      userNickname: the nickname of the person sending the gift
 *      Message: the user's message inside the gift box
 *      animalName: the name of the animal delivering the gift
 *      location: the latitude and longitude of where the gift was placed
 *      timePlaced: the time the gift was placed
 *      sendTo: the email of the specific user to send the gift to, if any
 *      giftBox: the type of gift box to display on the map
 */

public class MapGift implements Serializable{
    private String id;
    private String giftName;
    private String userName;
    private String userNickname;
    private String Message;
    private String animalName;
    private cs65.edu.dartmouth.cs.gifto.LatLng location;
    private long timePlaced;
    private String sendTo;          // email address!!
    private int giftBox;

    public MapGift(){
        id = "";
        giftName = "";
        userName = "";
        userNickname = "";
        Message = "";
        animalName = "";
        location = new LatLng(-1, -1);
        timePlaced = -1;
        sendTo = "";
        giftBox = -1;
    }

    public MapGift(String giftName, String userName, String userNickname, String message,
                   String animalName, cs65.edu.dartmouth.cs.gifto.LatLng location,
                   long timePlaced, String sendTo, int giftBox) {
        this.giftName = giftName;
        this.userName = userName;
        this.userNickname = userNickname;
        this.Message = message;
        this.animalName = animalName;
        this.location = location;
        this.timePlaced = timePlaced;
        this.sendTo = sendTo;
        this.giftBox = giftBox;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public cs65.edu.dartmouth.cs.gifto.LatLng getLocation() {
        return location;
    }

    public void setLocation(cs65.edu.dartmouth.cs.gifto.LatLng location) {
        this.location = location;
    }

    public long getTimePlaced() {
        return timePlaced;
    }

    public void setTimePlaced(long timePlaced) {
        this.timePlaced = timePlaced;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public int getGiftBox() {
        return giftBox;
    }

    public void setGiftBox(int giftBox) {
        this.giftBox = giftBox;
    }
}
