package mx.com.cesarcorona.coffeetime.pojo;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by ccabrera on 26/10/17.
 */

public class Geometry implements Serializable {

    GLocation location;


    public Geometry() {
    }


    public GLocation getLocation() {
        return location;
    }

    public void setLocation(GLocation location) {
        this.location = location;
    }
}
