package cs65.edu.dartmouth.cs.gifto;

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

public class MapGift {
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

    MapGift(){
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

    MapGift(String giftName, String userName, String userNickname, String message,
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

    String getGiftName() {
        return giftName;
    }

    void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    String getUserName() {
        return userName;
    }

    void setUserName(String userName) {
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

    void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    cs65.edu.dartmouth.cs.gifto.LatLng getLocation() {
        return location;
    }

    void setLocation(cs65.edu.dartmouth.cs.gifto.LatLng location) {
        this.location = location;
    }

    long getTimePlaced() {
        return timePlaced;
    }

    void setTimePlaced(long timePlaced) {
        this.timePlaced = timePlaced;
    }

    String getUserNickname() {
        return userNickname;
    }

    void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    int getGiftBox() {
        return giftBox;
    }

    void setGiftBox(int giftBox) {
        this.giftBox = giftBox;
    }
}
