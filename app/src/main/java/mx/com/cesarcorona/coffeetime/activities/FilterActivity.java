package mx.com.cesarcorona.coffeetime.activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import mx.com.cesarcorona.coffeetime.MainActivity;
import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.CategoryAdapter;
import mx.com.cesarcorona.coffeetime.fragment.WorkaroundMapFragment;
import mx.com.cesarcorona.coffeetime.pojo.Categoria;
import mx.com.cesarcorona.coffeetime.pojo.Topic;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_DATE;
import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_TIME;
import static mx.com.cesarcorona.coffeetime.activities.JustCoffeActivity.KEY_JUST_COFFE;
import static mx.com.cesarcorona.coffeetime.activities.JustCoffeActivity.KEY_TOPIC;

public class FilterActivity extends BaseAnimatedActivity implements OnMapReadyCallback ,CategoryAdapter.CategorySelectedListener, GoogleApiClient.OnConnectionFailedListener , PlacesListener{


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int MAX_ZOOM = 16;
    private static final String CATEGORIAS_REFERENCE = "categorias";
    public static final String KEY_PARTY_NUMBER = "party";


    public static String KEY_CATEGORIA="categoria";
    public static String KEY_FAVORITE_PLACE="place";
    public static String KEY_CURRENT_LATIDU="latitud";
    public static String KEY_CURRENT_LONGITUD="longitud";
    public static String KEY_PLACE_SELECTED="placeselected";
    public static final String KEY_TOPIC="topic";




    private String dateSelected,timeSelected;


    private FusedLocationProviderClient mFusedLocationClient;
    private WorkaroundMapFragment mapFragment;
    private Location currentLocation;
    private GoogleMap currentMap;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference databaseReference;
    private ProgressDialog pDialog;
    private LinkedList<Categoria> allcategorias;
    private Spinner categoriasSelector;
    private CategoryAdapter categoryAdapter;
    private Categoria categoriaSeleccionada;
    private SupportPlaceAutocompleteFragment autocompleteFragment;
    private Place ubicacionPreferida;
    private EditText buscarText;
    private ImageView homeButton, nextButtton, searchButton,centerButton;
    private double latitud, longitud;
    private noman.googleplaces.Place placeSeleccionado;
    private List<noman.googleplaces.Place> placesFound;
    private List<noman.googleplaces.Place> historicPlaces;
    private Topic topicSeleccionado;


    private boolean justCoffe;

    private Button plusButton, minusButton;
    private TextView partyNumber;
    private int number = 1;
    private ScrollView myScroolView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        categoriasSelector = (Spinner) findViewById(R.id.categorySpinner);
        buscarText = (EditText) findViewById(R.id.buscar_text);
        searchButton = (ImageView) findViewById(R.id.search_button);
        centerButton = (ImageView)findViewById(R.id.center_button);
        myScroolView = (ScrollView)findViewById(R.id.myScroolView);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlaces();
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        pDialog = new ProgressDialog(FilterActivity.this);
        pDialog.setMessage("Por favor espera...");
        pDialog.setCancelable(false);
        allcategorias = new LinkedList<>();
        historicPlaces = new LinkedList<>();
        showpDialog();
        justCoffe = false ;
        databaseReference = FirebaseDatabase.getInstance().getReference(CATEGORIAS_REFERENCE);
        homeButton = (ImageView) findViewById(R.id.home_icon);
        nextButtton = (ImageView) findViewById(R.id.next_icon);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeActivity = new Intent(FilterActivity.this, MainActivity.class);
                startActivity(homeActivity);
                finish();
            }
        });

        nextButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(categoriaSeleccionada == null && !justCoffe){
                     Toast.makeText(FilterActivity.this,getString(R.string.selected_category),Toast.LENGTH_LONG).show();
                 }else{
                     Gson gson = new Gson();
                     Bundle extras = new Bundle();
                     extras.putString(KEY_DATE,dateSelected);
                     extras.putString(KEY_TIME,timeSelected);
                     extras.putSerializable(KEY_TOPIC,topicSeleccionado);
                     if(justCoffe){
                         extras.putSerializable(KEY_CATEGORIA,categoriaSeleccionada);

                     }
                     if(ubicacionPreferida == null){
                         extras.putString(KEY_FAVORITE_PLACE,"");

                     }else{
                         extras.putString(KEY_FAVORITE_PLACE,gson.toJson(ubicacionPreferida));

                     }
                     extras.putDouble(KEY_CURRENT_LATIDU,latitud);
                     extras.putDouble(KEY_CURRENT_LONGITUD,longitud);
                     if(placeSeleccionado == null){
                         extras.putString(KEY_PLACE_SELECTED,"");
                         Toast.makeText(FilterActivity.this,getString(R.string.place_Selected),Toast.LENGTH_LONG).show();
                         return;


                     }else{
                         extras.putString(KEY_PLACE_SELECTED,gson.toJson(placeSeleccionado));

                     }
                     extras.putBoolean(KEY_JUST_COFFE,justCoffe);
                     extras.putInt(KEY_PARTY_NUMBER,number);

                     Intent filterIntent = new Intent(FilterActivity.this,SearchActivity.class);
                     filterIntent.putExtras(extras);
                     startActivity(filterIntent);
                     finish();

                 }
            }
        });

        mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
               myScroolView.requestDisallowInterceptTouchEvent(true);

            }
        });

        if (ContextCompat.checkSelfPermission(FilterActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(FilterActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block


            } else {


                ActivityCompat.requestPermissions(FilterActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = location;
                                centerLocationOnScreenFirstTime();
                            }
                        }
                    });
        }
        justCoffe = getIntent().getExtras().getBoolean(KEY_JUST_COFFE);

        if(!justCoffe){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot categoriaSnap : dataSnapshot.getChildren()) {
                        Categoria categoria = categoriaSnap.getValue(Categoria.class);
                        categoria.setDataBaseReference(categoriaSnap.getKey());
                        allcategorias.add(categoria);
                    }

                    LinkedList<String> list = new LinkedList<String>();
                    for(Categoria topic:allcategorias){
                        list.add(topic.getDisplay_title());
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(FilterActivity.this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categoriasSelector.setAdapter(dataAdapter);

                    categoryAdapter = new CategoryAdapter(allcategorias, FilterActivity.this);
                    //categoryAdapter.setCategorySelectedListener(FilterActivity.this);
                    //categoriasSelector.setAdapter(categoryAdapter);
                    hidepDialog();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // hidepDialog();


                }
            });
        }



        categoriasSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = allcategorias.get(position);
                Toast.makeText(parent.getContext(),
                        "Category selected : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getSupportFragmentManager().
                        findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY).setCountry("MX")
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                ubicacionPreferida = place;
                latitud = ubicacionPreferida.getLatLng().latitude;
                longitud = ubicacionPreferida.getLatLng().longitude;
                showpDialog();
                centerLocationOnSelectedPlaceAndSearch();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        dateSelected = getIntent().getExtras().getString(KEY_DATE);
        timeSelected = getIntent().getExtras().getString(KEY_TIME);
        topicSeleccionado = (Topic)getIntent().getExtras().getSerializable(FilterTopicsActivity.KEY_TOPIC);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMapOnCurrentLocation();
            }
        });

        plusButton =(Button) findViewById(R.id.plus_button);
        minusButton =(Button) findViewById(R.id.minus_button);
        partyNumber = (TextView)findViewById(R.id.agetext);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number < 2){
                    partyNumber.setText(""+ ++number);

                }
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



    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        currentLocation = location;
                                        centerLocationOnScreenFirstTime();
                                    }
                                }
                            });

                } else {

                  //Negaron los permisos
                }
                return;
            }


        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        currentMap = googleMap;
        currentMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                for(noman.googleplaces.Place plafes:historicPlaces){
                    if(plafes.getLatitude() == marker.getPosition().latitude ){
                        if(plafes.getLongitude() == marker.getPosition().longitude){
                            placeSeleccionado = plafes;
                            break;
                        }
                    }
                }
                marker.showInfoWindow();

                return false;
            }
        });
    }

    private void centerLocationOnScreen(){
        currentMap.clear();
        currentMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()))
                .title(getString(R.string.current_position))).showInfoWindow();
        currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),MAX_ZOOM));
        //latitud = currentLocation.getLatitude();
        //longitud = currentLocation.getLongitude();

    }


    private void centerLocationOnSelectedPlaceAndSearch(){
        currentMap.clear();
        historicPlaces = new LinkedList<>();
        currentMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()))
                .title(getString(R.string.favorite_place))).showInfoWindow();
        currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud,longitud),MAX_ZOOM));
        //latitud = currentLocation.getLatitude();
        //longitud = currentLocation.getLongitude();
        if(justCoffe){
            justCoffeSEarch();
        }
    }



    private void centerLocationOnScreenFirstTime(){
        currentMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()))
                .title(getString(R.string.current_position))).showInfoWindow();
        currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),MAX_ZOOM));
        latitud = currentLocation.getLatitude();
        longitud = currentLocation.getLongitude();
        if(justCoffe){
            justCoffeSEarch();
        }


    }






    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void searchPlaces() {
        Locale current = getResources().getConfiguration().locale;


        String keyWord ="";
        if(categoriaSeleccionada != null){
            keyWord = categoriaSeleccionada.getDisplay_title();
        }else{
            keyWord ="";
        }

        new NRPlaces.Builder()
                .listener(this).language(current.getLanguage(),current.getISO3Country())
                .key(getString(R.string.google_api_key))
                .latlng(latitud, longitud)
                .radius(500).keyword(keyWord)
                .build()
                .execute();
    }


    @Override
    public void OnCategoryClicked(Categoria categoria) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        hidepDialog();


    }


    @Override
    public void onPlacesFailure(PlacesException e) {
        hidepDialog();


    }

    @Override
    public void onPlacesStart() {
        hidepDialog();


    }

    @Override
    public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {



        placesFound = places;
        for(noman.googleplaces.Place place :places){
            historicPlaces.add(place);
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("UI thread", "I am the UI thread");

                //currentMap.clear();
                currentMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitud,longitud))
                        .title(getString(R.string.current_position))).showInfoWindow();
                for(noman.googleplaces.Place place :places){
                    currentMap.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLatitude(),place.getLongitude()))
                            .title(place.getName())).showInfoWindow();
                    // currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),MAX_ZOOM));
                }
            }
        });

        hidepDialog();


    }

    @Override
    public void onPlacesFinished() {
        hidepDialog();

    }

    private void justCoffeSEarch() {
        Locale current = getResources().getConfiguration().locale;

        String keyWord ="";
        if(categoriaSeleccionada != null){
            keyWord = categoriaSeleccionada.getDisplay_title();
        }else{
            keyWord ="";
        }

        new NRPlaces.Builder()
                .listener(this).language(current.getLanguage(),current.getCountry())
                .key(getString(R.string.google_api_key))
                .latlng(latitud, longitud)
                .type(PlaceType.CAFE)
                .radius(500)
                .build()
                .execute();
    }


    private void centerMapOnCurrentLocation(){
        if (ContextCompat.checkSelfPermission(FilterActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(FilterActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block


            } else {


                ActivityCompat.requestPermissions(FilterActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = location;
                                centerLocationOnScreenFirstTime();
                            }
                        }
                    });
        }
    }



}
