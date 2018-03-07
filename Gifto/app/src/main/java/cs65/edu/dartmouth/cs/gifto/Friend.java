package cs65.edu.dartmouth.cs.gifto;

import java.io.Serializable;

/**
 * Created by Oliver on 2/25/2018.
 *
 * Class to store a single friend
 *
 * A friend is any other user. Users can add friends using the drop down menu in the garden
 *      email: the email account that the friend signed up with
 *      nickname: the nickname of the user, equal to the email if a nickname has not been set
 *      firebaseId: the Firebase ID of the friend. The ID of the same user will be different in
 *        different friend lists. This is okay, because globally the users are defined by their
 *        email. The firebasFirebase ID is stored simply to make it easier to access a specific
 *        friend for a specific user
 */

public class Friend implements Serializable{
    private String email;
    private String nickname;
    private String firebaseId;

    // if you want to just make an empty friend
    Friend(){
        email = "";
        nickname = "";
        firebaseId = "";
    }

    String getNickname() {
        return nickname;
    }

    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String name) {
        this.email = name;
    }

    String getFirebaseId() {
        return firebaseId;
    }

    void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }
}
