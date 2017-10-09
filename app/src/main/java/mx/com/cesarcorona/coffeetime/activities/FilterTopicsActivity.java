package mx.com.cesarcorona.coffeetime.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mx.com.cesarcorona.coffeetime.MainActivity;
import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.CategoryAdapter;
import mx.com.cesarcorona.coffeetime.adapter.TopicsAdapter;
import mx.com.cesarcorona.coffeetime.pojo.Categoria;
import mx.com.cesarcorona.coffeetime.pojo.Topic;

import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_DATE;
import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_TIME;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CATEGORIA;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CURRENT_LATIDU;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CURRENT_LONGITUD;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_FAVORITE_PLACE;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_PLACE_SELECTED;

public class FilterTopicsActivity extends BaseAnimatedActivity {




    public static final String KEY_TOPIC="topic";
    public static final String KEY_PARTY_NUMBER = "party";



    private ImageView homeButton , nextButtton;
    private Categoria categoriaSeleccionada;
    private Topic topicSeleccionado;
    private String dateSelected ,timeSelected;
    private noman.googleplaces.Place placeSeleccionado;
    private Place ubicacionPreferida;
    private double latitud, longitud;

    private static final String TOPICS_REFERENCE = "topicos";


    private DatabaseReference databaseReference;
    private ProgressDialog pDialog;
    private LinkedList<Topic> allTopics;
    private Spinner topicSelector;
    private TopicsAdapter categoryAdapter;


    private Button plusButton, minusButton;
    private TextView partyNumber;
    private int number = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_topics);
        pDialog = new ProgressDialog(FilterTopicsActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        allTopics = new LinkedList<>();
        showpDialog();
        topicSelector = (Spinner) findViewById(R.id.categorySpinner);
        plusButton =(Button) findViewById(R.id.plus_button);
        minusButton =(Button) findViewById(R.id.minus_button);
        partyNumber = (TextView)findViewById(R.id.agetext);
        databaseReference = FirebaseDatabase.getInstance().getReference(TOPICS_REFERENCE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);





        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number++;
                partyNumber.setText(""+number);
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number>1){
                    number--;
                    partyNumber.setText(""+number);
                }
            }
        });


        homeButton = (ImageView) findViewById(R.id.home_icon);
        nextButtton = (ImageView) findViewById(R.id.next_icon);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeActivity = new Intent(FilterTopicsActivity.this, MainSettingsActivity.class);
                startActivity(homeActivity);
                finish();
            }
        });

        nextButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topicSeleccionado == null){
                    Toast.makeText(FilterTopicsActivity.this,getString(R.string.selected_topic),Toast.LENGTH_LONG).show();
                }else{
                    Gson gson = new Gson();
                    Bundle extras = new Bundle();
                    extras.putString(KEY_DATE,dateSelected);
                    extras.putString(KEY_TIME,timeSelected);
                    extras.putSerializable(KEY_CATEGORIA,categoriaSeleccionada);
                    if(ubicacionPreferida == null){
                        extras.putString(KEY_FAVORITE_PLACE,"");

                    }else{
                        extras.putString(KEY_FAVORITE_PLACE,gson.toJson(ubicacionPreferida));

                    }
                    extras.putDouble(KEY_CURRENT_LATIDU,latitud);
                    extras.putDouble(KEY_CURRENT_LONGITUD,longitud);
                    if(placeSeleccionado == null){
                        extras.putString(KEY_PLACE_SELECTED,"");

                    }else{
                        extras.putString(KEY_PLACE_SELECTED,gson.toJson(placeSeleccionado));

                    }
                    extras.putInt(KEY_PARTY_NUMBER,number);
                    extras.putSerializable(KEY_TOPIC,topicSeleccionado);
                    Intent filterIntent = new Intent(FilterTopicsActivity.this,SearchActivity.class);
                    filterIntent.putExtras(extras);
                    startActivity(filterIntent);
                    finish();

                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot categoriaSnap : dataSnapshot.getChildren()) {
                    Topic topic = categoriaSnap.getValue(Topic.class);
                    topic.setDataBaseReference(categoriaSnap.getKey());
                    allTopics.add(topic);
                }

                LinkedList<String> list = new LinkedList<String>();
                for(Topic topic:allTopics){
                    list.add(topic.getDisplay_title());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(FilterTopicsActivity.this,
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                topicSelector.setAdapter(dataAdapter);

                categoryAdapter = new TopicsAdapter(allTopics, FilterTopicsActivity.this);
                //categoryAdapter.setCategorySelectedListener(FilterTopicsActivity.this);
           //     topicSelector.setAdapter(categoryAdapter);
                hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        topicSelector.setOnItemSelectedListener(new mySpinnerListener());



        Gson gson = new Gson();
        dateSelected = getIntent().getExtras().getString(KEY_DATE);
        timeSelected = getIntent().getExtras().getString(KEY_TIME);
        placeSeleccionado = gson.fromJson(getIntent().getExtras().getString(KEY_PLACE_SELECTED),noman.googleplaces.Place.class);
        categoriaSeleccionada = (Categoria) getIntent().getExtras().getSerializable(KEY_CATEGORIA);
       // ubicacionPreferida = gson.fromJson(getIntent().getExtras().getString(KEY_PLACE_SELECTED),Place.class);
        latitud = getIntent().getExtras().getDouble(KEY_CURRENT_LATIDU);
        longitud = getIntent().getExtras().getDouble(KEY_CURRENT_LONGITUD);



    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    class mySpinnerListener implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {

            Toast.makeText(parent.getContext(),
                    "Topic selected : " + parent.getItemAtPosition(position).toString(),
                    Toast.LENGTH_SHORT).show();
            topicSeleccionado = allTopics.get(position);

        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }

    }


}
