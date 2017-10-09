package mx.com.cesarcorona.coffeetime.fragment;

import android.Manifest;
import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.appolica.flubber.Flubber;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy;
import mx.com.cesarcorona.coffeetime.activities.FilterActivity;
import mx.com.cesarcorona.coffeetime.activities.FilterTopicsActivity;
import mx.com.cesarcorona.coffeetime.activities.SearchActivity;
import mx.com.cesarcorona.coffeetime.adapter.CategoryAdapter;
import mx.com.cesarcorona.coffeetime.adapter.TopicsAdapter;
import mx.com.cesarcorona.coffeetime.pojo.Categoria;
import mx.com.cesarcorona.coffeetime.pojo.Topic;
import mx.com.cesarcorona.coffeetime.pojo.User;
import mx.com.cesarcorona.coffeetime.pojo.UserProfile;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_DATE;
import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.KEY_TIME;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CATEGORIA;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CURRENT_LATIDU;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_CURRENT_LONGITUD;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_FAVORITE_PLACE;
import static mx.com.cesarcorona.coffeetime.activities.FilterActivity.KEY_PLACE_SELECTED;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.KEY_TOKEN;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.PREFERENCES_KEY;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.USERS_REFERENCE;

/**
 * Created by ccabrera on 24/09/17.
 */

public class CoffeOptionsFragment extends Fragment implements OnMapReadyCallback ,CategoryAdapter.CategorySelectedListener, GoogleApiClient.OnConnectionFailedListener , PlacesListener {


    public static String TAG = CoffeOptionsFragment.class.getSimpleName();


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int MAX_ZOOM = 16;
    private static final String CATEGORIAS_REFERENCE = "categorias";
    private static final String TOPICS_REFERENCE = "topicos";


    public static String KEY_DATE = "date";
    public static String KEY_TIME = "time";
    public static final String KEY_PARTY_NUMBER = "party";
    public static String KEY_CATEGORIA="categoria";
    public static String KEY_FAVORITE_PLACE="place";
    public static String KEY_CURRENT_LATIDU="latitud";
    public static String KEY_CURRENT_LONGITUD="longitud";
    public static String KEY_PLACE_SELECTED="placeselected";
    public static final String KEY_TOPIC="topic";




    public static final int DATE_OPTION = 0;
    public static final int LOCATION_OPTION = 1;
    public static final int TOPIC_OPTION = 2;




    private LinearLayout linearTimeAndDate , linearLocation, linearTopic, topicContent;
    private ImageView timeFinish, locationFinish , topiFinish;
    private RelativeLayout timeAnDateContent ,locationContent;


    private Calendar myCalendar;
    private EditText dateEditText , timeEditText;
    private ImageView dateSelector , timeSelector;
    private  OnActionSelectedListener onActionSelectedListener;
    private boolean isFirsttime;
    private boolean fakeOnCreate;



    private FusedLocationProviderClient mFusedLocationClient;
    private WorkaroundMapFragment mapFragment;
    private Location currentLocation;
    private GoogleMap currentMap;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference databaseReference;
    private ProgressDialog pDialog;
    private LinkedList<Categoria> allcategorias;
    private Spinner categoriasSelector;
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

    private Button plusButton, minusButton;
    private TextView partyNumber;
    private int number = 1;
    private ScrollView myScroolView;
    private boolean justCoffe;

    private LinkedList<Topic> allTopics;
    private Spinner topicSelector;
    private TopicsAdapter categoryAdapter;
    private RelativeLayout searchButtonR;

    private String dateSelected , timeSelected;







    private View rootView;





    public interface OnActionSelectedListener {
         void OnSelectedOption(int optionSelected);
         void OnFirstOptionSelected();
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.coffe_options_fragment,container,false);
        timeFinish = (ImageView) rootView.findViewById(R.id.date_finish);
        locationFinish = (ImageView)rootView.findViewById(R.id.location_finish);
        topiFinish = (ImageView)rootView.findViewById(R.id.topic_finish);
        topicSelector = (Spinner) rootView.findViewById(R.id.categorySpinner);
        databaseReference = FirebaseDatabase.getInstance().getReference(TOPICS_REFERENCE);
        searchButtonR = (RelativeLayout)rootView.findViewById(R.id.start_searching_button);





        linearTimeAndDate = (LinearLayout) rootView.findViewById(R.id.linear_date_button);
        timeAnDateContent = (RelativeLayout)rootView.findViewById(R.id.date_time_content);
        locationContent = (RelativeLayout)rootView.findViewById(R.id.location_content);
        topicContent = (LinearLayout)rootView.findViewById(R.id.topic_content);
        linearLocation= (LinearLayout)rootView.findViewById(R.id.linear_location_button);
        linearTopic = (LinearLayout) rootView.findViewById(R.id.linear_topyc);

        linearTimeAndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTimeAndDate();
            }
        });

        linearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fakeOnCreate){
                    fakeOnCreate = false;
                    setUpLocationOptions();
                }
                toogleLocation();
            }
        });

        linearTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFilters();
            }
        });





        linearTopic = (LinearLayout) rootView.findViewById(R.id.linear_topyc);

        myCalendar = Calendar.getInstance(Locale.getDefault());
        dateEditText= (EditText) rootView.findViewById(R.id.date_text);
        timeEditText = (EditText) rootView.findViewById(R.id.time_text);
        timeSelector = (ImageView)rootView.findViewById(R.id.time_selector);
        dateSelector = (ImageView)rootView.findViewById(R.id.date_selector);
        dateSelector.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener)getActivity(), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener)getActivity(), myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity())).show();

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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                topicSelector.setAdapter(dataAdapter);

                categoryAdapter = new TopicsAdapter(allTopics,getActivity());
                //categoryAdapter.setCategorySelectedListener(FilterTopicsActivity.this);
                //     topicSelector.setAdapter(categoryAdapter);
               // hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        topicSelector.setOnItemSelectedListener(new CoffeOptionsFragment.mySpinnerListener());




        searchButtonR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent filterIntent = new Intent(getActivity(),SearchActivity.class);
                filterIntent.putExtras(extras);
                startActivity(filterIntent);
                getActivity().finish();
            }
        });


        return  rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionSelectedListener = (OnActionSelectedListener)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirsttime = true;
        fakeOnCreate = true;
        justCoffe = true;
        allTopics = new LinkedList<>();

    }

    public void updateLabel(String date){
        dateSelected = date;
        dateEditText.setText(date);
        optionsIsFinish();

    }

    public void updateTimeLabel(String data){
        timeSelected = data;
        timeEditText.setText(data);
        optionsIsFinish();

    }

    public void finishOption(int finishedOption){
        switch (finishedOption){
            case DATE_OPTION:
                timeFinish.setVisibility(View.VISIBLE);
                break;
            case LOCATION_OPTION:
                locationFinish.setVisibility(View.VISIBLE);
                break;
            case TOPIC_OPTION:
                topiFinish.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void toogleLocation(){
        optionsIsFinish();

        if(isFirsttime){
                if(onActionSelectedListener != null){
                    onActionSelectedListener.OnFirstOptionSelected();
                    isFirsttime = false ;
                }
            }
            if(locationContent.getVisibility() ==View.VISIBLE){
                locationContent.setVisibility(GONE);
            }else{
                locationContent.setVisibility(View.VISIBLE);

            }
            topicContent.setVisibility(GONE);
            timeAnDateContent.setVisibility(GONE);
    }

    private void toggleFilters(){
        optionsIsFinish();

        if(isFirsttime){
            if(onActionSelectedListener != null){
                onActionSelectedListener.OnFirstOptionSelected();
                isFirsttime = false ;
            }
        }
        if(topicContent.getVisibility() ==View.VISIBLE){
            topicContent.setVisibility(GONE);
        }else{
            topicContent.setVisibility(View.VISIBLE);

        }
        locationContent.setVisibility(GONE);
        timeAnDateContent.setVisibility(GONE);
    }

    private void toggleTimeAndDate(){
        optionsIsFinish();
        if(isFirsttime){
            if(onActionSelectedListener != null){
                onActionSelectedListener.OnFirstOptionSelected();
                isFirsttime = false ;
            }
        }
        if(timeAnDateContent.getVisibility() ==View.VISIBLE){
            timeAnDateContent.setVisibility(GONE);
        }else{
            timeAnDateContent.setVisibility(View.VISIBLE);

        }
        topicContent.setVisibility(GONE);
        locationContent.setVisibility(GONE);


      /*  Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                 if(timeAnDateContent.getVisibility() ==View.VISIBLE){
                     timeAnDateContent.setVisibility(GONE);
                 }else{
                     timeAnDateContent.setVisibility(View.VISIBLE);

                 }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        if(timeAnDateContent.getVisibility() == View.VISIBLE){
            Animator animator= Flubber.with()
                    .animation(Flubber.AnimationPreset.FADE_IN_UP) // Slide up animation
                    .repeatCount(1)                              // Repeat once
                    .duration(1000)                              // Last for 1000 milliseconds(1 second)
                    .createFor(timeAnDateContent);               // Apply it to the view
            animator.addListener(animatorListener);
            animator.start();

        }else{
            Animator animator= Flubber.with()
                    .animation(Flubber.AnimationPreset.FADE_IN_DOWN) // Slide up animation
                    .repeatCount(1)                              // Repeat once
                    .duration(1000)                              // Last for 1000 milliseconds(1 second)
                    .createFor(timeAnDateContent);               // Apply it to the view
            animator.addListener(animatorListener);
            animator.start();
        }*/

    }


    private void setUpLocationOptions(){
        categoriasSelector = (Spinner) rootView.findViewById(R.id.categorySpinner);
        buscarText = (EditText) rootView.findViewById(R.id.buscar_text);
        searchButton = (ImageView) rootView.findViewById(R.id.search_button);
        centerButton = (ImageView)rootView.findViewById(R.id.center_button);
        myScroolView = (ScrollView)rootView.findViewById(R.id.myScroolView);
       /* searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlaces();
            }
        });*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.please_wait_d));
        pDialog.setCancelable(false);
        allcategorias = new LinkedList<>();
        historicPlaces = new LinkedList<>();
       // showpDialog();
        justCoffe = true ;
        databaseReference = FirebaseDatabase.getInstance().getReference(CATEGORIAS_REFERENCE);

        mapFragment = (WorkaroundMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                myScroolView.requestDisallowInterceptTouchEvent(true);

            }
        });


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block


            } else {


                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = location;

                                centerLocationOnScreenFirstTime();
                            }
                        }
                    });
        }


        autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().
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
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(),this)
                .build();


        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMapOnCurrentLocation();
            }
        });


        plusButton =(Button) rootView.findViewById(R.id.plus_button);
        minusButton =(Button) rootView.findViewById(R.id.minus_button);
        partyNumber = (TextView)rootView.findViewById(R.id.agetext);
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




    private void showpDialog() {
        /*if (!pDialog.isShowing())
            pDialog.show();*/
    }

    private void hidepDialog() {
        /*if (pDialog.isShowing())
            pDialog.dismiss();*/
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
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
                            locationFinish.setVisibility(View.VISIBLE);
                            optionsIsFinish();

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
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block


            } else {


                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
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



    class mySpinnerListener implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {

            Toast.makeText(parent.getContext(),
                    "Topic selected : " + parent.getItemAtPosition(position).toString(),
                    Toast.LENGTH_SHORT).show();
            topicSeleccionado = allTopics.get(position);
            topiFinish.setVisibility(View.VISIBLE);
            optionsIsFinish();


        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }

    }



    private void optionsIsFinish(){
        if(dateSelected != null && dateSelected.length() >1 && timeSelected != null && timeSelected.length() >1 && placeSeleccionado != null){
            searchButtonR.setVisibility(View.VISIBLE);
        }
    }






}
