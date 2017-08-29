package mx.com.cesarcorona.coffeetime.pojo;

import java.io.Serializable;

/**
 * Created by ccabrera on 29/08/17.
 */

public class User implements Serializable {

    private String uuidUser;
    private String tokenPush;


    public User(String uuidUser, String tokenPush) {
        this.uuidUser = uuidUser;
        this.tokenPush = tokenPush;
    }


    public String getUuidUser() {
        return uuidUser;
    }

    public void setUuidUser(String uuidUser) {
        this.uuidUser = uuidUser;
    }

    public String getTokenPush() {
        return tokenPush;
    }

    public void setTokenPush(String tokenPush) {
        this.tokenPush = tokenPush;
    }
}
