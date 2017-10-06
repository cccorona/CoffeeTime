package mx.com.cesarcorona.coffeetime.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import mx.com.cesarcorona.coffeetime.MainActivity;
import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.CategoryAdapter;
import mx.com.cesarcorona.coffeetime.fragment.CoffeOptionsFragment;
import mx.com.cesarcorona.coffeetime.fragment.WorkaroundMapFragment;
import mx.com.cesarcorona.coffeetime.pojo.Categoria;
import mx.com.cesarcorona.coffeetime.pojo.Topic;
import mx.com.cesarcorona.coffeetime.pojo.User;
import mx.com.cesarcorona.coffeetime.pojo.UserProfile;

import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.KEY_TOKEN;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.PREFERENCES_KEY;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.USERS_REFERENCE;

public class MainSettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DatePickerDialog.OnDateSetListener ,
        TimePickerDialog.OnTimeSetListener ,CoffeOptionsFragment.OnActionSelectedListener  {



    private  NavigationView navigationView;
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
        setContentView(R.layout.activity_main_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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

      /*  Button logout = (Button) findViewById(R.id.log_out_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });*/


        cooffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coffeOptionsFragment = (CoffeOptionsFragment) getSupportFragmentManager().findFragmentByTag(CoffeOptionsFragment.TAG);
                if(coffeOptionsFragment == null){
                    coffeOptionsFragment = new CoffeOptionsFragment();
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_from_down,0)
                            .add(R.id.fragment_selected_time,coffeOptionsFragment,CoffeOptionsFragment.TAG).commit();
                }else {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_from_right, 0)
                            .replace(R.id.fragment_selected_time, coffeOptionsFragment).commit();
                }
            }
        });

        initUserData();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about_us) {
            // Handle the camera action
        } else if (id == R.id.nav_contact_us) {

        } else if (id == R.id.nav_support) {

        } else if (id == R.id.nav_q_a) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setUpProfileDrawerMenu(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        TextView nombrePerfil = (TextView)navigationView.getHeaderView(0).findViewById(R.id.text_view_nombre);
        TextView correoPerfil = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textView_correo_suaurio);

        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();

        nombrePerfil.setText(currentUser.getDisplayName());
        correoPerfil.setText(currentUser.getEmail());


    }



    private void checkDatesActivity(){
        Intent intent = new Intent(MainSettingsActivity.this,JustCoffeActivity.class);
        startActivity(intent);
    }

    private void meriendaActivity(){
        Intent intent= new Intent(MainSettingsActivity.this, ReviewActivity.class);
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
