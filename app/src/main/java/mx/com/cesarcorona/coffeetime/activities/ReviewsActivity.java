package mx.com.cesarcorona.coffeetime.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.pojo.UserProfile;

public class ReviewsActivity extends BaseAnimatedActivity {



    public static String TAG = ReviewActivity.class.getSimpleName();
    public static String KEY_USER ="userReview";

    private UserProfile userProfile ;
    private String profileKey ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);


    }
}
