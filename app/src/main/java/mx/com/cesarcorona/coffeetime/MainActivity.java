package mx.com.cesarcorona.coffeetime;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy;
import mx.com.cesarcorona.coffeetime.activities.JustCoffeActivity;
import mx.com.cesarcorona.coffeetime.activities.ReviewActivity;
import mx.com.cesarcorona.coffeetime.adapter.CategoryAdapter;
import mx.com.cesarcorona.coffeetime.fragment.CoffeOptionsFragment;
import mx.com.cesarcorona.coffeetime.fragment.WorkaroundMapFragment;
import mx.com.cesarcorona.coffeetime.pojo.Categoria;
import mx.com.cesarcorona.coffeetime.pojo.Topic;
import mx.com.cesarcorona.coffeetime.pojo.User;
import mx.com.cesarcorona.coffeetime.pojo.UserProfile;
import noman.googleplaces.PlacesListener;

import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.KEY_TOKEN;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.PREFERENCES_KEY;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.USERS_REFERENCE;

public class MainActivity extends AppCompatActivity implements   DatePickerDialog.OnDateSetListener ,
        TimePickerDialog.OnTimeSetListener ,CoffeOptionsFragment.OnActionSelectedListener {


    public static String USER_PROFILES_REFERENCE = "profiles";
    public static String TAG = MainActivity.class.getSimpleName();

    private CardView cooffee, merienda;
    private LinearLayout backgroundCoffe, backgroundMeal;
    private CoffeOptionsFragment coffeOptionsFragment;
    private String dateSelected , timeSelected;
    private Date fechaEnCalendario;
    private Calendar myCalendar;



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








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        myCalendar = Calendar.getInstance(Locale.getDefault());
        cooffee = (CardView) findViewById(R.id.card_view_cofee);
        merienda = (CardView) findViewById(R.id.card_view_merienda);
        backgroundCoffe = (LinearLayout) findViewById(R.id.linear_background_coffe);
        backgroundMeal = (LinearLayout) findViewById(R.id.linear_background_meal);


       /* cooffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDatesActivity();
            }
        });

        merienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meriendaActivity();
            }
        });*/

        Button logout = (Button) findViewById(R.id.log_out_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });


        cooffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coffeOptionsFragment = (CoffeOptionsFragment) getSupportFragmentManager().findFragmentByTag(CoffeOptionsFragment.TAG);
                if(coffeOptionsFragment == null){
                    coffeOptionsFragment = new CoffeOptionsFragment();
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_from_down,0)
                            .add(R.id.fragment_selected_time,coffeOptionsFragment,CoffeOptionsFragment.TAG).commit();
                }else {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_from_right, 0)
                            .replace(R.id.fragment_selected_time, coffeOptionsFragment).commit();
                }
            }
        });

        initUserData();


    }

    private void checkDatesActivity(){
        Intent intent = new Intent(MainActivity.this,JustCoffeActivity.class);
        startActivity(intent);
    }

    private void meriendaActivity(){
        Intent intent= new Intent(MainActivity.this, ReviewActivity.class);
        Bundle extras = new Bundle();
        extras.putString(ReviewActivity.KEY_USER,FirebaseAuth.getInstance().getCurrentUser().getUid());
        intent.putExtras(extras);
        startActivity(intent);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    }



    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        Date dateOnCAlendar =myCalendar.getTime() ;
        Date now =Calendar.getInstance().getTime();
        fechaEnCalendario = dateOnCAlendar;
        if(dateOnCAlendar.after(now) || dateOnCAlendar.equals(now)){
            coffeOptionsFragment = (CoffeOptionsFragment) getSupportFragmentManager().findFragmentByTag(CoffeOptionsFragment.TAG);
            coffeOptionsFragment.updateLabel(sdf.format(myCalendar.getTime()));
            dateSelected = sdf.format(myCalendar.getTime());
        }else{
            Toast.makeText(this,"You have to select a future Date",Toast.LENGTH_LONG).show();
        }

        if(dateSelected != null && dateSelected.length() >0 && timeSelected != null && timeSelected.length()>0){
            coffeOptionsFragment = (CoffeOptionsFragment) getSupportFragmentManager().findFragmentByTag(CoffeOptionsFragment.TAG);
            coffeOptionsFragment.finishOption(CoffeOptionsFragment.DATE_OPTION);
        }

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar rightNow = Calendar.getInstance();
        Date now =Calendar.getInstance().getTime();
        int hourNow = rightNow.get(Calendar.HOUR_OF_DAY);
        boolean isPassedTime = false;
        if(hourNow>hourOfDay){
            isPassedTime = true;
        }
        String aMpM = "AM";
        if(hourOfDay >11)
        {
            aMpM = "PM";
        }
        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if(hourOfDay>11)
        {
            currentHour = hourOfDay - 12;
        }
        else
        {
            currentHour = hourOfDay;
        }
        coffeOptionsFragment = (CoffeOptionsFragment) getSupportFragmentManager().findFragmentByTag(CoffeOptionsFragment.TAG);

        if(fechaEnCalendario.equals(now)){
            if(isPassedTime){
                Toast.makeText(this,"You have to select a future hour",Toast.LENGTH_LONG).show();

            }else{
                timeSelected = String.valueOf(currentHour)
                        + " : " + String.valueOf(minute) + " " + aMpM;
                coffeOptionsFragment.updateTimeLabel(timeSelected);

            }
        }else{

            timeSelected = String.valueOf(currentHour)
                    + " : " + String.valueOf(minute) + " " + aMpM;
            coffeOptionsFragment.updateTimeLabel(timeSelected);

        }

        if(dateSelected != null && dateSelected.length() >0 && timeSelected != null && timeSelected.length()>0){
            coffeOptionsFragment = (CoffeOptionsFragment) getSupportFragmentManager().findFragmentByTag(CoffeOptionsFragment.TAG);
            coffeOptionsFragment.finishOption(CoffeOptionsFragment.DATE_OPTION);
        }


    }

    private void initUserData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USERS_REFERENCE);
        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
            SharedPreferences sharedPreferences =getSharedPreferences(PREFERENCES_KEY,MODE_PRIVATE);
            String token = sharedPreferences.getString(KEY_TOKEN,"");
            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),token);
            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
        }

        DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference(USER_PROFILES_REFERENCE);
        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserProfile userProfile = new UserProfile();
            userProfile.setEmail(user.getEmail());
            userProfile.setName(user.getDisplayName());
            profileReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userProfile);
        }
    }

    @Override
    public void OnSelectedOption(int optionSelected) {

    }

    @Override
    public void OnFirstOptionSelected() {
        cooffee.setVisibility(View.GONE);
    }
}
