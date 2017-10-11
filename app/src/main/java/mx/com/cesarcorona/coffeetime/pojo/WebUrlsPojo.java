package mx.com.cesarcorona.coffeetime.pojo;

import java.io.Serializable;

/**
 * Created by ccabrera on 10/10/17.
 */

public class WebUrlsPojo implements Serializable {

    private String urlAboutUs;
    private String contactUS;
    private String support;
    private String qa;


    public WebUrlsPojo() {
    }


    public WebUrlsPojo(String urlAboutUs, String contactUS, String support, String qa) {
        this.urlAboutUs = urlAboutUs;
        this.contactUS = contactUS;
        this.support = support;
        this.qa = qa;
    }


    public String getUrlAboutUs() {
        return urlAboutUs;
    }

    public void setUrlAboutUs(String urlAboutUs) {
        this.urlAboutUs = urlAboutUs;
    }

    public String getContactUS() {
        return contactUS;
    }

    public void setContactUS(String contactUS) {
        this.contactUS = contactUS;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public String getQa() {
        return qa;
    }

    public void setQa(String qa) {
        this.qa = qa;
    }
}
