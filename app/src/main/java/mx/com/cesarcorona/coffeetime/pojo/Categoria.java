package mx.com.cesarcorona.coffeetime.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by Corona on 8/7/2017.
 */

public class Categoria implements Serializable {

    private String display_title;
    private String type;
    @Exclude
    private String dataBaseReference;


    public Categoria(String display_title, String type) {
        this.display_title = display_title;
        this.type = type;
    }

    public Categoria() {
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
