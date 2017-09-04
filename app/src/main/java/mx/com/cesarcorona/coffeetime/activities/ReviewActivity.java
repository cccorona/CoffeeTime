package mx.com.cesarcorona.coffeetime.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.models.User;

import java.util.LinkedList;

import mx.com.cesarcorona.coffeetime.MainActivity;
import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.ReviewAdapter;
import mx.com.cesarcorona.coffeetime.pojo.ReviewObject;
import mx.com.cesarcorona.coffeetime.pojo.UserProfile;

import static mx.com.cesarcorona.coffeetime.activities.JustCoffeActivity.USER_PROFILES_REFERENCE;

public class ReviewActivity extends BaseAnimatedActivity {



    public static String KEY_USER = "user";
    public static String REVIEWS_REFENRECE ="reviews";


    private DatabaseReference databaseReference;
    private DatabaseReference userDAtabase;
    private ProgressDialog pDialog;
    private String userUUID ;
    private LinkedList<ReviewObject> reviewObjects;
    private ReviewAdapter reviewAdapter;
    private ListView reviewsList;
    private LinearLayout lienarButton;
    private ImageView homeIcon;
    private TextView userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        reviewsList = (ListView) findViewById(R.id.review_list);
        homeIcon = (ImageView)findViewById(R.id.home_icon);
        lienarButton = (LinearLayout) findViewById(R.id.linear_back_button);
        userName = (TextView) findViewById(R.id.user_name_reviews);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setCancelable(false);
        showpDialog();
        reviewObjects = new LinkedList<>();
        userUUID = getIntent().getExtras().getString(KEY_USER);
        userDAtabase = FirebaseDatabase.getInstance().getReference(USER_PROFILES_REFERENCE +"/" +userUUID);
        userDAtabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                userName.setText(userProfile.getName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference(REVIEWS_REFENRECE + "/" + userUUID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot review:dataSnapshot.getChildren()){
                    ReviewObject object = review.getValue(ReviewObject.class);
                    reviewObjects.add(object);
                }

                reviewAdapter = new ReviewAdapter(ReviewActivity.this,reviewObjects);
                reviewsList.setAdapter(reviewAdapter);
                hidepDialog();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();

            }
        });

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ReviewActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

        lienarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewActivity.this.onBackPressed();
            }
        });

    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
