package mx.com.cesarcorona.coffeetime.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by ccabrera on 24/08/17.
 */

public class Topic implements Serializable {

    private String display_title;
    private String type;
    @Exclude
    private String dataBaseReference;


    public Topic(String display_title, String type) {
        this.display_title = display_title;
        this.type = type;
    }

    public Topic() {
    }

    public String getDisplay_title() {
        return display_title;
    }

    public void setDisplay_title(String display_title) {
        this.display_title = display_title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getDataBaseReference() {
        return dataBaseReference;
    }

    public void setDataBaseReference(String dataBaseReference) {
        this.dataBaseReference = dataBaseReference;
    }
}
