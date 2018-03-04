package cs65.edu.dartmouth.cs.gifto;

/**
 * Created by Oliver on 2/25/2018.
 *
 * Even though it's a very simple class, I think it will make adding to databases easier
 */

public class Friend {
    private String email;
    private String nickname;
    private String firebaseId;

    // if you want to just make an empty friend
    Friend(){
        email = "";
        nickname = "";
        firebaseId = "";
    }

    // to initialize with name and nickname, if known
    Friend(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String name) {
        this.email = name;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }
}
