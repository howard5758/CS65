package cs65.edu.dartmouth.cs.gifto;

/**
 * Created by Oliver on 2/25/2018.
 *
 * Even though it's a very simple class, I think it will make adding to databases easier
 */

public class Friend {
    private String name;
    private String nickname;

    // if you want to just make an empty friend
    Friend(){}

    // to initialize with name and nickname, if known
    Friend(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
