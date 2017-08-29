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
    private double latitud;
    private double longitud;
    private String favoritePlace;


    public CoffeDate(String time, String placeId, String user1, double latitud, double longitud) {
        this.time = time;
        this.placeId = placeId;
        this.user1 = user1;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public CoffeDate() {
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

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFavoritePlace() {
        return favoritePlace;
    }

    public void setFavoritePlace(String favoritePlace) {
        this.favoritePlace = favoritePlace;
    }
}
