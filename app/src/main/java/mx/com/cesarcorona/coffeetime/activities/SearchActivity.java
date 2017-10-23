package mx.com.cesarcorona.coffeetime.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appolica.flubber.Flubber;
import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import mx.com.cesarcorona.coffeetime.MainActivity;
import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.CoffeDateAdapter;
import mx.com.cesarcorona.coffeetime.pojo.Categoria;
import mx.com.cesarcorona.coffeetime.pojo.CoffeDate;
import mx.com.cesarcorona.coffeetime.pojo.Topic;

import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_DATE;
import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_TIME;
import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.USER_PROFILES_REFERENCE;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CATEGORIA;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CURRENT_LATIDU;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CURRENT_LONGITUD;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_PLACE_SELECTED;
import static mx.com.cesarcorona.coffeetime.activities.FilterTopicsActivity.KEY_PARTY_NUMBER;
import static mx.com.cesarcorona.coffeetime.activities.FilterTopicsActivity.KEY_TOPIC;
import static mx.com.cesarcorona.coffeetime.activities.JustCoffeActivity.KEY_JUST_COFFE;

public class SearchActivity extends BaseAnimatedActivity implements CoffeDate.FillInformationInterface , CoffeDateAdapter.MatchingInterface {


    public static String TAG = SearchActivity.class.getSimpleName();
    public static String DATES_REFERENCE = "dates/";
    public static String USER_DATES_REFERENCE ="mydates";


    private DatabaseReference databaseReference;

    private Categoria categoriaSeleccionada;
    private Topic topicSeleccionado;
    private String dateSelected ,timeSelected;
    private noman.googleplaces.Place placeSeleccionado;
    private Place ubicacionPreferida;
    private double latitud, longitud;
    private int partyNumber;

    private String DATA_BASE_PATH;
    private ProgressDialog pDialog;
    private LinkedList<CoffeDate> availableDates;
    private String keyDate;
    private int numberOfSrikes;
    private int numberErrors;
    private CoffeDateAdapter coffeDateAdapter;
    private LinearLayout searchingPanel;
    private ListView matchingList ;
    private boolean justCoffe;
    private ImageView animatedLog;
    private TextView not_match;
    private ImageView homeButton;

    private static final long SPLASH_SCREEN_DELAY = 3000;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        matchingList = (ListView) findViewById(R.id.matching_dates_list);
        searchingPanel = (LinearLayout) findViewById(R.id.loading_page);
        animatedLog = (ImageView)findViewById(R.id.animated_logo);
        not_match = (TextView)findViewById(R.id.not_match);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.please_wait_d));
        pDialog.setCancelable(false);
        homeButton = (ImageView) findViewById(R.id.home_icon);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeActivity = new Intent(SearchActivity.this, MainSettingsActivity.class);
                startActivity(homeActivity);
                finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Gson gson = new Gson();
      //  showpDialog();
        numberOfSrikes = 0;
        numberErrors = 0;
        availableDates = new LinkedList<>();
        dateSelected = getIntent().getExtras().getString(KEY_DATE);//ya
        timeSelected = getIntent().getExtras().getString(KEY_TIME);//ya
        placeSeleccionado = gson.fromJson(getIntent().getExtras().getString(KEY_PLACE_SELECTED),noman.googleplaces.Place.class);//ya
        //categoriaSeleccionada = (Categoria) getIntent().getExtras().getSerializable(KEY_CATEGORIA);//ya
        topicSeleccionado = (Topic)getIntent().getExtras().getSerializable(KEY_TOPIC);//ya
        //ubicacionPreferida = gson.fromJson(getIntent().getExtras().getString(KEY_PLACE_SELECTED),Place.class);
        latitud = getIntent().getExtras().getDouble(KEY_CURRENT_LATIDU);
        longitud = getIntent().getExtras().getDouble(KEY_CURRENT_LONGITUD);
        partyNumber = getIntent().getExtras().getInt(KEY_PARTY_NUMBER);
        justCoffe = getIntent().getExtras().getBoolean(KEY_JUST_COFFE);


        DATA_BASE_PATH = buildDataBasePath();
        databaseReference = FirebaseDatabase.getInstance().getReference(DATA_BASE_PATH);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapShot: dataSnapshot.getChildren()){
                    CoffeDate date = snapShot.getValue(CoffeDate.class);
                    date.setDataBaseReference(snapShot.getKey());
                    if(date.getOpenDate()){
                        availableDates.add(date);
                    }
                }


                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        Thread.currentThread()
                                .setName(this.getClass().getSimpleName() + ": " + Thread.currentThread().getName());

                        if(availableDates != null && availableDates.size()>0){
                            generateMatchingCards();
                        }else{
                            presentCoffeDate();
                        }

                    }
                };

                Timer timer = new Timer();
                timer.schedule(task, SPLASH_SCREEN_DELAY);


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Flubber.with()
                .animation(Flubber.AnimationPreset.FADE_OUT_IN) // Slide up animation
                .repeatCount(100)                              // Repeat once
                .duration(1000)                              // Last for 1000 milliseconds(1 second)
                .createFor(animatedLog)                             // Apply it to the view
                .start();




    }



    private void presentCoffeDate(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedLog.setVisibility(View.GONE);
                not_match.setVisibility(View.VISIBLE);
            }
        });

        CoffeDate date = new CoffeDate();
        date.setOpenDate(true);
        date.setTime(dateSelected +"," + timeSelected);
        date.setUser1(FirebaseAuth.getInstance().getCurrentUser().getUid());
        date.setRequestedPlaces(partyNumber);
        date.setFavoritePlace(placeSeleccionado.getName());
        databaseReference.push().setValue(date);
        hidepDialog();
        //Show meessga no match
    }

    private String buildDataBasePath(){

        StringBuilder pathBuilder = new StringBuilder(DATES_REFERENCE);
        //pathBuilder.append(categoriaSeleccionada.getDataBaseReference()).append("/");
        pathBuilder.append(topicSeleccionado.getDataBaseReference()).append("/");
        pathBuilder.append(placeSeleccionado.getPlaceId()).append("/");
        pathBuilder.append(dateSelected).append("/");
        //pathBuilder.append((timeSelected));
        return  pathBuilder.toString();


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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedLog.setVisibility(View.GONE);
                matchingList.setVisibility(View.VISIBLE);
            }
        });


        for(CoffeDate coffeDate:availableDates){
             coffeDate.setFillInformationInterface(this);
             coffeDate.fullFillUserProfileWithReference(USER_PROFILES_REFERENCE +"/" +coffeDate.getUser1() );
        }

    }


    @Override
    public void OnDataChangeSuccess() {
        numberOfSrikes++;
        if(numberOfSrikes == availableDates.size() && numberErrors == 0){
            //finisFill all user profiles
         coffeDateAdapter = new CoffeDateAdapter(this,availableDates);
         coffeDateAdapter.setMatchingInterface(SearchActivity.this);
         matchingList.setAdapter(coffeDateAdapter);

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


    //From Adapter

    @Override
    public void OnConnectButton(CoffeDate coffeDate) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(DATA_BASE_PATH+"/"+coffeDate.getDataBaseReference());
        databaseReference.child("user2").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.child("openDate").setValue(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final CoffeDate date = dataSnapshot.getValue(CoffeDate.class);
                date.setDataBaseReference(dataSnapshot.getKey());
                ((CoffeDateAdapter)matchingList.getAdapter()).updateList(date);
                DatabaseReference myDatesReferemce = FirebaseDatabase.getInstance().getReference(USER_DATES_REFERENCE+"/"
                +date.getUser1()).child(date.getDataBaseReference());
                myDatesReferemce.setValue(date);

                myDatesReferemce.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        DatabaseReference myDatesReferemce2 = FirebaseDatabase.getInstance().getReference(USER_DATES_REFERENCE+"/"
                                +date.getUser2()).child(date.getDataBaseReference());
                        myDatesReferemce2.setValue(date);


                        myDatesReferemce2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Toast.makeText(SearchActivity.this,"Coffe Date Complete",Toast.LENGTH_LONG).show();
                                autoChat(dataSnapshot.getValue(CoffeDate.class));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //maybe retry
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                       //maybe retry
                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void OnReview(CoffeDate coffeDate) {
        Intent intent= new Intent(SearchActivity.this, ReviewActivity.class);
        Bundle extras = new Bundle();
        extras.putString(ReviewActivity.KEY_USER,coffeDate.getUser1());
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

    private void autoChat(CoffeDate coffeDate){
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
}
