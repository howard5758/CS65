package cs65.edu.dartmouth.cs.gifto;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Oliver on 2/25/2018.
 *
 */

public class Gift {
    private String giftName;
    private boolean sent;
    private int friend_id;
    private long time;
    private LatLng location;

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

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
