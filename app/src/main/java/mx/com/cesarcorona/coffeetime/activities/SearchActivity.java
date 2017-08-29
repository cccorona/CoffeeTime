package mx.com.cesarcorona.coffeetime.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.location.places.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.LinkedList;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.pojo.Categoria;
import mx.com.cesarcorona.coffeetime.pojo.CoffeDate;
import mx.com.cesarcorona.coffeetime.pojo.Topic;

import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_DATE;
import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_TIME;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CATEGORIA;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CURRENT_LATIDU;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CURRENT_LONGITUD;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_PLACE_SELECTED;
import static mx.com.cesarcorona.coffeetime.activities.FilterTopicsActivity.KEY_PARTY_NUMBER;
import static mx.com.cesarcorona.coffeetime.activities.FilterTopicsActivity.KEY_TOPIC;

public class SearchActivity extends AppCompatActivity {


    public static String TAG = SearchActivity.class.getSimpleName();
    public static String DATES_REFERENCE = "dates";


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Gson gson = new Gson();
        showpDialog();
        availableDates = new LinkedList<>();
        dateSelected = getIntent().getExtras().getString(KEY_DATE);
        timeSelected = getIntent().getExtras().getString(KEY_TIME);
        placeSeleccionado = gson.fromJson(getIntent().getExtras().getString(KEY_PLACE_SELECTED),noman.googleplaces.Place.class);
        categoriaSeleccionada = (Categoria) getIntent().getExtras().getSerializable(KEY_CATEGORIA);
        topicSeleccionado = (Topic)getIntent().getExtras().getSerializable(KEY_TOPIC);
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
                    availableDates.add(date);
                }

                updateUI();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        hidepDialog();


    }

    private void updateUI(){

    }

    private String buildDataBasePath(){

        StringBuilder pathBuilder = new StringBuilder();
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



}
