package mx.com.cesarcorona.coffeetime.pojo;

import java.io.Serializable;

/**
 * Created by ccabrera on 03/09/17.
 */

public class ReviewObject implements Serializable {


    private String reviewer;
    private String commentary;
    private int rateStars;
    private String reviewwerPhotoUrl;


    public ReviewObject(String reviewer, String commentary, int rateStars) {
        this.reviewer = reviewer;
        this.commentary = commentary;
        this.rateStars = rateStars;
    }

    public ReviewObject() {
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public int getRateStars() {
        return rateStars;
    }

    public void setRateStars(int rateStars) {
        this.rateStars = rateStars;
    }


    public String getReviewwerPhotoUrl() {
        return reviewwerPhotoUrl;
    }

    public void setReviewwerPhotoUrl(String reviewwerPhotoUrl) {
        this.reviewwerPhotoUrl = reviewwerPhotoUrl;
    }
}
