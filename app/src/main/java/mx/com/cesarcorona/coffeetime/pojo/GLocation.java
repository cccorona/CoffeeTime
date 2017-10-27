package mx.com.cesarcorona.coffeetime.pojo;

import java.io.Serializable;

/**
 * Created by ccabrera on 26/10/17.
 */

public class GLocation implements Serializable {


    private double lat;
    private double lng;


    public GLocation() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
