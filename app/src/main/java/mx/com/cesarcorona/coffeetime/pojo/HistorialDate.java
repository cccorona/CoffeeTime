package mx.com.cesarcorona.coffeetime.pojo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.USER_PROFILES_REFERENCE;

/**
 * Created by ccabrera on 29/10/17.
 */

public class HistorialDate implements Serializable {

    private String dateReference;
    @Exclude
    private CoffeDate date;

    private FillDateInterface fillDateInterface;

    public void setFillDateInterface(FillDateInterface fillDateInterface) {
        this.fillDateInterface = fillDateInterface;
    }

    public interface FillDateInterface{
        void OnDateFilled(CoffeDate coffeDate);
    }


    public HistorialDate() {
    }


    public String getDateReference() {
        return dateReference;
    }

    public void setDateReference(String dateReference) {
        this.dateReference = dateReference;
    }


    public CoffeDate getDate() {
        return date;
    }

    public void setDate(CoffeDate date) {
        this.date = date;
    }

    public void fillDAte(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(dateReference);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(fillDateInterface != null){
                    final CoffeDate coffeDate = dataSnapshot.getValue(CoffeDate.class);
                    coffeDate.setDataBaseReference(dataSnapshot.getKey());
                    coffeDate.setAlternativeFill(true);
                    coffeDate.setAlternatiVeReference(dataSnapshot.getRef().toString());
                    fillDateInterface.OnDateFilled(coffeDate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
