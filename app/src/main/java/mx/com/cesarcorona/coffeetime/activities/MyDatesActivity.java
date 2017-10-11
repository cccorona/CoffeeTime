package mx.com.cesarcorona.coffeetime.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import mx.com.cesarcorona.coffeetime.MainActivity;
import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.CoffeDateAdapter;
import mx.com.cesarcorona.coffeetime.adapter.MyDatesAdapter;
import mx.com.cesarcorona.coffeetime.adapter.ReviewAdapter;
import mx.com.cesarcorona.coffeetime.dialogs.FullScreenDialog;
import mx.com.cesarcorona.coffeetime.pojo.CoffeDate;
import mx.com.cesarcorona.coffeetime.pojo.ReviewObject;
import mx.com.cesarcorona.coffeetime.pojo.UserProfile;

import static mx.com.cesarcorona.coffeetime.activities.JustCoffeActivity.USER_PROFILES_REFERENCE;

public class MyDatesActivity extends BaseAnimatedActivity implements MyDatesAdapter.MatchingInterface, CoffeDate.FillInformationInterface {


    public static String TAG = SearchActivity.class.getSimpleName();
    public static String DATES_REFERENCE = "dates/";
    public static String USER_DATES_REFERENCE ="mydates";

    private ProgressDialog pDialog;
    private LinkedList<CoffeDate> availableDates;
    private ImageView homeButton;
    private DatabaseReference databaseReference;
    private MyDatesAdapter myDatesAdapter;
    private ListView myDatesList;
    private LinearLayout lienarButton;
    private ImageView homeIcon;


    private int numberOfSrikes;
    private int numberErrors;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dates);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.please_wait_d));
        pDialog.setCancelable(false);
        homeButton = (ImageView) findViewById(R.id.home_icon);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeActivity = new Intent(MyDatesActivity.this, MainSettingsActivity.class);
                startActivity(homeActivity);
                finish();
            }
        });
        homeIcon = (ImageView)findViewById(R.id.home_icon);
        lienarButton = (LinearLayout) findViewById(R.id.linear_back_button);
        availableDates = new LinkedList<>();
        myDatesList = (ListView) findViewById(R.id.list);

        databaseReference = FirebaseDatabase.getInstance().getReference(USER_DATES_REFERENCE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot review:dataSnapshot.getChildren()){
                    CoffeDate object = review.getValue(CoffeDate.class);
                    availableDates.add(object);
                }

                generateMatchingCards();



               /* myDatesAdapter = new MyDatesAdapter(MyDatesActivity.this,availableDates);
                myDatesAdapter.setMatchingInterface(MyDatesActivity.this);
                myDatesList.setAdapter(myDatesAdapter);*/

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
                Intent mainIntent = new Intent(MyDatesActivity.this, MainSettingsActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

        lienarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatesActivity.this.onBackPressed();
            }
        });

    }




    @Override
    public void OnConnectButton(CoffeDate coffeDate) {

        Bundle arguments = new Bundle();
        arguments.putSerializable(FullScreenDialog.KEY_DATE,coffeDate);
        FullScreenDialog rateDialog = new FullScreenDialog();
        rateDialog.setArguments(arguments);
        rateDialog.show(getSupportFragmentManager(),FullScreenDialog.TAG);

    }

    @Override
    public void OnReview(CoffeDate coffeDate) {

        Intent intent= new Intent(MyDatesActivity.this, ReviewActivity.class);
        Bundle extras = new Bundle();
        String wichUser ="";
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(coffeDate.getUser1())){
            wichUser = coffeDate.getUser2();
        }else{
            wichUser = coffeDate.getUser1();
        }

        extras.putString(ReviewActivity.KEY_USER,wichUser);
        intent.putExtras(extras);
        startActivity(intent);


    }

    @Override
    public void OnChat(CoffeDate coffeDate) {

        Bundle extras = new Bundle();
        String wichUser = "";
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(coffeDate.getUser1())){
            wichUser = coffeDate.getUser2();
        }else{
            wichUser = coffeDate.getUser1();
        }
        extras.putString(ChatActivity.KEY_COFFEDATE,wichUser);
        extras.putString(ChatActivity.KEY_COFFEDATE_USER1,coffeDate.getUser1());
        extras.putString(ChatActivity.KEY_COFFEDATE_USER2,coffeDate.getUser2());
        Intent chatIntent = new Intent(this,ChatActivity.class);
        chatIntent.putExtras(extras);
        startActivity(chatIntent);

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void generateMatchingCards(){
        String myUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for(CoffeDate coffeDate:availableDates){
            coffeDate.setFillInformationInterface(this);
            String wichUser = "";

            if(myUser.equals(coffeDate.getUser1())){
                wichUser = coffeDate.getUser2();
            }else{
                wichUser = coffeDate.getUser1();
            }
            coffeDate.fullFillUserProfileWithReference(CoffeTimeActiviy.USER_PROFILES_REFERENCE +"/" +wichUser);
        }

    }

    @Override
    public void OnDataChangeSuccess() {

        numberOfSrikes++;
        if(numberOfSrikes == availableDates.size() && numberErrors == 0){
            //finisFill all user profiles
            myDatesAdapter = new MyDatesAdapter(MyDatesActivity.this,availableDates);
            myDatesAdapter.setMatchingInterface(MyDatesActivity.this);
            myDatesList.setAdapter(myDatesAdapter);

            //  hidepDialog();


        }else {
            // hidepDialog();

            //Show error
        }

    }

    @Override
    public void OnError(String error) {
        numberErrors ++ ;

    }
}
