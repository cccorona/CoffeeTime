package mx.com.cesarcorona.coffeetime.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import mx.com.cesarcorona.coffeetime.MainActivity;
import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.TopicsAdapter;
import mx.com.cesarcorona.coffeetime.pojo.Topic;
import mx.com.cesarcorona.coffeetime.pojo.User;
import mx.com.cesarcorona.coffeetime.pojo.UserProfile;

import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.KEY_TOKEN;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.PREFERENCES_KEY;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.USERS_REFERENCE;

public class JustCoffeActivity extends BaseAnimatedActivity implements DatePickerDialog.OnDateSetListener ,
        TimePickerDialog.OnTimeSetListener {


    public static String KEY_DATE = "date";
    public static String KEY_TIME = "time";
    public static final String KEY_TOPIC="topic";
    public static final String KEY_JUST_COFFE ="justcofffe";


    public static String USER_PROFILES_REFERENCE = "profiles";
    private static final String TOPICS_REFERENCE = "topicos";



    private Calendar myCalendar;
    private EditText dateEditText, timeEditText;
    private ImageView dateSelector, timeSelector;
    private String dateSelected, timeSelected;
    private ImageView homeButton, nextButtton;
    private Date fechaEnCalendario;


    private DatabaseReference databaseReference;
    private ProgressDialog pDialog;
    private LinkedList<Topic> allTopics;
    private Spinner topicSelector;
    private Topic topicSeleccionado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUserData();
        setContentView(R.layout.activity_just_coffe);
        databaseReference = FirebaseDatabase.getInstance().getReference(TOPICS_REFERENCE);
        myCalendar = Calendar.getInstance(Locale.getDefault());
        dateEditText = (EditText) findViewById(R.id.date_text);
        timeEditText = (EditText) findViewById(R.id.time_text);
        timeSelector = (ImageView) findViewById(R.id.time_selector);
        dateSelector = (ImageView) findViewById(R.id.date_selector);
        dateSelector.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(JustCoffeActivity.this, JustCoffeActivity.this, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(JustCoffeActivity.this, JustCoffeActivity.this, myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(JustCoffeActivity.this)).show();

            }
        });


        homeButton = (ImageView) findViewById(R.id.home_icon);
        nextButtton = (ImageView) findViewById(R.id.next_icon);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeActivity = new Intent(JustCoffeActivity.this, MainSettingsActivity.class);
                startActivity(homeActivity);
                finish();
            }
        });

        nextButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateSelected == null || timeSelected == null || topicSeleccionado == null) {
                    Toast.makeText(JustCoffeActivity.this, getString(R.string.warning), Toast.LENGTH_LONG).show();
                } else {
                    Bundle extras = new Bundle();
                    extras.putString(KEY_DATE, dateSelected);
                    extras.putString(KEY_TIME, timeSelected);
                    extras.putSerializable(KEY_TOPIC,topicSeleccionado);
                    extras.putSerializable(KEY_JUST_COFFE,true);
                    Intent filterIntent = new Intent(JustCoffeActivity.this, FilterActivity.class);
                    filterIntent.putExtras(extras);
                    startActivity(filterIntent);
                    finish();

                }
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        pDialog = new ProgressDialog(JustCoffeActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        allTopics = new LinkedList<>();
        showpDialog();
        topicSelector = (Spinner) findViewById(R.id.categorySpinner);


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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(JustCoffeActivity.this,
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                topicSelector.setAdapter(dataAdapter);

                hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        topicSelector.setOnItemSelectedListener(new mySpinnerListener());




    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        Date dateOnCAlendar = myCalendar.getTime();
        Date now = Calendar.getInstance().getTime();
        fechaEnCalendario = dateOnCAlendar;
        if (dateOnCAlendar.after(now) || dateOnCAlendar.equals(now)) {
            dateEditText.setText(sdf.format(myCalendar.getTime()));
            dateSelected = dateEditText.getText().toString();
        } else {
            Toast.makeText(this, "You have to select a future Date", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

        Calendar rightNow = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        int hourNow = rightNow.get(Calendar.HOUR_OF_DAY);
        boolean isPassedTime = false;
        if (hourNow > hourOfDay) {
            isPassedTime = true;
        }
        String aMpM = "AM";
        if (hourOfDay > 11) {
            aMpM = "PM";
        }
        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if (hourOfDay > 11) {
            currentHour = hourOfDay - 12;
        } else {
            currentHour = hourOfDay;
        }

        if (fechaEnCalendario.equals(now)) {
            if (isPassedTime) {
                Toast.makeText(this, "You have to select a future hour", Toast.LENGTH_LONG).show();

            } else {
                timeEditText.setText(String.valueOf(currentHour)
                        + " : " + String.valueOf(minute) + " " + aMpM);
                timeSelected = timeEditText.getText().toString();
            }
        } else {
            timeEditText.setText(String.valueOf(currentHour)
                    + " : " + String.valueOf(minute) + " " + aMpM);
            timeSelected = timeEditText.getText().toString();
        }


    }


    private void initUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USERS_REFERENCE);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
            String token = sharedPreferences.getString(KEY_TOKEN, "");
            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), token);
            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
        }

        DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference(USER_PROFILES_REFERENCE);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserProfile userProfile = new UserProfile();
            userProfile.setEmail(user.getEmail());
            userProfile.setName(user.getDisplayName());
            profileReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userProfile);
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

        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
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

}