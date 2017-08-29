package mx.com.cesarcorona.coffeetime.pojo;

import java.io.Serializable;

/**
 * Created by Corona on 8/28/2017.
 */

public class CoffeDate implements Serializable {

    private String time;
    private String placeId;
    private int requestedPlaces;
    private String user1;
    private String user2;

    public CoffeDate(String time, String placeId, int requestedPlaces, String user1, String user2) {
        this.time = time;
        this.placeId = placeId;
        this.requestedPlaces = requestedPlaces;
        this.user1 = user1;
        this.user2 = user2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getRequestedPlaces() {
        return requestedPlaces;
    }

    public void setRequestedPlaces(int requestedPlaces) {
        this.requestedPlaces = requestedPlaces;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }
}
