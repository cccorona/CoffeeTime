package mx.com.cesarcorona.coffeetime.pojo;

import android.location.Location;

import java.util.Arrays;

/**
 * Created by ccabrera on 26/10/17.
 */

public class GPlace {
    /**
     * coordinates of place
     */
    private Geometry geometry;
    /**
     * icon of place, not all places have icon.
     */
    private String icon;
    /**
     * name of place
     */
    private String name;
    /**
     * place_id, can get place details using this id
     */
    private String id;
    /**
     * types of place
     */
    private String[] types;
    /**
     * near area of place
     */
    private String vicinity;

    public GPlace() {
    }



    @Override
    public String toString() {
        return "Place{" +
                "location=" + geometry +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", placeId='" + id + '\'' +
                ", types=" + Arrays.toString(types) +
                ", vicinity='" + vicinity + '\'' +
                '}';
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Get icon of the place
     * Note : Not all places have icons.
     *
     * @return url of icon e.g. https://maps.gstatic.com/mapfiles/place_api/icons/cafe-71.png
     */
    public String getIcon() {
        return icon;
    }

    /**
     * set icon of location
     *
     * @param icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * returns name of location
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * set name of location
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get placeid of location.
     * placeid is string id and can be used to get details of a place
     *
     * @return
     */
    public String getPlaceId() {
        return id;
    }

    /**
     * set place id
     *
     * @param placeId
     */
    public void setPlaceId(String placeId) {
        this.id = placeId;
    }

    /**
     * returns types of location e.g. types ["cafe","food","point_of_interest","establishment"]
     *
     * @return String array types
     */
    public String[] getTypes() {
        return types;
    }

    /**
     * set location types
     *
     * @param types
     */
    public void setTypes(String[] types) {
        this.types = types;
    }

    /**
     * returns address/surrounding of place
     *
     * @return vicinity of place
     */
    public String getVicinity() {
        return vicinity;
    }

    /**
     * set vicinity
     *
     * @param vicinity
     */
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public double getLatitude(){
        return geometry.getLocation().getLat();
    }

    public double getLongitude(){
        return geometry.getLocation().getLng();
    }


}