package mx.com.cesarcorona.coffeetime.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import mx.com.cesarcorona.coffeetime.MainActivity;
import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.pojo.User;
import mx.com.cesarcorona.coffeetime.pojo.UserProfile;

import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.KEY_TOKEN;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.PREFERENCES_KEY;
import static mx.com.cesarcorona.coffeetime.services.MyFirebaseInstanceIDService.USERS_REFERENCE;

public class CoffeTimeActiviy extends AppCompatActivity implements DatePickerDialog.OnDateSetListener ,
        TimePickerDialog.OnTimeSetListener{



    public static String KEY_DATE ="date";
    public static String KEY_TIME ="time";

    public static String USER_PROFILES_REFERENCE = "profiles";


    private Calendar myCalendar;
    private EditText dateEditText , timeEditText;
    private ImageView dateSelector , timeSelector;
    private String dateSelected , timeSelected;
    private ImageView homeButton ,nextButtton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUserData();
        setContentView(R.layout.activity_coffe_time_activiy);
        myCalendar = Calendar.getInstance(Locale.getDefault());
        dateEditText= (EditText) findViewById(R.id.date_text);
        timeEditText = (EditText) findViewById(R.id.time_text);
        timeSelector = (ImageView)findViewById(R.id.time_selector);
        dateSelector = (ImageView)findViewById(R.id.date_selector);
        dateSelector.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(CoffeTimeActiviy.this, CoffeTimeActiviy.this, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(CoffeTimeActiviy.this,CoffeTimeActiviy.this, myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(CoffeTimeActiviy.this)).show();

            }
        });


        homeButton = (ImageView) findViewById(R.id.home_icon);
        nextButtton = (ImageView) findViewById(R.id.next_icon);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeActivity = new Intent(CoffeTimeActiviy.this, MainActivity.class);
                startActivity(homeActivity);
                finish();
            }
        });

        nextButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(dateSelected == null || timeSelected == null){
                     Toast.makeText(CoffeTimeActiviy.this,getString(R.string.warning),Toast.LENGTH_LONG).show();
                 }else{
                     Bundle extras = new Bundle();
                     extras.putString(KEY_DATE,dateSelected);
                     extras.putString(KEY_TIME,timeSelected);
                     Intent filterIntent = new Intent(CoffeTimeActiviy.this,FilterActivity.class);
                     filterIntent.putExtras(extras);
                     startActivity(filterIntent);
                     finish();

                 }
            }
        });




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
        dateEditText.setText(sdf.format(myCalendar.getTime()));
        dateSelected = dateEditText.getText().toString();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
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

        //Display the user changed time on TextView
        timeEditText.setText(String.valueOf(currentHour)
                + " : " + String.valueOf(minute) + " " + aMpM + "\n");
        timeSelected = timeEditText.getText().toString();
    }


    private void initUserData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USERS_REFERENCE);
        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_KEY,MODE_PRIVATE);
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

}
