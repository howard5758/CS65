package cs65.edu.dartmouth.cs.gifto;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Oliver on 2/26/2018.
 *
 * Special type of gift specifically for display on the map, includes some user data
 */

public class MapGift {
    private String giftName;
    private String userName;
    private String userNickname;
    private String Message;
    private String animalName;
    private LatLng location;
    private long timePlaced;

    public MapGift(){}

    public MapGift(String giftName, String userName, String userNickname, String message,
                   String animalName, LatLng location, long timePlaced) {
        this.giftName = giftName;
        this.userName = userName;
        this.userNickname = userNickname;
        this.Message = message;
        this.animalName = animalName;
        this.location = location;
        this.timePlaced = timePlaced;
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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
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
}
