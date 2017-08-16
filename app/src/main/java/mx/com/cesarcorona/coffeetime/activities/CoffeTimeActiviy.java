package mx.com.cesarcorona.coffeetime.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import mx.com.cesarcorona.coffeetime.R;

public class CoffeTimeActiviy extends AppCompatActivity implements DatePickerDialog.OnDateSetListener ,
        TimePickerDialog.OnTimeSetListener{


    private Calendar myCalendar;
    private EditText dateEditText , timeEditText;
    private ImageView dateSelector , timeSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
}