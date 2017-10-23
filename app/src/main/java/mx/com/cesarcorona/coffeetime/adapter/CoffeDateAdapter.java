package mx.com.cesarcorona.coffeetime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.pojo.CoffeDate;

/**
 * Created by ccabrera on 29/08/17.
 */

public class CoffeDateAdapter extends BaseAdapter {


    private Context context;
    private LinkedList<CoffeDate> availableDates;
    private MatchingInterface matchingInterface;


    public void setMatchingInterface(MatchingInterface matchingInterface) {
        this.matchingInterface = matchingInterface;
    }

    public interface MatchingInterface{
        void OnConnectButton(CoffeDate coffeDate);
        void OnReview(CoffeDate coffeDate);
        void OnChat(CoffeDate coffeDate);
    }

    public CoffeDateAdapter(Context context, LinkedList<CoffeDate> availableDates) {
        this.context = context;
        this.availableDates = availableDates;
        this.matchingInterface = (MatchingInterface) context;
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
        connectButon.setText(context.getString(R.string.connect));
        connectButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matchingInterface != null){
                    matchingInterface.OnConnectButton(availableDates.get(position));
                }
            }
        });

        TextView dateFromDate = (TextView)rootView.findViewById(R.id.set_time_text);
        dateFromDate.setText(availableDates.get(position).getTime());

        TextView peopleInDate = (TextView)rootView.findViewById(R.id.people_in_date);
        peopleInDate.setText(""+availableDates.get(position).getRequestedPlaces());

        TextView placeOfDate = (TextView)rootView.findViewById(R.id.place_of_date);
        placeOfDate.setText(availableDates.get(position).getFavoritePlace());


        ImageView rateButton = (ImageView) rootView.findViewById(R.id.rate_person);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matchingInterface != null){
                    matchingInterface.OnReview(availableDates.get(position));
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

        if(availableDates.get(position).getOpenDate()){
            chatButton.setVisibility(View.GONE);
        }else{
            chatButton.setVisibility(View.VISIBLE);
        }

        TextView personName = (TextView) rootView.findViewById(R.id.person_name);
        personName.setText(availableDates.get(position).getProfile1().getName());

        return rootView;
    }

    public void updateList(CoffeDate coffeDate){
        int pos = 0;
        for(CoffeDate datoToUpdate:availableDates){
            if(datoToUpdate.getDataBaseReference().equalsIgnoreCase(coffeDate.getDataBaseReference())){
                availableDates.get(pos).setOpenDate(false);
                break;
            }
            pos++;
        }


        notifyDataSetChanged();
    }

}
