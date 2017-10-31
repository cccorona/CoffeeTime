package mx.com.cesarcorona.coffeetime.pojo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

/**
 * Created by Corona on 8/28/2017.
 */

public class CoffeDate implements Serializable {

    private String time;
    private String placeId;
    private int requestedPlaces;
    private String user1;
    private String user2;
    private double latitud;
    private double longitud;
    private String favoritePlace;
    private Boolean openDate;



    @Exclude
    private UserProfile profile1;
    @Exclude
    private UserProfile profile2;
    @Exclude
    DatabaseReference databaseProfileReference;
    @Exclude
    FillInformationInterface fillInformationInterface;
    @Exclude
    private String dataBaseReference;
    @Exclude
    private String alternatiVeReference;
    @Exclude
    private boolean alternativeFill;


    public interface FillInformationInterface{
        void OnDataChangeSuccess();
        void OnError(String error);
    }


    public CoffeDate(String time, String placeId, String user1) {
        this.time = time;
        this.placeId = placeId;
        this.user1 = user1;
        this.openDate = true;
    }

    public CoffeDate() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getRequestedPlaces() {
        return requestedPlaces;
    }

    public void setRequestedPlaces(int requestedPlaces) {
        this.requestedPlaces = requestedPlaces;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFavoritePlace() {
        return favoritePlace;
    }

    public void setFavoritePlace(String favoritePlace) {
        this.favoritePlace = favoritePlace;
    }

    public UserProfile getProfile1() {
        return profile1;
    }

    public void setProfile1(UserProfile profile1) {
        this.profile1 = profile1;
    }

    public UserProfile getProfile2() {
        return profile2;
    }

    public void setProfile2(UserProfile profile2) {
        this.profile2 = profile2;
    }


    public void setFillInformationInterface(FillInformationInterface fillInformationInterface) {
        this.fillInformationInterface = fillInformationInterface;
    }

    public void fullFillUserProfileWithReference(String databaseReference){
        databaseProfileReference = FirebaseDatabase.getInstance().getReference(databaseReference);
        databaseProfileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                profile1 = userProfile;
                if(fillInformationInterface != null){
                    fillInformationInterface.OnDataChangeSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(fillInformationInterface != null){
                    fillInformationInterface.OnError(databaseError.getMessage());
                }
            }
        });
    }


    public String getDataBaseReference() {
        return dataBaseReference;
    }

    public void setDataBaseReference(String dataBaseReference) {
        this.dataBaseReference = dataBaseReference;
    }

    public Boolean getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Boolean openDate) {
        this.openDate = openDate;
    }


    public String getAlternatiVeReference() {
        return alternatiVeReference;
    }

    public void setAlternatiVeReference(String alternatiVeReference) {
        this.alternatiVeReference = alternatiVeReference;
    }

    public boolean isAlternativeFill() {
        return alternativeFill;
    }

    public void setAlternativeFill(boolean alternativeFill) {
        this.alternativeFill = alternativeFill;
    }
}
