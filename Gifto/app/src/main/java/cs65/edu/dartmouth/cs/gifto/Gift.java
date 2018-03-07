package cs65.edu.dartmouth.cs.gifto;

import java.io.Serializable;

/**
 * Created by Oliver on 2/25/2018.
 *
 * Class to store gift information
 * Preferably would be passed into MySQLiteHelper
 *
 * A Gift is anything that you pick up from the map or anything that an animal brings you. Usually,
 *   a Gift can be placed in the Garden to attract more animals
 *      id: the Firebase ID of a specific gift. Used to make finding the gift easier
 *      giftName: the unique name of the gift
 *      sent: whether the gift was sent to someone or received from someone, for display later. If
 *        it was from an animal, then sent is false as the gift was received from that animal
 *      friendName: the name of the friend (or animal) who gave you the gift, or your name if you
 *        sent the gift
 *      time: the time in milliseconds the gift was placed or picked up
 *      location: the Latitude and Longitude of where the gift was placed
 *      giftBox: the type of gift box that the particular gift should be wrapped in
 *
 */

public class Gift implements Serializable{
    private String id;
    private String giftName;
    private boolean sent;
    private String friendName;
    private long time;
    private cs65.edu.dartmouth.cs.gifto.LatLng location;
    private int giftBox;

    public Gift(){
        id = "";
        giftName = "";
        sent = false;
        friendName = "";
        time = -1;
        location = new cs65.edu.dartmouth.cs.gifto.LatLng(-1,-1);
        giftBox = -1;
    }

    public Gift(String giftName, boolean sent, String friendName, long time, cs65.edu.dartmouth.cs.gifto.LatLng location) {
        this.giftName = giftName;
        this.sent = sent;
        this.friendName = friendName;
        this.time = time;
        this.location = location;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public cs65.edu.dartmouth.cs.gifto.LatLng getLocation() {
        return location;
    }

    public void setLocation(cs65.edu.dartmouth.cs.gifto.LatLng location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGiftBox() {
        return giftBox;
    }

    public void setGiftBox(int giftBox) {
        this.giftBox = giftBox;
    }
}
