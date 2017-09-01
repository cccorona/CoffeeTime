package mx.com.cesarcorona.coffeetime.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.LinkedList;

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

public class SearchActivity extends BaseAnimatedActivity implements CoffeDate.FillInformationInterface , CoffeDateAdapter.MatchingInterface {


    public static String TAG = SearchActivity.class.getSimpleName();
    public static String DATES_REFERENCE = "dates/";


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        matchingList = (ListView) findViewById(R.id.matching_dates_list);
        searchingPanel = (LinearLayout) findViewById(R.id.loading_page);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Por favor espera...");
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Gson gson = new Gson();
        showpDialog();
        numberOfSrikes = 0;
        numberErrors = 0;
        availableDates = new LinkedList<>();
        dateSelected = getIntent().getExtras().getString(KEY_DATE);//ya
        timeSelected = getIntent().getExtras().getString(KEY_TIME);//ya
        placeSeleccionado = gson.fromJson(getIntent().getExtras().getString(KEY_PLACE_SELECTED),noman.googleplaces.Place.class);//ya
        categoriaSeleccionada = (Categoria) getIntent().getExtras().getSerializable(KEY_CATEGORIA);//ya
        topicSeleccionado = (Topic)getIntent().getExtras().getSerializable(KEY_TOPIC);//ya
        ubicacionPreferida = gson.fromJson(getIntent().getExtras().getString(KEY_PLACE_SELECTED),Place.class);
        latitud = getIntent().getExtras().getDouble(KEY_CURRENT_LATIDU);
        longitud = getIntent().getExtras().getDouble(KEY_CURRENT_LONGITUD);
        partyNumber = getIntent().getExtras().getInt(KEY_PARTY_NUMBER);

        DATA_BASE_PATH = buildDataBasePath();
        databaseReference = FirebaseDatabase.getInstance().getReference(DATA_BASE_PATH);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapShot: dataSnapshot.getChildren()){
                    CoffeDate date = snapShot.getValue(CoffeDate.class);
                    date.setDataBaseReference(snapShot.getKey());
                    if(date.getTime().equalsIgnoreCase(timeSelected)){
                        if(placeSeleccionado.getPlaceId().equalsIgnoreCase(date.getPlaceId())){
                            if(date.getUser2().equalsIgnoreCase("") || date.getUser2() == null){
                                availableDates.add(date);
                            }
                        }
                    }
                }

                if(availableDates != null && availableDates.size()>0){
                    generateMatchingCards();
                }else{
                    presentCoffeDate();
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }



    private void presentCoffeDate(){
        CoffeDate date = new CoffeDate();
        date.setLatitud(latitud);
        date.setLongitud(longitud);
        date.setUser1(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(placeSeleccionado != null){
            date.setPlaceId(placeSeleccionado.getPlaceId());
        }
        date.setRequestedPlaces(partyNumber);
        if(ubicacionPreferida != null){
            date.setFavoritePlace(ubicacionPreferida.getId());
        }

        databaseReference.push().setValue(date);
        hidepDialog();
        //Show meessga no match
    }

    private String buildDataBasePath(){

        StringBuilder pathBuilder = new StringBuilder(DATES_REFERENCE);
        pathBuilder.append(categoriaSeleccionada.getDataBaseReference()).append("/");
        pathBuilder.append(topicSeleccionado.getDataBaseReference()).append("/");
        pathBuilder.append(dateSelected);
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
        for(CoffeDate coffeDate:availableDates){
             coffeDate.fullFillUserProfileWithReference(USER_PROFILES_REFERENCE +"/" +coffeDate.getUser1() );
        }

    }


    @Override
    public void OnDataChangeSuccess() {
        if(numberOfSrikes == availableDates.size() && numberErrors == 0){
            //finisFill all user profiles
         coffeDateAdapter = new CoffeDateAdapter(this,availableDates);
         matchingList.setAdapter(coffeDateAdapter);
            hidepDialog();


        }else {
            hidepDialog();

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(DATES_REFERENCE+coffeDate.getDataBaseReference());
        databaseReference.child("user2").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CoffeDate date = dataSnapshot.getValue(CoffeDate.class);
                ((CoffeDateAdapter)matchingList.getAdapter()).updateList(date);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void OnReview(CoffeDate coffeDate) {
        Intent reviewIntent = new Intent(this,ReviewActivity.class);
        startActivity(reviewIntent);
    }

    @Override
    public void OnChat(CoffeDate coffeDate) {

        Intent chatIntent = new Intent(this,ChatActivity.class);
        startActivity(chatIntent);

    }
}
