package cs65.edu.dartmouth.cs.gifto;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Oliver on 2/25/2018.
 *
 * Class to store gift information
 * Preferably would be passed into MySQLiteHelper
 *
 * Could potentially assign another variable storing drawing information? Same with other classes
 */

public class Gift {
    private String id;
    private String giftName;
    private boolean sent;
    private String friendName;
    private long time;
    private cs65.edu.dartmouth.cs.gifto.LatLng location;

    Gift(){}

    Gift(String giftName, boolean sent, String friendName, long time, cs65.edu.dartmouth.cs.gifto.LatLng location) {
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
}
