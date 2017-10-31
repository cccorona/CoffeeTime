package mx.com.cesarcorona.coffeetime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.pojo.CoffeDate;

/**
 * Created by ccabrera on 08/10/17.
 */

public class MyDatesAdapter extends BaseAdapter {


    private Context context;
    private LinkedList<CoffeDate> availableDates;
    private MatchingInterface matchingInterface;
    private Date now;


    public void setMatchingInterface(MatchingInterface matchingInterface) {
        this.matchingInterface = matchingInterface;
    }

    public interface MatchingInterface{
        void OnConnectButton(CoffeDate coffeDate);
        void OnReview(CoffeDate coffeDate);
        void OnChat(CoffeDate coffeDate);
        void OnCancelDate(CoffeDate coffeDate);
        void OnDeleteDate(CoffeDate coffeDate);
    }

    public MyDatesAdapter(Context context, LinkedList<CoffeDate> availableDates) {
        this.context = context;
        this.availableDates = availableDates;
        this.matchingInterface = (MatchingInterface) context;
        this.now = Calendar.getInstance().getTime();
    }

    @Override
    public int getCount() {
        return availableDates.size();
    }

    @Override
    public CoffeDate getItem(int position) {
        return availableDates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.my_dates_item_layout,parent,false);
        Button connectButon = (Button) rootView.findViewById(R.id.connect_button);
        connectButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matchingInterface != null){
                    matchingInterface.OnConnectButton(availableDates.get(position));
                }
            }
        });

        ImageView rateButton = (ImageView) rootView.findViewById(R.id.rate_person);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matchingInterface != null){
                    matchingInterface.OnReview(availableDates.get(position));
                }
            }
        });


        TextView dateFromDate = (TextView)rootView.findViewById(R.id.set_time_text);
        dateFromDate.setText(availableDates.get(position).getTime());

        TextView peopleInDate = (TextView)rootView.findViewById(R.id.people_in_date);
        peopleInDate.setText(""+availableDates.get(position).getRequestedPlaces());

        TextView placeOfDate = (TextView)rootView.findViewById(R.id.place_of_date);
        placeOfDate.setText(availableDates.get(position).getFavoritePlace());

        ImageView deleteButton = (ImageView)rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matchingInterface != null){
                    matchingInterface.OnDeleteDate(availableDates.get(position));
                }
            }
        });


        ImageView chatButton = (ImageView) rootView.findViewById(R.id.message_persone);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matchingInterface != null){
                    matchingInterface.OnChat(availableDates.get(position));
                }
            }
        });


        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        String []dateParts = availableDates.get(position).getTime().split(",");


        chatButton.setVisibility(View.VISIBLE);
        String dateCoffe = "";
        if(dateParts != null && dateParts.length >1){
            dateCoffe = dateParts[0];
        }

       // dateCoffe = "10/25/17";


        try {
            Date date = sdf.parse(dateCoffe);
            if(now.before(date)){
               connectButon.setText(R.string.cancel_date);
                connectButon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(matchingInterface != null){
                            matchingInterface.OnCancelDate(availableDates.get(position));
                        }
                    }
                });
            }else{
                connectButon.setText(R.string.rate_this_date);
                connectButon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(matchingInterface != null){
                            matchingInterface.OnConnectButton(availableDates.get(position));
                        }
                    }
                });
                deleteButton.setVisibility(View.VISIBLE);

            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


        TextView personName = (TextView) rootView.findViewById(R.id.person_name);
        personName.setText(availableDates.get(position).getProfile1().getName());





        return rootView;
    }

    public void updateList(CoffeDate coffeDate){
        for(CoffeDate datoToUpdate:availableDates){
            if(datoToUpdate.getDataBaseReference().equalsIgnoreCase(coffeDate.getDataBaseReference())){
                datoToUpdate = coffeDate;
                break;
            }
        }

        notifyDataSetChanged();
    }

    private void cancelDateAction(){

    }

    public void updateData(){
        notifyDataSetChanged();
    }

}
